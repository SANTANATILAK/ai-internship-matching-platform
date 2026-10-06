package com.tilak.internship_platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tilak.internship_platform.dto.response.AtsScoreResponse;
import com.tilak.internship_platform.entity.AtsResult;
import com.tilak.internship_platform.entity.Resume;
import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.exception.ResourceNotFoundException;
import com.tilak.internship_platform.parser.ResumeAnalyzer.AnalyzedResumeData;
import com.tilak.internship_platform.repository.AtsResultRepository;
import com.tilak.internship_platform.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AtsService {

    private final AtsResultRepository atsResultRepository;
    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;

    public AtsService(AtsResultRepository atsResultRepository,
                      ResumeRepository resumeRepository,
                      ObjectMapper objectMapper) {
        this.atsResultRepository = atsResultRepository;
        this.resumeRepository = resumeRepository;
        this.objectMapper = objectMapper;
    }

    public static class AtsEvaluationResult {
        public int score;
        public List<String> strengths = new ArrayList<>();
        public List<String> weaknesses = new ArrayList<>();
        public List<String> missingSections = new ArrayList<>();
        public List<String> suggestions = new ArrayList<>();
        public List<String> detectedKeywords = new ArrayList<>();
    }

    public AtsEvaluationResult evaluateResume(String rawText, AnalyzedResumeData parsed) {
        AtsEvaluationResult res = new AtsEvaluationResult();
        int totalScore = 0;
        String lower = rawText.toLowerCase();

        // 1. Contact Information Completeness (Max 10 pts)
        int contactScore = 0;
        if (parsed.getEmail() != null) contactScore += 4;
        if (parsed.getPhone() != null) contactScore += 3;
        if (parsed.getName() != null && !parsed.getName().equalsIgnoreCase("Candidate")) contactScore += 3;
        totalScore += contactScore;

        if (contactScore == 10) {
            res.strengths.add("Complete and well-formatted contact details (Email, Phone, Name)");
        } else {
            res.weaknesses.add("Incomplete contact information. Ensure phone, professional email, and name are distinct");
            res.suggestions.add("Add a clean contact header with your name, phone, email, and LinkedIn/GitHub links");
        }

        // 2. Education & Graduation Year (Max 15 pts)
        int eduScore = 0;
        if (parsed.getEducationDetails() != null && !parsed.getEducationDetails().isEmpty()) eduScore += 8;
        if (parsed.getGraduationYear() != null) eduScore += 7;
        totalScore += eduScore;

        if (eduScore >= 12) {
            res.strengths.add("Clear educational background with recognizable graduation year (" + parsed.getGraduationYear() + ")");
        } else {
            res.missingSections.add("Education / Graduation Year");
            res.suggestions.add("Clearly state your college name, degree (e.g. B.Tech Computer Science), and expected graduation year");
        }

        // 3. Technical Skills Breadth & Depth (Max 25 pts)
        int skillCount = parsed.getSkills() != null ? parsed.getSkills().size() : 0;
        int skillScore = Math.min(25, skillCount * 3);
        totalScore += skillScore;
        if (parsed.getSkills() != null) {
            res.detectedKeywords.addAll(parsed.getSkills());
        }

        if (skillCount >= 7) {
            res.strengths.add("Robust set of " + skillCount + " modern technical skills detected");
        } else {
            res.weaknesses.add("Limited number of technical keywords detected (" + skillCount + " found)");
            res.suggestions.add("Add specialized core competencies like Database (SQL, MongoDB), Version Control (Git), and Cloud/API tools");
        }

        // 4. Projects & Technical Applications (Max 20 pts)
        int projectScore = 0;
        if (lower.contains("project") || lower.contains("projects")) {
            projectScore += 10;
            if (lower.contains("http") || lower.contains("github.com") || lower.contains("live") || lower.contains("demo")) {
                projectScore += 5;
            }
            if (lower.contains("%") || lower.contains("increased") || lower.contains("reduced") || lower.contains("optimized") || lower.contains("achieved")) {
                projectScore += 5;
            }
        }
        totalScore += projectScore;

        if (projectScore >= 15) {
            res.strengths.add("Strong project descriptions with measurable outcomes and deployment links");
        } else {
            res.suggestions.add("Use the STAR method (Situation, Task, Action, Result) and include metrics in your project bullets (e.g. 'Improved efficiency by 25%')");
        }

        // 5. Work Experience / Internships (Max 15 pts)
        int expScore = 0;
        if (lower.contains("experience") || lower.contains("internship") || lower.contains("work experience")) {
            expScore += 10;
            if (parsed.getExperienceYears() != null && parsed.getExperienceYears() > 0) {
                expScore += 5;
            }
        }
        totalScore += expScore;

        if (expScore >= 10) {
            res.strengths.add("Relevant internship or professional experience clearly highlighted");
        } else {
            res.missingSections.add("Internship / Professional Experience");
            res.suggestions.add("Include open-source contributions, hackathons, or freelance work if you do not have formal company internships yet");
        }

        // 6. Certifications & Extras (Max 15 pts)
        int certScore = 0;
        if (parsed.getCertifications() != null && !parsed.getCertifications().isEmpty()) {
            certScore += 10;
            res.strengths.add("Industry certifications detected (" + parsed.getCertifications().size() + " verified)");
        } else if (lower.contains("certificate") || lower.contains("certification") || lower.contains("achievement")) {
            certScore += 5;
        }
        if (lower.contains("git") || lower.contains("github") || lower.contains("leetcode") || lower.contains("hackerrank")) {
            certScore += 5;
        }
        certScore = Math.min(15, certScore);
        totalScore += certScore;

        if (certScore < 8) {
            res.suggestions.add("Add verified cloud or AI certifications (e.g. AWS Cloud Practitioner, Microsoft Azure, Coursera Deep Learning)");
        }

        // Ensure score is capped between 0 and 100
        res.score = Math.max(15, Math.min(100, totalScore));

        return res;
    }

    @Transactional
    public AtsResult saveAtsEvaluation(User user, Resume resume, AtsEvaluationResult eval) {
        try {
            AtsResult result = AtsResult.builder()
                    .user(user)
                    .resume(resume)
                    .atsScore(eval.score)
                    .strengthsJson(objectMapper.writeValueAsString(eval.strengths))
                    .weaknessesJson(objectMapper.writeValueAsString(eval.weaknesses))
                    .missingSectionsJson(objectMapper.writeValueAsString(eval.missingSections))
                    .suggestionsJson(objectMapper.writeValueAsString(eval.suggestions))
                    .detectedKeywordsJson(objectMapper.writeValueAsString(eval.detectedKeywords))
                    .build();

            return atsResultRepository.save(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing ATS evaluation to JSON", e);
        }
    }

    @Transactional(readOnly = true)
    public AtsScoreResponse getLatestAtsScore(Long userId) {
        AtsResult result = atsResultRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No ATS evaluation found. Please upload a resume first."));

        return mapToResponse(result);
    }

    @Transactional(readOnly = true)
    public List<AtsScoreResponse> getAtsHistory(Long userId) {
        List<AtsResult> list = atsResultRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<AtsScoreResponse> responses = new ArrayList<>();
        for (AtsResult ar : list) {
            responses.add(mapToResponse(ar));
        }
        return responses;
    }

    private AtsScoreResponse mapToResponse(AtsResult r) {
        try {
            List<String> strengths = r.getStrengthsJson() != null ?
                    objectMapper.readValue(r.getStrengthsJson(), new TypeReference<>() {}) : Collections.emptyList();
            List<String> weaknesses = r.getWeaknessesJson() != null ?
                    objectMapper.readValue(r.getWeaknessesJson(), new TypeReference<>() {}) : Collections.emptyList();
            List<String> missing = r.getMissingSectionsJson() != null ?
                    objectMapper.readValue(r.getMissingSectionsJson(), new TypeReference<>() {}) : Collections.emptyList();
            List<String> suggestions = r.getSuggestionsJson() != null ?
                    objectMapper.readValue(r.getSuggestionsJson(), new TypeReference<>() {}) : Collections.emptyList();
            List<String> keywords = r.getDetectedKeywordsJson() != null ?
                    objectMapper.readValue(r.getDetectedKeywordsJson(), new TypeReference<>() {}) : Collections.emptyList();

            return AtsScoreResponse.builder()
                    .id(r.getId())
                    .resumeId(r.getResume() != null ? r.getResume().getId() : null)
                    .atsScore(r.getAtsScore())
                    .strengths(strengths)
                    .weaknesses(weaknesses)
                    .missingSections(missing)
                    .suggestions(suggestions)
                    .detectedKeywords(keywords)
                    .createdAt(r.getCreatedAt())
                    .build();
        } catch (Exception e) {
            return AtsScoreResponse.builder()
                    .id(r.getId())
                    .atsScore(r.getAtsScore())
                    .createdAt(r.getCreatedAt())
                    .build();
        }
    }
}
