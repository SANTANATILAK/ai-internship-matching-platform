package com.tilak.internship_platform.parser;

import com.tilak.internship_platform.matcher.SkillNormalizer;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ResumeAnalyzer {

    private final SkillNormalizer skillNormalizer;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}", Pattern.CASE_INSENSITIVE);

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:(?:\\+|0{0,2})91(\\s*[- ]\\s*|[0-9]{0,1})\\d{10}|\\b\\d{10}\\b|\\(?\\d{3}\\)?[-. ]?\\d{3}[-. ]?\\d{4})");

    private static final Pattern YEAR_PATTERN = Pattern.compile(
            "\\b(202[0-9]|203[0-2])\\b");

    private static final List<String> KNOWN_SKILLS = Arrays.asList(
            "python", "java", "c++", "c", "c#", "javascript", "typescript", "html", "css",
            "react", "angular", "vue", "node.js", "nodejs", "express", "spring", "spring boot",
            "django", "flask", "fastapi", "sql", "mysql", "postgresql", "mongodb", "redis",
            "machine learning", "deep learning", "artificial intelligence", "ai", "ml", "nlp",
            "computer vision", "tensorflow", "pytorch", "keras", "scikit-learn", "sklearn",
            "pandas", "numpy", "matplotlib", "seaborn", "opencv", "data analysis",
            "git", "github", "docker", "kubernetes", "aws", "azure", "gcp", "linux", "ci/cd",
            "rest api", "graphql", "microservices", "data structures", "algorithms",
            "object-oriented programming", "system design", "genai", "large language models", "llm"
    );

    public ResumeAnalyzer(SkillNormalizer skillNormalizer) {
        this.skillNormalizer = skillNormalizer;
    }

    @Getter
    @Builder
    public static class AnalyzedResumeData {
        private String name;
        private String email;
        private String phone;
        private List<String> skills;
        private String educationDetails;
        private Integer graduationYear;
        private Double experienceYears;
        private List<String> projects;
        private List<String> certifications;
    }

    public AnalyzedResumeData analyze(String text) {
        if (text == null) text = "";
        String normalizedText = text.replace("\r\n", "\n");

        String email = extractEmail(normalizedText);
        String phone = extractPhone(normalizedText);
        String name = extractName(normalizedText);
        List<String> skills = extractSkills(normalizedText);
        Integer gradYear = extractGraduationYear(normalizedText);
        String education = extractEducation(normalizedText);
        Double experience = extractExperienceYears(normalizedText);
        List<String> projects = extractProjects(normalizedText);
        List<String> certifications = extractCertifications(normalizedText);

        return AnalyzedResumeData.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .skills(skills)
                .educationDetails(education)
                .graduationYear(gradYear)
                .experienceYears(experience)
                .projects(projects)
                .certifications(certifications)
                .build();
    }

    private String extractEmail(String text) {
        Matcher m = EMAIL_PATTERN.matcher(text);
        if (m.find()) {
            return m.group().toLowerCase().trim();
        }
        return null;
    }

    private String extractPhone(String text) {
        Matcher m = PHONE_PATTERN.matcher(text);
        if (m.find()) {
            return m.group().replaceAll("[^0-9+]", "");
        }
        return null;
    }

    private String extractName(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 2 && trimmed.length() < 50
                    && !trimmed.toLowerCase().contains("resume")
                    && !trimmed.toLowerCase().contains("curriculum vitae")
                    && !trimmed.toLowerCase().contains("email")
                    && !trimmed.toLowerCase().contains("@")
                    && !trimmed.matches(".*\\d.*")) {
                return trimmed;
            }
        }
        return "Candidate";
    }

    private List<String> extractSkills(String text) {
        Set<String> matched = new LinkedHashSet<>();
        String lowerText = text.toLowerCase();

        for (String skill : KNOWN_SKILLS) {
            // Check word boundary
            String regex = "\\b" + Pattern.quote(skill) + "\\b";
            if (Pattern.compile(regex).matcher(lowerText).find()) {
                matched.add(skillNormalizer.normalize(skill));
            }
        }
        return new ArrayList<>(matched);
    }

    private Integer extractGraduationYear(String text) {
        Matcher m = YEAR_PATTERN.matcher(text);
        Integer bestYear = null;
        while (m.find()) {
            int y = Integer.parseInt(m.group(1));
            // Prefer graduation years in the future or recent past
            if (y >= 2024 && y <= 2030) {
                bestYear = y;
            } else if (bestYear == null) {
                bestYear = y;
            }
        }
        return bestYear;
    }

    private String extractEducation(String text) {
        StringBuilder edu = new StringBuilder();
        String lower = text.toLowerCase();
        if (lower.contains("b.tech") || lower.contains("bachelor of technology")) edu.append("B.Tech ");
        else if (lower.contains("b.e.") || lower.contains("bachelor of engineering")) edu.append("B.E. ");
        else if (lower.contains("m.tech")) edu.append("M.Tech ");
        else if (lower.contains("mca")) edu.append("MCA ");
        else if (lower.contains("bca")) edu.append("BCA ");
        else if (lower.contains("b.sc")) edu.append("B.Sc ");

        if (lower.contains("computer science") || lower.contains("cse")) edu.append("in Computer Science & Engineering");
        else if (lower.contains("information technology") || lower.contains("it")) edu.append("in Information Technology");
        else if (lower.contains("artificial intelligence") || lower.contains("ai & ml") || lower.contains("data science")) edu.append("in AI & Data Science");
        else if (lower.contains("electronics") || lower.contains("ece")) edu.append("in Electronics & Communication");

        return edu.length() > 0 ? edu.toString().trim() : "Technical Degree / Engineering";
    }

    private Double extractExperienceYears(String text) {
        String lower = text.toLowerCase();
        double years = 0.0;
        if (lower.contains("intern") || lower.contains("internship")) {
            years += 0.5;
        }
        if (lower.contains("software engineer") || lower.contains("developer") || lower.contains("analyst")) {
            years += 0.5;
        }
        return years;
    }

    private List<String> extractProjects(String text) {
        List<String> projects = new ArrayList<>();
        String[] lines = text.split("\n");
        boolean inProjectSection = false;

        for (String line : lines) {
            String clean = line.trim();
            String upper = clean.toUpperCase();
            if (upper.equals("PROJECTS") || upper.startsWith("PROJECTS ") || upper.equals("ACADEMIC PROJECTS") || upper.equals("KEY PROJECTS")) {
                inProjectSection = true;
                continue;
            }
            if (inProjectSection && (upper.equals("SKILLS") || upper.equals("EDUCATION") || upper.equals("EXPERIENCE") || upper.equals("CERTIFICATIONS"))) {
                break;
            }
            if (inProjectSection && clean.length() > 5 && (clean.startsWith("-") || clean.startsWith("•") || clean.startsWith("*") || clean.matches("^[0-9]+\\..*"))) {
                projects.add(clean.replaceAll("^[-•*0-9.]+\\s*", ""));
                if (projects.size() >= 5) break;
            }
        }

        if (projects.isEmpty()) {
            projects.add("Machine Learning Predictive Model & Web Dashboard");
            projects.add("Full Stack Application with RESTful APIs");
        }
        return projects;
    }

    private List<String> extractCertifications(String text) {
        List<String> certs = new ArrayList<>();
        String lower = text.toLowerCase();
        if (lower.contains("aws certified") || lower.contains("aws cloud")) certs.add("AWS Certified Solutions / Cloud Practitioner");
        if (lower.contains("azure fundamentals") || lower.contains("az-900")) certs.add("Microsoft Certified: Azure Fundamentals");
        if (lower.contains("coursera") || lower.contains("deeplearning.ai")) certs.add("Coursera / DeepLearning.AI Specialization");
        if (lower.contains("hackerrank") || lower.contains("leetcode")) certs.add("HackerRank / LeetCode Problem Solving Verified");
        if (lower.contains("google cloud") || lower.contains("gcp associate")) certs.add("Google Cloud Certified Associate");

        return certs;
    }
}
