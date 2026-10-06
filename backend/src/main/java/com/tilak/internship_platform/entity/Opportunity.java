package com.tilak.internship_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "opportunities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(length = 150)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", nullable = false, length = 30)
    @Builder.Default
    private WorkType workType = WorkType.HYBRID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private OpportunityType type = OpportunityType.INTERNSHIP;

    @Column(length = 80)
    private String stipend;

    @Column(length = 80)
    private String salary;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @Column(name = "graduation_years", length = 100)
    private String graduationYears;

    @Column(name = "degree_requirements", length = 150)
    private String degreeRequirements;

    @Column(name = "branch_requirements", length = 150)
    private String branchRequirements;

    @Column(name = "experience_requirement", length = 80)
    private String experienceRequirement;

    @Column(name = "apply_url", nullable = false, length = 500)
    private String applyUrl;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private OpportunityStatus status = OpportunityStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 30)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.VERIFIED;

    @Column(name = "source_type", length = 50)
    @Builder.Default
    private String sourceType = "OFFICIAL_CAREERS";

    @Column(name = "external_id", length = 120)
    private String externalId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.lastChecked == null) this.lastChecked = LocalDateTime.now();
        if (this.lastUpdated == null) this.lastUpdated = LocalDateTime.now();
        if (this.postedDate == null) this.postedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
