package com.tilak.internship_platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tilak.internship_platform.dto.response.ResumeAnalysisResponse;
import com.tilak.internship_platform.entity.Resume;
import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.exception.ResourceNotFoundException;
import com.tilak.internship_platform.parser.PdfResumeParser;
import com.tilak.internship_platform.parser.ResumeAnalyzer;
import com.tilak.internship_platform.parser.ResumeAnalyzer.AnalyzedResumeData;
import com.tilak.internship_platform.repository.ResumeRepository;
import com.tilak.internship_platform.repository.UserRepository;
import com.tilak.internship_platform.util.FileStorageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Service
public class ResumeService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);

    private final FileStorageUtil fileStorageUtil;
    private final PdfResumeParser pdfResumeParser;
    private final ResumeAnalyzer resumeAnalyzer;
    private final AtsService atsService;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public ResumeService(FileStorageUtil fileStorageUtil,
                         PdfResumeParser pdfResumeParser,
                         ResumeAnalyzer resumeAnalyzer,
                         AtsService atsService,
                         ResumeRepository resumeRepository,
                         UserRepository userRepository,
                         ObjectMapper objectMapper) {
        this.fileStorageUtil = fileStorageUtil;
        this.pdfResumeParser = pdfResumeParser;
        this.resumeAnalyzer = resumeAnalyzer;
        this.atsService = atsService;
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ResumeAnalysisResponse uploadAndProcessResume(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 1. Store file safely
        String storedFilename = fileStorageUtil.storeFile(file);
        File pdfFile = fileStorageUtil.getFilePath(storedFilename).toFile();

        // 2. Parse text from PDF
        String extractedText = pdfResumeParser.extractText(pdfFile);

        // 3. Analyze resume
        AnalyzedResumeData analysis = resumeAnalyzer.analyze(extractedText);

        // 4. Calculate ATS score
        AtsService.AtsEvaluationResult atsEval = atsService.evaluateResume(extractedText, analysis);

        // 5. Serialize lists
        String skillsJson = "";
        String projectsJson = "";
        String certsJson = "";
        try {
            skillsJson = objectMapper.writeValueAsString(analysis.getSkills());
            projectsJson = objectMapper.writeValueAsString(analysis.getProjects());
            certsJson = objectMapper.writeValueAsString(analysis.getCertifications());
        } catch (JsonProcessingException e) {
            logger.warn("Failed to serialize resume fields: {}", e.getMessage());
        }

        // 6. Save Resume entity
        Resume resume = Resume.builder()
                .user(user)
                .originalFilename(file.getOriginalFilename())
                .storedFilename(storedFilename)
                .extractedText(extractedText)
                .parsedSkills(skillsJson)
                .educationDetails(analysis.getEducationDetails())
                .graduationYear(analysis.getGraduationYear())
                .experienceYears(analysis.getExperienceYears())
                .parsedProjects(projectsJson)
                .parsedCertifications(certsJson)
                .atsScore(atsEval.score)
                .build();

        resume = resumeRepository.save(resume);

        // 7. Save ATS Result
        atsService.saveAtsEvaluation(user, resume, atsEval);

        // 8. Auto-update user graduation year if not previously present
        if (user.getGraduationYear() == null && analysis.getGraduationYear() != null) {
            user.setGraduationYear(analysis.getGraduationYear());
            userRepository.save(user);
        }

        return ResumeAnalysisResponse.builder()
                .resumeId(resume.getId())
                .originalFilename(resume.getOriginalFilename())
                .extractedName(analysis.getName())
                .extractedEmail(analysis.getEmail() != null ? analysis.getEmail() : user.getEmail())
                .extractedPhone(analysis.getPhone() != null ? analysis.getPhone() : user.getPhone())
                .extractedSkills(analysis.getSkills())
                .educationDetails(analysis.getEducationDetails())
                .graduationYear(analysis.getGraduationYear())
                .experienceYears(analysis.getExperienceYears())
                .extractedProjects(analysis.getProjects())
                .extractedCertifications(analysis.getCertifications())
                .atsScore(atsEval.score)
                .strengths(atsEval.strengths)
                .weaknesses(atsEval.weaknesses)
                .missingSections(atsEval.missingSections)
                .suggestions(atsEval.suggestions)
                .detectedKeywords(atsEval.detectedKeywords)
                .uploadedAt(resume.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Resume getLatestResume(Long userId) {
        return resumeRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No resume found for user. Please upload your resume."));
    }

    @Transactional(readOnly = true)
    public Resume getResumeById(Long resumeId) {
        return resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId));
    }
}
