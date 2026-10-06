package com.tilak.internship_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;

    @Column(name = "extracted_text", columnDefinition = "LONGTEXT")
    private String extractedText;

    @Column(name = "parsed_skills", columnDefinition = "TEXT")
    private String parsedSkills;

    @Column(name = "education_details", columnDefinition = "TEXT")
    private String educationDetails;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "experience_years")
    @Builder.Default
    private Double experienceYears = 0.0;

    @Column(name = "parsed_projects", columnDefinition = "TEXT")
    private String parsedProjects;

    @Column(name = "parsed_certifications", columnDefinition = "TEXT")
    private String parsedCertifications;

    @Column(name = "ats_score")
    @Builder.Default
    private Integer atsScore = 0;

    @Column(name = "ats_feedback_json", columnDefinition = "TEXT")
    private String atsFeedbackJson;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
