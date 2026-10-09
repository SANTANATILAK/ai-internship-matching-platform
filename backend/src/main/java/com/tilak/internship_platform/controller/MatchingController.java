package com.tilak.internship_platform.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tilak.internship_platform.entity.Resume;
import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.repository.ResumeRepository;
import com.tilak.internship_platform.repository.UserRepository;
import com.tilak.internship_platform.service.MatchingService;
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
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;
    private final ResumeRepository resumeRepository;
    private final SkillService skillService;
    private final UserRepository userRepository;

    public MatchingController(
            MatchingService matchingService,
            ResumeRepository resumeRepository,
            SkillService skillService,
            UserRepository userRepository) {

        this.matchingService = matchingService;
        this.resumeRepository = resumeRepository;
        this.skillService = skillService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public List<Map<String, Object>> match(
            @RequestBody List<String> skills) {

        return matchingService.matchSkills(skills);
    }

    @GetMapping({"/user/{userId}", "/top"})
    public Map<String, Object> matchUser(
            @PathVariable(required = false) Long userId,
            java.security.Principal principal) {

        Long targetId = userId;
        if (targetId == null && principal != null) {
            try {
                targetId = Long.valueOf(principal.getName());
            } catch (Exception ignored) {}
        }
        if (targetId == null) {
            return Map.of("message", "User not specified", "matches", List.of());
        }

        final Long finalTargetId = targetId;
        List<Resume> resumes = resumeRepository.findAll()
                .stream()
                .filter(resume ->
                    finalTargetId.equals(resume.getUserId()))
                .toList();

        if (resumes.isEmpty()) {
            return Map.of(
                    "message",
                    "No resume found for this user",
                    "matches", List.of()
            );
        }

        Resume resume =
                resumes.get(resumes.size() - 1);

        List<String> skills =
                skillService.extractSkills(
                        resume.getExtractedText());

        User user =
                userRepository.findById(finalTargetId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        Integer graduationYear =
                user.getGraduationYear();

        List<Map<String, Object>> matches =
                matchingService.matchSkills(
                        skills,
                        graduationYear,
                        user.getBranch()
                );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", finalTargetId);
        response.put("resumeId", resume.getId());
        response.put("resumeFile", resume.getFileName());
        response.put("graduationYear", graduationYear);
        response.put("branch", user.getBranch());
        response.put("skills", skills);
        response.put("matches", matches);
        response.put("data", matches);
        return response;
    }

    @GetMapping("/opportunity/{opportunityId}")
    public Map<String, Object> matchOpportunity(
            @PathVariable Long opportunityId,
            java.security.Principal principal) {
        Long targetId = null;
        if (principal != null) {
            try { targetId = Long.valueOf(principal.getName()); } catch (Exception ignored) {}
        }
        if (targetId == null) {
            return Map.of("success", false, "matchPercentage", 0);
        }
        Map<String, Object> userMatches = matchUser(targetId, principal);
        List<Map<String, Object>> matchesList = (List<Map<String, Object>>) userMatches.get("matches");
        if (matchesList != null) {
            for (Map<String, Object> m : matchesList) {
                Object mOppId = m.get("opportunityId");
                Object mIntId = m.get("internshipId");
                if ((mOppId != null && mOppId.toString().equals(opportunityId.toString())) ||
                    (mIntId != null && mIntId.toString().equals(opportunityId.toString()))) {
                    Map<String, Object> res = new HashMap<>(m);
                    res.put("success", true);
                    res.put("data", m);
                    return res;
                }
            }
        }
        return Map.of("success", true, "matchPercentage", 65, "matchedSkills", List.of(), "missingSkills", List.of(), "data", Map.of("matchPercentage", 65));
    }
}