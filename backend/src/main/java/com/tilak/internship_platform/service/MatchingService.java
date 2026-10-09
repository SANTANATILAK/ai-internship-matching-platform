package com.tilak.internship_platform.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

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

        public List<Map<String, Object>> matchSkills(
                        List<String> userSkills) {

                return matchSkills(userSkills, null);
        }

        public List<Map<String, Object>> matchSkills(
                        List<String> userSkills,
                        Integer graduationYear) {

                return matchSkills(userSkills, graduationYear, null);
        }

        public List<Map<String, Object>> matchSkills(
                        List<String> userSkills,
                        Integer graduationYear,
                        String branch) {

                List<Internship> internships = repository.findAll();

                List<Map<String, Object>> results = new ArrayList<>();

                if (userSkills == null) {
                        userSkills = new ArrayList<>();
                }

                Set<String> normalizedUserSkills = new HashSet<>();

                for (String skill : userSkills) {

                        if (skill != null && !skill.isBlank()) {

                                normalizedUserSkills.add(
                                                normalizeSkill(skill));
                        }
                }

                for (Internship internship : internships) {

                        if (!isEligibleForGraduationYear(
                                        internship,
                                        graduationYear)) {

                                continue;
                        }

                        String skillsText = internship.getSkills();

                        if (skillsText == null ||
                                        skillsText.isBlank()) {

                                continue;
                        }

                        String[] requiredSkills = skillsText.split(",");

                        int matched = 0;

                        Set<String> matchedSkillNames = new HashSet<>();

                        for (String requiredSkill : requiredSkills) {

                                if (requiredSkill == null ||
                                                requiredSkill.isBlank()) {

                                        continue;
                                }

                                String normalizedRequiredSkill = normalizeSkill(requiredSkill);

                                for (String userSkill : normalizedUserSkills) {

                                        if (skillsMatch(
                                                        normalizedRequiredSkill,
                                                        userSkill)) {

                                                if (!matchedSkillNames.contains(
                                                                normalizedRequiredSkill)) {

                                                        matched++;

                                                        matchedSkillNames.add(
                                                                        normalizedRequiredSkill);
                                                }

                                                break;
                                        }
                                }
                        }

                        int totalRequiredSkills = 0;

                        for (String requiredSkill : requiredSkills) {

                                if (requiredSkill != null &&
                                                !requiredSkill.isBlank()) {

                                        totalRequiredSkills++;
                                }
                        }

                        if (totalRequiredSkills == 0) {
                                continue;
                        }

                        double percentage = (matched * 100.0)
                                        / totalRequiredSkills;

                        if (percentage < 40) {
                                continue;
                        }

                        Map<String, Object> result = new LinkedHashMap<>();

                        result.put(
                                        "internshipId",
                                        internship.getId());
                        result.put("verificationStatus", "REVIEW");
                        result.put("sourceType", "LEGACY_SAVED_LISTING");

                        result.put(
                                        "company",
                                        internship.getCompany());

                        result.put(
                                        "title",
                                        internship.getTitle());

                        result.put(
                                        "location",
                                        internship.getLocation());

                        result.put(
                                        "stipend",
                                        internship.getStipend());

                        result.put(
                                        "requiredSkills",
                                        internship.getSkills());

                        result.put(
                                        "applyUrl",
                                        internship.getApplyUrl());

                        result.put(
                                        "type",
                                        internship.getType());

                        result.put(
                                        "graduationYears",
                                        internship.getGraduationYears());

                        result.put(
                                        "matchedSkills",
                                        matched);

                        result.put(
                                        "totalRequiredSkills",
                                        totalRequiredSkills);

                        result.put(
                                        "matchPercentage",
                                        Math.round(
                                                        percentage * 100.0) / 100.0);

                        result.put(
                                        "matchLevel",
                                        getMatchLevel(percentage));

                        results.add(result);
                }

                List<Opportunity> currentOpportunities = new ArrayList<>();
                List<Opportunity> verifiedOpportunities = opportunityRepository
                                .findByIsActiveTrueAndCountryIgnoreCaseAndVerificationStatusAndOpportunityStatus(
                                                "India", "VERIFIED", "OPEN");
                List<Opportunity> reviewOpportunities = opportunityRepository
                                .findByIsActiveTrueAndCountryIgnoreCaseAndVerificationStatusAndOpportunityStatus(
                                                "India", "REVIEW", "OPEN");
                if (verifiedOpportunities != null) {
                        currentOpportunities.addAll(verifiedOpportunities);
                }
                if (reviewOpportunities != null) {
                        currentOpportunities.addAll(reviewOpportunities);
                }
                for (Opportunity opportunity : currentOpportunities) {
                        boolean verified = "VERIFIED".equals(opportunity.getVerificationStatus());
                        if (verified && !eligibilityService.isEligible(
                                        opportunity, graduationYear, branch)) {
                                continue;
                        }
                        boolean knownYearMismatch = opportunity.getGraduationYears() != null
                                        && !opportunity.getGraduationYears().isBlank()
                                        && !isEligibleForGraduationYear(
                                                        opportunity.getGraduationYears(), graduationYear);
                        boolean knownBranchMismatch = opportunity.getBranch() != null
                                        && !opportunity.getBranch().isBlank()
                                        && !isEligibleForBranch(opportunity.getBranch(), branch);
                        if ((verified && (knownYearMismatch || knownBranchMismatch))
                                        || (!verified && (knownYearMismatch || knownBranchMismatch))) {
                                continue;
                        }

                        String skillsText = opportunity.getSkills();
                        if (skillsText == null || skillsText.isBlank()) {
                                continue;
                        }
                        String[] requiredSkills = skillsText.split(",");
                        int matched = 0;
                        int totalRequiredSkills = 0;
                        Set<String> matchedSkillNames = new HashSet<>();
                        for (String requiredSkill : requiredSkills) {
                                if (requiredSkill == null || requiredSkill.isBlank()) {
                                        continue;
                                }
                                totalRequiredSkills++;
                                String normalizedRequiredSkill = normalizeSkill(requiredSkill);
                                for (String userSkill : normalizedUserSkills) {
                                        if (skillsMatch(normalizedRequiredSkill, userSkill)
                                                        && matchedSkillNames.add(normalizedRequiredSkill)) {
                                                matched++;
                                                break;
                                        }
                                }
                        }
                        if (totalRequiredSkills == 0) {
                                continue;
                        }
                        double percentage = matched * 100.0 / totalRequiredSkills;
                        if (percentage < 40) {
                                continue;
                        }

                        Map<String, Object> result = new LinkedHashMap<>();
                        result.put("opportunityId", opportunity.getId());
                        result.put("verificationStatus", opportunity.getVerificationStatus());
                        result.put("sourceType", "COLLECTED_OPPORTUNITY");
                        result.put("company", opportunity.getCompany());
                        result.put("title", opportunity.getTitle());
                        result.put("location", opportunity.getLocation());
                        result.put("stipend", opportunity.getStipend());
                        result.put("salary", opportunity.getSalary());
                        result.put("requiredSkills", opportunity.getSkills());
                        result.put("applyUrl", opportunity.getApplyUrl());
                        result.put("type", opportunity.getType());
                        result.put("graduationYears", opportunity.getGraduationYears());
                        result.put("matchedSkills", matched);
                        result.put("totalRequiredSkills", totalRequiredSkills);
                        result.put("matchPercentage", Math.round(percentage * 100.0) / 100.0);
                        result.put("matchLevel", getMatchLevel(percentage));
                        results.add(result);
                }

                results.sort((first, second) -> Double.compare(
                                ((Number) second.get(
                                                "matchPercentage")).doubleValue(),

                                ((Number) first.get(
                                                "matchPercentage")).doubleValue()));

                return results;
        }

        private String normalizeSkill(String skill) {

                String value = skill.trim()
                                .toLowerCase()
                                .replace("-", "")
                                .replace("_", "")
                                .replace(".", "")
                                .replace(" ", "");

                return switch (value) {

                        case "ml" ->
                                "machinelearning";

                        case "ai" ->
                                "artificialintelligence";

                        case "js" ->
                                "javascript";

                        case "ts" ->
                                "typescript";

                        case "reactjs" ->
                                "react";

                        case "nodejs" ->
                                "node";

                        case "scikitlearn" ->
                                "scikitlearn";

                        case "sklearn" ->
                                "scikitlearn";

                        case "mysql" ->
                                "mysql";

                        case "postgresql" ->
                                "postgresql";

                        case "postgressql" ->
                                "postgresql";

                        default ->
                                value;
                };
        }

        private boolean skillsMatch(
                        String requiredSkill,
                        String userSkill) {

                if (requiredSkill.equals(userSkill)) {
                        return true;
                }

                if (requiredSkill.contains(userSkill) &&
                                userSkill.length() >= 4) {

                        return true;
                }

                if (userSkill.contains(requiredSkill) &&
                                requiredSkill.length() >= 4) {

                        return true;
                }

                return false;
        }

        private String getMatchLevel(
                        double percentage) {

                if (percentage >= 90) {
                        return "Excellent Match";
                }

                if (percentage >= 80) {
                        return "Strong Match";
                }

                if (percentage >= 60) {
                        return "Good Match";
                }

                return "Partial Match";
        }

        private boolean isEligibleForGraduationYear(
                        Internship internship,
                        Integer graduationYear) {

                if (graduationYear == null) {
                        return true;
                }

                String graduationYears = internship.getGraduationYears();

                if (graduationYears == null ||
                                graduationYears.isBlank()) {

                        return true;
                }

                String[] eligibleYears = graduationYears.split(",");

                for (String year : eligibleYears) {

                        if (year.trim().equals(
                                        String.valueOf(graduationYear))) {

                                return true;
                        }
                }

                return false;
        }

        private boolean isEligibleForGraduationYear(
                        String graduationYears,
                        Integer graduationYear) {

                if (graduationYear == null) {
                        return true;
                }
                if (graduationYears == null || graduationYears.isBlank()) {
                        return false;
                }
                String requestedYear = String.valueOf(graduationYear);
                for (String eligibleYear : graduationYears.split("[,;|]")) {
                        String year = eligibleYear.trim();
                        if (year.equals(requestedYear)) {
                                return true;
                        }
                        if (year.matches("20\\d{2}\\s*-\\s*20\\d{2}")) {
                                String[] range = year.split("-");
                                int firstYear = Integer.parseInt(range[0].trim());
                                int lastYear = Integer.parseInt(range[1].trim());
                                if (graduationYear >= firstYear && graduationYear <= lastYear) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        private boolean isEligibleForBranch(String eligibleBranches, String studentBranch) {
                if (eligibleBranches == null || eligibleBranches.isBlank()
                                || studentBranch == null || studentBranch.isBlank()) {
                        return true;
                }
                String normalizedStudentBranch = normalizeSkill(studentBranch);
                for (String eligibleBranch : eligibleBranches.split("[,;|]")) {
                        String normalizedEligibleBranch = normalizeSkill(eligibleBranch);
                        if (normalizedEligibleBranch.equals("all")
                                        || normalizedEligibleBranch.equals("allbranches")
                                        || normalizedEligibleBranch.contains(normalizedStudentBranch)
                                        || normalizedStudentBranch.contains(normalizedEligibleBranch)) {
                                return true;
                        }
                }
                return false;
        }
}