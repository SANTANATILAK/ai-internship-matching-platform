package com.tilak.internship_platform.matcher;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.entity.OpportunityType;
import com.tilak.internship_platform.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class EligibilityEngine {

    public boolean isEligible(User user, Opportunity opportunity) {
        if (user == null || opportunity == null) return false;

        // 1. Check Graduation Year
        if (!isGraduationYearEligible(user.getGraduationYear(), opportunity)) {
            return false;
        }

        // 2. Check Branch Requirements if specified
        if (!isBranchEligible(user.getBranch(), opportunity.getBranchRequirements())) {
            return false;
        }

        return true;
    }

    public boolean isGraduationYearEligible(Integer studentGradYear, Opportunity opportunity) {
        if (studentGradYear == null) return true; // lenient if user hasn't specified yet

        String oppGradYears = opportunity.getGraduationYears();
        if (oppGradYears != null && !oppGradYears.trim().isEmpty()) {
            if (oppGradYears.equalsIgnoreCase("any") || oppGradYears.equalsIgnoreCase("all")) {
                return true;
            }
            List<String> years = Arrays.stream(oppGradYears.split(","))
                    .map(String::trim)
                    .toList();
            if (years.contains(String.valueOf(studentGradYear))) {
                return true;
            }
        }

        // Dynamic eligibility based on current calendar year
        int currentYear = LocalDate.now().getYear();
        int yearsUntilGraduation = studentGradYear - currentYear;
        OpportunityType oppType = opportunity.getType();

        switch (oppType) {
            case FULL_TIME:
                // Graduating this year or already graduated
                return yearsUntilGraduation <= 0;
            case PPO:
                // Graduating within 1 year
                return yearsUntilGraduation >= 0 && yearsUntilGraduation <= 1;
            case INTERNSHIP:
                // Graduating in 0 to 3 years
                return yearsUntilGraduation >= 0 && yearsUntilGraduation <= 3;
            case PART_TIME:
                return true;
            default:
                return true;
        }
    }

    public boolean isBranchEligible(String studentBranch, String requiredBranches) {
        if (requiredBranches == null || requiredBranches.trim().isEmpty() || requiredBranches.equalsIgnoreCase("any")) {
            return true;
        }
        if (studentBranch == null || studentBranch.trim().isEmpty()) {
            return true;
        }

        String lowerStudentBranch = normalizeBranch(studentBranch);
        String[] branches = requiredBranches.toLowerCase().split("[,/]");
        for (String b : branches) {
            String normB = normalizeBranch(b.trim());
            if (lowerStudentBranch.contains(normB) || normB.contains(lowerStudentBranch)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeBranch(String branch) {
        if (branch == null) return "";
        String s = branch.trim().toLowerCase();
        if (s.equals("cse") || s.equals("cs") || s.contains("computer science") || s.contains("computing")) {
            return "computer science";
        }
        if (s.equals("it") || s.contains("information technology")) {
            return "information technology";
        }
        if (s.equals("ece") || s.contains("electronics")) {
            return "electronics";
        }
        if (s.equals("ai") || s.equals("aiml") || s.contains("artificial intelligence")) {
            return "artificial intelligence";
        }
        if (s.equals("ds") || s.contains("data science")) {
            return "data science";
        }
        return s;
    }
}
