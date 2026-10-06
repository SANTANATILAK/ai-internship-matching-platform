package com.tilak.internship_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ats_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "ats_score", nullable = false)
    private Integer atsScore;

    @Column(name = "strengths_json", columnDefinition = "TEXT")
    private String strengthsJson;

    @Column(name = "weaknesses_json", columnDefinition = "TEXT")
    private String weaknessesJson;

    @Column(name = "missing_sections_json", columnDefinition = "TEXT")
    private String missingSectionsJson;

    @Column(name = "suggestions_json", columnDefinition = "TEXT")
    private String suggestionsJson;

    @Column(name = "detected_keywords_json", columnDefinition = "TEXT")
    private String detectedKeywordsJson;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
