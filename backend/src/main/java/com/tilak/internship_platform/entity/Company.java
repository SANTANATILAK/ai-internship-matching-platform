package com.tilak.internship_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(name = "official_domain", nullable = false, length = 150)
    private String officialDomain;

    @Column(name = "career_url", nullable = false, length = 500)
    private String careerUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String industry;

    @Column(length = 150)
    private String location;

    @Column(nullable = false)
    @Builder.Default
    private boolean verified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 30)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.REVIEW;

    @Column(name = "trust_score")
    @Builder.Default
    private Integer trustScore = 50;

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
