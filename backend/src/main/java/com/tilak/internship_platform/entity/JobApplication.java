package com.tilak.internship_platform.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "job_applications", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_opportunity_applied", columnNames = { "userId", "opportunityId" })
})
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long opportunityId;

    private String status; // APPLIED, INTERVIEWING, OFFERED, REJECTED

    private LocalDateTime appliedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public JobApplication() {
    }

    public JobApplication(Long userId, Long opportunityId, String status) {
        this.userId = userId;
        this.opportunityId = opportunityId;
        this.status = status != null ? status : "APPLIED";
        this.appliedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
