package com.tilak.internship_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "collector_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_name", nullable = false, length = 100)
    private String sourceName;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "opportunities_found")
    @Builder.Default
    private int opportunitiesFound = 0;

    @Column(name = "opportunities_added")
    @Builder.Default
    private int opportunitiesAdded = 0;

    @Column(name = "opportunities_updated")
    @Builder.Default
    private int opportunitiesUpdated = 0;

    @Column(name = "opportunities_expired")
    @Builder.Default
    private int opportunitiesExpired = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
