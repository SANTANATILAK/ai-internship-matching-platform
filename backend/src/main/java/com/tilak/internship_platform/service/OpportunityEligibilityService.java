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
        return graduationYear != null && branch != null && !branch.isBlank()
                && graduationYearEligible(opportunity.getGraduationYears(), graduationYear)
                && branchEligible(opportunity.getBranch(), branch)
                && jobTypeEligible(jobType, graduationYear);
    }

    public boolean graduationYearEligible(String criteria, Integer graduationYear) {
        if (criteria == null || criteria.isBlank() || graduationYear == null)
            return false;
        String lower = criteria.toLowerCase(Locale.ROOT);
        if (lower.contains("all years") || lower.contains("any year")
                || lower.equals("all") || lower.equals("any"))
            return true;

        Matcher matcher = YEAR.matcher(criteria);
        Set<Integer> years = new HashSet<>();
        while (matcher.find())
            years.add(Integer.parseInt(matcher.group()));
        if (years.contains(graduationYear))
            return true;

        int previous = -1;
        matcher.reset();
        while (matcher.find()) {
            int current = Integer.parseInt(matcher.group());
            if (previous >= 0 && graduationYear >= previous && graduationYear <= current)
                return true;
            previous = current;
        }
        return false;
    }

    public boolean branchEligible(String criteria, String studentBranch) {
        if (criteria == null || criteria.isBlank() || studentBranch == null || studentBranch.isBlank())
            return false;
        String accepted = normalize(criteria);
        if (accepted.contains("allbranches") || accepted.contains("allengineering")
                || accepted.contains("allstreams") || accepted.contains("anydiscipline")
                || accepted.equals("all") || accepted.equals("any"))
            return true;

        Set<String> aliases = aliases(studentBranch);
        Set<String> acceptedAliases = new HashSet<>();
        for (String token : criteria.split("[,;|/\\n]"))
            acceptedAliases.addAll(aliases(token));
        for (String alias : aliases)
            if (acceptedAliases.contains(alias))
                return true;
        for (String alias : aliases) {
            if (alias.length() >= 4 && accepted.contains(alias))
                return true;
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

        if (year <= fullTimeThrough)
            return fullTime || !internship;
        if (fullTimeExtraYears.contains(year) && fullTime)
            return true;
        if (internship && year >= internshipFrom)
            return true;
        if (ppo && year >= ppoFrom)
            return true;
        return !fullTime && !internship && !ppo;
    }

    private Set<Integer> parseYears(String value) {
        Set<Integer> years = new HashSet<>();
        if (value == null)
            return years;
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
        aliases.add(normalized);
        switch (normalized) {
            case "cse", "computerscience", "computerscienceengineering", "computerengineering" ->
                aliases.addAll(Set.of("cse", "computerscience", "computerscienceengineering", "computerengineering"));
            case "aiml", "artificialintelligence", "artificialintelligencemachinelearning", "machinelearning" ->
                aliases.addAll(Set.of("aiml", "artificialintelligence", "artificialintelligencemachinelearning",
                        "machinelearning"));
            case "aids", "artificialintelligencedatascience", "datascience" ->
                aliases.addAll(Set.of("aids", "artificialintelligencedatascience", "datascience"));
            case "it", "informationtechnology" -> aliases.addAll(Set.of("it", "informationtechnology"));
            case "ece", "electronicscommunication", "electronicscommunicationengineering" ->
                aliases.addAll(Set.of("ece", "electronicscommunication", "electronicscommunicationengineering"));
            case "eee", "electricalelectronics", "electricalelectronicsengineering" ->
                aliases.addAll(Set.of("eee", "electricalelectronics", "electricalelectronicsengineering"));
            case "me", "mechanical", "mechanicalengineering" ->
                aliases.addAll(Set.of("me", "mechanical", "mechanicalengineering"));
            case "ce", "civil", "civilengineering" -> aliases.addAll(Set.of("ce", "civil", "civilengineering"));
            case "chemical", "chemicalengineering" -> aliases.addAll(Set.of("chemical", "chemicalengineering"));
            default -> {
            }
        }
        return aliases;
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
