package com.tilak.internship_platform.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tilak.internship_platform.entity.Internship;
import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.repository.InternshipRepository;
import com.tilak.internship_platform.repository.OpportunityRepository;

@Service
public class MatchingService {

    private final InternshipRepository repository;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityEligibilityService eligibilityService;

    @Autowired
    public MatchingService(
            InternshipRepository repository,
            OpportunityRepository opportunityRepository,
            OpportunityEligibilityService eligibilityService) {
        this.repository = repository;
        this.opportunityRepository = opportunityRepository;
        this.eligibilityService = eligibilityService;
    }

    public MatchingService(
            InternshipRepository repository,
            OpportunityRepository opportunityRepository) {
        this(repository, opportunityRepository,
                new OpportunityEligibilityService(2026, "2027", 2027, 2027));
    }

    public List<Map<String, Object>> matchSkills(List<String> userSkills) {
        return matchSkills(userSkills, null, null);
    }

    public List<Map<String, Object>> matchSkills(List<String> userSkills, Integer graduationYear) {
        return matchSkills(userSkills, graduationYear, null);
    }

    public List<Map<String, Object>> matchSkills(
            List<String> userSkills,
            Integer graduationYear,
            String branch) {

        List<Map<String, Object>> results = new ArrayList<>();

        if (userSkills == null) {
            userSkills = new ArrayList<>();
        }

        Set<String> normalizedUserSkills = new HashSet<>();
        for (String skill : userSkills) {
            if (skill != null && !skill.isBlank()) {
                normalizedUserSkills.add(normalizeSkill(skill));
            }
        }

        // 1. Process Opportunities (Primary Opportunity Database)
        List<Opportunity> opportunities = opportunityRepository.findAll();
        for (Opportunity opportunity : opportunities) {
            if (Boolean.FALSE.equals(opportunity.getIsActive())) {
                continue;
            }

            boolean eligible = eligibilityService.isEligible(opportunity, graduationYear, branch);
            if (!eligible) {
                continue;
            }

            String skillsText = opportunity.getSkills();
            String[] requiredSkills = (skillsText != null && !skillsText.isBlank())
                    ? skillsText.split("[,;|/\\n]")
                    : new String[0];

            List<String> matchedSkillNames = new ArrayList<>();
            List<String> missingSkillNames = new ArrayList<>();
            int totalRequired = 0;

            for (String req : requiredSkills) {
                String trimmed = req.trim();
                if (trimmed.isBlank()) continue;
                totalRequired++;
                String normalizedReq = normalizeSkill(trimmed);

                boolean matched = false;
                for (String userSkill : normalizedUserSkills) {
                    if (skillsMatch(normalizedReq, userSkill)) {
                        matched = true;
                        break;
                    }
                }

                if (matched) {
                    if (!matchedSkillNames.contains(trimmed)) {
                        matchedSkillNames.add(trimmed);
                    }
                } else {
                    if (!missingSkillNames.contains(trimmed)) {
                        missingSkillNames.add(trimmed);
                    }
                }
            }

            double percentage;
            if (totalRequired == 0) {
                // If company didn't list specific skills, estimate match by branch alignment
                percentage = 75.0;
            } else if (userSkills.isEmpty()) {
                percentage = 65.0;
            } else {
                percentage = (matchedSkillNames.size() * 100.0) / totalRequired;
                // Add academic branch alignment bonus if candidate's branch matches role
                if (branch != null && opportunity.getBranch() != null
                        && eligibilityService.branchEligible(opportunity.getBranch(), branch)) {
                    percentage = Math.min(98.0, percentage + 10.0);
                }
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("opportunityId", opportunity.getId());
            result.put("verificationStatus", opportunity.getVerificationStatus() != null ? opportunity.getVerificationStatus() : "VERIFIED");
            result.put("sourceType", "COLLECTED_OPPORTUNITY");
            result.put("company", opportunity.getCompany());
            result.put("companyDomain", opportunity.getCompanyDomain());
            result.put("title", opportunity.getTitle());
            result.put("location", opportunity.getLocation());
            result.put("workMode", opportunity.getWorkMode() != null ? opportunity.getWorkMode() : "HYBRID");
            result.put("stipend", opportunity.getStipend() != null ? opportunity.getStipend() : opportunity.getSalary());
            result.put("salary", opportunity.getSalary());
            result.put("requiredSkills", opportunity.getSkills());
            result.put("applyUrl", opportunity.getApplyUrl());
            result.put("type", opportunity.getType() != null ? opportunity.getType() : "INTERNSHIP");
            result.put("jobType", opportunity.getJobType() != null ? opportunity.getJobType() : opportunity.getType());
            result.put("branch", opportunity.getBranch());
            result.put("graduationYears", opportunity.getGraduationYears());
            result.put("matchedSkills", matchedSkillNames);
            result.put("missingSkills", missingSkillNames);
            result.put("matchedSkillsCount", matchedSkillNames.size());
            result.put("totalRequiredSkills", totalRequired);
            result.put("matchPercentage", Math.round(percentage * 10.0) / 10.0);
            result.put("matchLevel", getMatchLevel(percentage));
            results.add(result);
        }

        // 2. Process legacy Internships table if present
        List<Internship> internships = repository.findAll();
        for (Internship internship : internships) {
            if (!isEligibleForGraduationYear(internship, graduationYear)) {
                continue;
            }

            String skillsText = internship.getSkills();
            String[] requiredSkills = (skillsText != null && !skillsText.isBlank())
                    ? skillsText.split("[,;|/\\n]")
                    : new String[0];

            List<String> matchedSkillNames = new ArrayList<>();
            List<String> missingSkillNames = new ArrayList<>();
            int totalRequired = 0;

            for (String req : requiredSkills) {
                String trimmed = req.trim();
                if (trimmed.isBlank()) continue;
                totalRequired++;
                String normalizedReq = normalizeSkill(trimmed);

                boolean matched = false;
                for (String userSkill : normalizedUserSkills) {
                    if (skillsMatch(normalizedReq, userSkill)) {
                        matched = true;
                        break;
                    }
                }

                if (matched) {
                    if (!matchedSkillNames.contains(trimmed)) matchedSkillNames.add(trimmed);
                } else {
                    if (!missingSkillNames.contains(trimmed)) missingSkillNames.add(trimmed);
                }
            }

            double percentage = totalRequired == 0 ? 70.0 : ((matchedSkillNames.size() * 100.0) / totalRequired);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("internshipId", internship.getId());
            result.put("verificationStatus", "VERIFIED");
            result.put("sourceType", "VERIFIED_LISTING");
            result.put("company", internship.getCompany());
            result.put("title", internship.getTitle());
            result.put("location", internship.getLocation());
            result.put("stipend", internship.getStipend());
            result.put("requiredSkills", internship.getSkills());
            result.put("applyUrl", internship.getApplyUrl());
            result.put("type", internship.getType());
            result.put("graduationYears", internship.getGraduationYears());
            result.put("matchedSkills", matchedSkillNames);
            result.put("missingSkills", missingSkillNames);
            result.put("matchedSkillsCount", matchedSkillNames.size());
            result.put("totalRequiredSkills", totalRequired);
            result.put("matchPercentage", Math.round(percentage * 10.0) / 10.0);
            result.put("matchLevel", getMatchLevel(percentage));
            results.add(result);
        }

        // Sort descending by match percentage
        results.sort((first, second) -> Double.compare(
                ((Number) second.get("matchPercentage")).doubleValue(),
                ((Number) first.get("matchPercentage")).doubleValue()
        ));

        return results;
    }

    private String normalizeSkill(String skill) {
        if (skill == null) return "";
        String value = skill.trim()
                .toLowerCase(Locale.ROOT)
                .replace("-", "")
                .replace("_", "")
                .replace(".", "")
                .replace(" ", "");

        return switch (value) {
            case "ml" -> "machinelearning";
            case "ai" -> "artificialintelligence";
            case "js" -> "javascript";
            case "ts" -> "typescript";
            case "reactjs" -> "react";
            case "nodejs" -> "node";
            case "scikitlearn", "sklearn" -> "scikitlearn";
            case "mysql" -> "mysql";
            case "postgresql", "postgressql", "postgres" -> "postgresql";
            case "springboot" -> "springboot";
            case "aws" -> "aws";
            case "docker" -> "docker";
            case "autocad" -> "autocad";
            case "solidworks" -> "solidworks";
            case "vlsi" -> "vlsi";
            case "verilog" -> "verilog";
            default -> value;
        };
    }

    private boolean skillsMatch(String requiredSkill, String userSkill) {
        if (requiredSkill.equals(userSkill)) {
            return true;
        }
        if (requiredSkill.contains(userSkill) && userSkill.length() >= 3) {
            return true;
        }
        if (userSkill.contains(requiredSkill) && requiredSkill.length() >= 3) {
            return true;
        }
        return false;
    }

    private String getMatchLevel(double percentage) {
        if (percentage >= 85) return "Excellent Match";
        if (percentage >= 70) return "Strong Match";
        if (percentage >= 50) return "Good Match";
        return "Potential Match";
    }

    private boolean isEligibleForGraduationYear(Internship internship, Integer graduationYear) {
        if (graduationYear == null) return true;
        String graduationYears = internship.getGraduationYears();
        if (graduationYears == null || graduationYears.isBlank()) return true;

        String requested = String.valueOf(graduationYear);
        for (String year : graduationYears.split("[,;|]")) {
            if (year.trim().equals(requested)) return true;
        }
        return false;
    }
}