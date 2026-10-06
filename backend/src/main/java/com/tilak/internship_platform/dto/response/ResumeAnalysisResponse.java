package com.tilak.internship_platform.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysisResponse {
    private Long resumeId;
    private String originalFilename;
    private String extractedName;
    private String extractedEmail;
    private String extractedPhone;
    private List<String> extractedSkills;
    private String educationDetails;
    private Integer graduationYear;
    private Double experienceYears;
    private List<String> extractedProjects;
    private List<String> extractedCertifications;
    private Integer atsScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> missingSections;
    private List<String> suggestions;
    private List<String> detectedKeywords;
    private LocalDateTime uploadedAt;
}
