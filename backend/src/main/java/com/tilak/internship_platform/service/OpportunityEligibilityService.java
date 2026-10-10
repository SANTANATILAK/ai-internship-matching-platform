package com.tilak.internship_platform.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tilak.internship_platform.entity.Opportunity;

@Service
public class OpportunityEligibilityService {

    private static final Pattern YEAR = Pattern.compile("20\\d{2}");
    private final int fullTimeThrough;
    private final Set<Integer> fullTimeExtraYears;
    private final int internshipFrom;
    private final int ppoFrom;

    public OpportunityEligibilityService(
            @Value("${opportunities.eligibility.full-time-through:2026}") int fullTimeThrough,
            @Value("${opportunities.eligibility.full-time-extra-years:2027}") String fullTimeExtraYears,
            @Value("${opportunities.eligibility.internship-from:2027}") int internshipFrom,
            @Value("${opportunities.eligibility.ppo-from:2027}") int ppoFrom) {
        this.fullTimeThrough = fullTimeThrough;
        this.fullTimeExtraYears = parseYears(fullTimeExtraYears);
        this.internshipFrom = internshipFrom;
        this.ppoFrom = ppoFrom;
    }

    public boolean isEligible(Opportunity opportunity, Integer graduationYear, String branch) {
        String jobType = opportunity.getJobType();
        if (jobType == null || jobType.isBlank()) {
            jobType = opportunity.getType();
        }
        if (jobType == null || jobType.isBlank()) {
            jobType = opportunity.getTitle();
        }

        // Graduation year check
        if (graduationYear != null && opportunity.getGraduationYears() != null && !opportunity.getGraduationYears().isBlank()) {
            if (!graduationYearEligible(opportunity.getGraduationYears(), graduationYear)) {
                return false;
            }
        }

        // Branch check
        if (branch != null && !branch.isBlank() && opportunity.getBranch() != null && !opportunity.getBranch().isBlank()) {
            if (!branchEligible(opportunity.getBranch(), branch)) {
                return false;
            }
        }

        // Job type check (only if year is provided)
        if (graduationYear != null && !jobTypeEligible(jobType, graduationYear)) {
            return false;
        }

        return true;
    }

    public boolean graduationYearEligible(String criteria, Integer graduationYear) {
        if (criteria == null || criteria.isBlank() || graduationYear == null) {
            return true;
        }
        String lower = criteria.toLowerCase(Locale.ROOT);
        if (lower.contains("all years") || lower.contains("any year")
                || lower.equals("all") || lower.equals("any") || lower.contains("open")) {
            return true;
        }

        Matcher matcher = YEAR.matcher(criteria);
        Set<Integer> years = new HashSet<>();
        while (matcher.find()) {
            years.add(Integer.parseInt(matcher.group()));
        }
        if (years.contains(graduationYear)) {
            return true;
        }

        int previous = -1;
        matcher.reset();
        while (matcher.find()) {
            int current = Integer.parseInt(matcher.group());
            if (previous >= 0 && graduationYear >= previous && graduationYear <= current) {
                return true;
            }
            previous = current;
        }
        return false;
    }

    public boolean branchEligible(String criteria, String studentBranch) {
        if (criteria == null || criteria.isBlank() || studentBranch == null || studentBranch.isBlank()) {
            return true;
        }
        String accepted = normalize(criteria);
        if (accepted.contains("allbranches") || accepted.contains("allengineering")
                || accepted.contains("allstreams") || accepted.contains("anydiscipline")
                || accepted.contains("anydegree") || accepted.equals("all") || accepted.equals("any")
                || accepted.contains("open")) {
            return true;
        }

        Set<String> studentAliases = aliases(studentBranch);
        Set<String> acceptedAliases = new HashSet<>();
        for (String token : criteria.split("[,;|/\\n&]")) {
            acceptedAliases.addAll(aliases(token));
        }

        for (String alias : studentAliases) {
            if (acceptedAliases.contains(alias)) {
                return true;
            }
            if (alias.length() >= 3 && accepted.contains(alias)) {
                return true;
            }
        }

        return false;
    }

    public boolean jobTypeEligible(String value, int year) {
        if (value == null || value.isBlank()) {
            return true;
        }
        String type = normalize(value);
        boolean fullTime = type.contains("fulltime") || type.equals("job")
                || type.contains("regular") || type.contains("graduateengineertrainee");
        boolean internship = type.contains("intern");
        boolean ppo = type.contains("ppo") || type.contains("preplacement");

        if (year <= fullTimeThrough) {
            return fullTime || !internship;
        }
        if (fullTimeExtraYears.contains(year) && fullTime) {
            return true;
        }
        if (internship && year >= internshipFrom) {
            return true;
        }
        if (ppo && year >= ppoFrom) {
            return true;
        }
        return true;
    }

    private Set<Integer> parseYears(String value) {
        Set<Integer> years = new HashSet<>();
        if (value == null) {
            return years;
        }
        Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(part -> part.matches("20\\d{2}"))
                .map(Integer::parseInt)
                .forEach(years::add);
        return years;
    }

    private Set<String> aliases(String value) {
        String normalized = normalize(value);
        Set<String> aliases = new HashSet<>();
        if (normalized.isBlank()) return aliases;
        aliases.add(normalized);

        // CSE / IT / Software
        if (normalized.contains("cse") || normalized.contains("computerscience") || normalized.contains("software") || normalized.contains("informationtech")) {
            aliases.addAll(Set.of("cse", "computerscience", "computerscienceengineering", "it", "informationtechnology", "softwareengineering"));
        }
        // AI / ML / DS
        if (normalized.contains("aiml") || normalized.contains("ai") || normalized.contains("artificialintelligence") || normalized.contains("machinelearning") || normalized.contains("data")) {
            aliases.addAll(Set.of("aiml", "ai", "artificialintelligence", "machinelearning", "datascience", "aids", "cse", "it"));
        }
        // ECE / EEE / Electronics / Electrical
        if (normalized.contains("ece") || normalized.contains("electronics") || normalized.contains("vlsi") || normalized.contains("telecommunication")) {
            aliases.addAll(Set.of("ece", "electronics", "electronicscommunication", "vlsi", "embeddedsystems"));
        }
        if (normalized.contains("eee") || normalized.contains("electrical") || normalized.contains("power")) {
            aliases.addAll(Set.of("eee", "electrical", "electricalelectronics", "powerelectronics", "ece"));
        }
        // Mechanical / Robotics / Automobile / Production
        if (normalized.contains("mech") || normalized.contains("robotics") || normalized.contains("automobile") || normalized.contains("production") || normalized.contains("mechatronics")) {
            aliases.addAll(Set.of("me", "mechanical", "mechanicalengineering", "robotics", "mechatronics", "automobile", "production"));
        }
        // Civil / Structural / Environmental
        if (normalized.contains("civil") || normalized.contains("structural") || normalized.contains("construction")) {
            aliases.addAll(Set.of("ce", "civil", "civilengineering", "structural", "structuralengineering", "construction"));
        }
        // Chemical / Biotech
        if (normalized.contains("chem") || normalized.contains("petro") || normalized.contains("biotech") || normalized.contains("pharma")) {
            aliases.addAll(Set.of("chemical", "chemicalengineering", "biotechnology", "petroleum", "materials"));
        }

        return aliases;
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
