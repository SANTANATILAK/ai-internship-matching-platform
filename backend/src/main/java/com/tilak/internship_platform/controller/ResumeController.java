package com.tilak.internship_platform.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.Principal;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tilak.internship_platform.entity.Resume;
import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.repository.ResumeRepository;
import com.tilak.internship_platform.repository.UserRepository;
import com.tilak.internship_platform.service.MatchingService;
import com.tilak.internship_platform.service.PdfService;
import com.tilak.internship_platform.service.SkillService;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:5174",
        "http://127.0.0.1:5175"
})
@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeRepository repository;
    private final PdfService pdfService;
    private final SkillService skillService;
    private final UserRepository userRepository;
    private final MatchingService matchingService;

    private static final List<String> COMMON_CORE_SKILLS = List.of(
            "Git", "Docker", "AWS", "REST APIs", "SQL", "Linux", "System Design", "Agile", "Unit Testing", "Kubernetes"
    );

    public ResumeController(
            ResumeRepository repository,
            PdfService pdfService,
            SkillService skillService,
            UserRepository userRepository,
            MatchingService matchingService) {
        this.repository = repository;
        this.pdfService = pdfService;
        this.skillService = skillService;
        this.userRepository = userRepository;
        this.matchingService = matchingService;
    }

    @PostMapping("/upload")
    public Map<String, Object> uploadResume(
            @RequestParam(required = false) Long userId,
            @RequestParam("file") MultipartFile file,
            Principal principal) throws Exception {

        Long targetUserId = userId;
        if (targetUserId == null && principal != null) {
            try {
                targetUserId = Long.valueOf(principal.getName());
            } catch (Exception ignored) {
                java.util.Optional<User> u = userRepository.findByEmail(principal.getName());
                if (u.isPresent()) targetUserId = u.get().getId();
            }
        }
        if (targetUserId == null) {
            throw new IllegalArgumentException("User ID is required to associate resume.");
        }

        String originalFilename = file.getOriginalFilename() == null
                ? ""
                : file.getOriginalFilename().toLowerCase();
        if (file.isEmpty() || !(originalFilename.endsWith(".pdf")
                || originalFilename.endsWith(".docx"))) {
            throw new IllegalArgumentException("Upload a non-empty PDF or DOCX resume.");
        }
        if (file.getSize() > 10L * 1024 * 1024) {
            throw new IllegalArgumentException("Resume must be 10 MB or smaller.");
        }

        String extractedText = pdfService.extractText(file);
        List<String> skills = skillService.extractSkills(extractedText);
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Student account was not found."));

        Map<String, Object> atsAnalysis = analyzeResume(extractedText, skills);

        Resume resume = new Resume();
        resume.setUserId(targetUserId);
        resume.setFileName(file.getOriginalFilename());
        resume.setExtractedText(extractedText);
        resume.setAtsScore((Integer) atsAnalysis.get("atsScore"));
        resume.setStrengths(String.join("; ", (List<String>) atsAnalysis.get("strengths")));
        resume.setWeaknesses(String.join("; ", (List<String>) atsAnalysis.get("weaknesses")));
        resume.setMissingSkills(String.join(", ", (List<String>) atsAnalysis.get("missingSkills")));
        resume.setSuggestions(String.join("; ", (List<String>) atsAnalysis.get("suggestions")));
        resume.setDetectedSkills(String.join(", ", skills));
        resume.setDetectedEducation((String) atsAnalysis.get("detectedEducation"));
        resume.setDetectedGraduationYear((Integer) atsAnalysis.get("detectedGraduationYear"));
        resume.setUploadedAt(LocalDateTime.now());

        Resume savedResume = repository.save(resume);

        var matches = matchingService.matchSkills(
                skills,
                user.getGraduationYear(),
                user.getBranch());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("resumeId", savedResume.getId());
        response.put("fileName", savedResume.getFileName());
        response.put("skills", skills);
        response.put("detectedKeywords", skills);
        response.put("atsScore", savedResume.getAtsScore());
        response.put("strengths", atsAnalysis.get("strengths"));
        response.put("weaknesses", atsAnalysis.get("weaknesses"));
        response.put("missingSkills", atsAnalysis.get("missingSkills"));
        response.put("missingSections", atsAnalysis.get("missingSkills"));
        response.put("suggestions", atsAnalysis.get("suggestions"));
        response.put("detectedEducation", atsAnalysis.get("detectedEducation"));
        response.put("detectedGraduationYear", atsAnalysis.get("detectedGraduationYear"));
        response.put("graduationYear", user.getGraduationYear() == null ? "Not set" : user.getGraduationYear());
        response.put("branch", user.getBranch() == null ? "Not set" : user.getBranch());
        response.put("matches", matches);

        Map<String, Object> dataCopy = new HashMap<>(response);
        response.put("data", dataCopy);

        return response;
    }

    @GetMapping({"/latest", "/ats/latest"})
    public Map<String, Object> getLatestResumeFromPrincipal(Principal principal) {
        if (principal == null) {
            return Map.of("success", false, "hasResume", false);
        }
        Long targetUserId = null;
        try {
            targetUserId = Long.valueOf(principal.getName());
        } catch (Exception e) {
            java.util.Optional<User> u = userRepository.findByEmail(principal.getName());
            if (u.isPresent()) targetUserId = u.get().getId();
        }
        if (targetUserId == null) {
            return Map.of("success", false, "hasResume", false);
        }
        Map<String, Object> res = getLatestResume(targetUserId);
        Map<String, Object> out = new HashMap<>(res);
        out.put("success", true);
        out.put("data", res);
        return out;
    }

    @GetMapping("/user/{userId}")
    public Map<String, Object> getLatestResume(@PathVariable Long userId) {
        List<Resume> userResumes = repository.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .toList();

        if (userResumes.isEmpty()) {
            return Map.of("hasResume", false);
        }

        Resume latest = userResumes.get(userResumes.size() - 1);
        User user = userRepository.findById(userId).orElse(null);

        List<String> skills = latest.getDetectedSkills() != null && !latest.getDetectedSkills().isBlank()
                ? Arrays.stream(latest.getDetectedSkills().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .toList()
                : skillService.extractSkills(latest.getExtractedText());

        var matches = matchingService.matchSkills(
                skills,
                user != null ? user.getGraduationYear() : null,
                user != null ? user.getBranch() : null);

        Map<String, Object> response = new HashMap<>();
        response.put("hasResume", true);
        response.put("resumeId", latest.getId());
        response.put("fileName", latest.getFileName());
        response.put("skills", skills);
        response.put("atsScore", latest.getAtsScore() != null ? latest.getAtsScore() : 75);
        response.put("strengths", latest.getStrengths() != null ? Arrays.asList(latest.getStrengths().split("; ")) : List.of());
        response.put("weaknesses", latest.getWeaknesses() != null ? Arrays.asList(latest.getWeaknesses().split("; ")) : List.of());
        response.put("missingSkills", latest.getMissingSkills() != null ? Arrays.asList(latest.getMissingSkills().split(", ")) : List.of());
        response.put("suggestions", latest.getSuggestions() != null ? Arrays.asList(latest.getSuggestions().split("; ")) : List.of());
        response.put("detectedEducation", latest.getDetectedEducation());
        response.put("detectedGraduationYear", latest.getDetectedGraduationYear());
        response.put("uploadedAt", latest.getUploadedAt());
        response.put("graduationYear", user != null && user.getGraduationYear() != null ? user.getGraduationYear() : "Not set");
        response.put("branch", user != null && user.getBranch() != null ? user.getBranch() : "Not set");
        response.put("matches", matches);

        return response;
    }

    private Map<String, Object> analyzeResume(String text, List<String> skills) {
        if (text == null) text = "";
        String lower = text.toLowerCase(Locale.ROOT);

        int score = 0;
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        // 1. Contact Information
        boolean hasEmail = lower.contains("@") && (lower.contains(".com") || lower.contains(".edu") || lower.contains(".in") || lower.contains(".org"));
        boolean hasPhone = Pattern.compile("(\\+?\\d{1,3}[- ]?)?\\d{10}").matcher(text).find();
        boolean hasLinks = lower.contains("linkedin.com") || lower.contains("github.com") || lower.contains("portfolio");

        if (hasEmail) score += 5;
        if (hasPhone) score += 5;
        if (hasLinks) {
            score += 5;
            strengths.add("Professional profiles (LinkedIn / GitHub) detected");
        } else {
            weaknesses.add("No GitHub or LinkedIn profile link found");
            suggestions.add("Add clickable links to your GitHub profile and LinkedIn account to increase credibility");
        }

        // 2. Core Resume Sections
        boolean hasEducation = lower.contains("education") || lower.contains("academic") || lower.contains("b.tech") || lower.contains("degree");
        boolean hasExperience = lower.contains("experience") || lower.contains("internship") || lower.contains("work history");
        boolean hasProjects = lower.contains("project") || lower.contains("projects");
        boolean hasCertifications = lower.contains("certification") || lower.contains("certifications") || lower.contains("course");

        if (hasEducation) {
            score += 10;
            strengths.add("Dedicated Education and academic section identified");
        } else {
            weaknesses.add("Education section heading could not be clearly verified");
            suggestions.add("Use a standard 'Education' heading for easy ATS categorization");
        }

        if (hasProjects) {
            score += 10;
            strengths.add("Project portfolio section clearly documented");
        } else {
            weaknesses.add("Missing clear 'Projects' section");
            suggestions.add("Add 2-3 technical projects with repository links and tech stack descriptions");
        }

        if (hasExperience) {
            score += 10;
            strengths.add("Internship or work experience section present");
        } else {
            suggestions.add("Include open-source contributions or academic internship experience");
        }

        if (hasCertifications) {
            score += 5;
            strengths.add("Professional certifications or courses detected");
        }

        // 3. Technical Skills Density
        if (skills.size() >= 10) {
            score += 25;
            strengths.add("Rich technical skill variety (" + skills.size() + " skills recognized)");
        } else if (skills.size() >= 6) {
            score += 18;
            strengths.add("Good baseline of " + skills.size() + " core skills detected");
        } else if (skills.size() >= 3) {
            score += 12;
            weaknesses.add("Relatively low skill density (" + skills.size() + " detected)");
            suggestions.add("List all technical libraries, databases, and frameworks explicitly in a 'Skills' section");
        } else {
            score += 5;
            weaknesses.add("Very few technical skills identified");
            suggestions.add("Add a comprehensive skills section detailing programming languages, tools, and databases");
        }

        // Find missing standard industry skills
        for (String expected : COMMON_CORE_SKILLS) {
            boolean present = skills.stream().anyMatch(s -> s.equalsIgnoreCase(expected));
            if (!present && !lower.contains(expected.toLowerCase(Locale.ROOT))) {
                missingSkills.add(expected);
            }
        }
        if (!missingSkills.isEmpty()) {
            weaknesses.add("Missing common engineering competencies: " + String.join(", ", missingSkills.subList(0, Math.min(4, missingSkills.size()))));
        }

        // 4. Quantifiable Metrics & Action Verbs
        boolean hasNumbers = Pattern.compile("\\b(\\d{1,3}%|\\d{2,5}\\+|reduced|improved|built|developed|optimized|implemented)\\b", Pattern.CASE_INSENSITIVE).matcher(text).find();
        if (hasNumbers) {
            score += 15;
            strengths.add("Demonstrated measurable impact using action verbs and quantifiable results");
        } else {
            weaknesses.add("Few measurable outcomes or metrics in project bullet points");
            suggestions.add("Use the STAR method: quantify your accomplishments with numbers, speed improvements, or user counts");
        }

        // 5. Overall Length and Content Balance
        int wordCount = text.trim().split("\\s+").length;
        if (wordCount >= 180 && wordCount <= 900) {
            score += 10;
        } else if (wordCount < 180) {
            suggestions.add("Resume appears too short; expand on project details and technical responsibilities");
        }

        int finalScore = Math.max(30, Math.min(98, score));

        // Detect education and graduation year
        String detectedEducation = hasEducation ? "Engineering / Technical Degree" : "Unspecified";
        Integer detectedYear = null;
        Matcher yearMatcher = Pattern.compile("\\b(202[0-9]|2030)\\b").matcher(text);
        while (yearMatcher.find()) {
            detectedYear = Integer.parseInt(yearMatcher.group(1));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("atsScore", finalScore);
        map.put("strengths", strengths);
        map.put("weaknesses", weaknesses);
        map.put("missingSkills", missingSkills);
        map.put("suggestions", suggestions);
        map.put("detectedEducation", detectedEducation);
        map.put("detectedGraduationYear", detectedYear);
        return map;
    }
}