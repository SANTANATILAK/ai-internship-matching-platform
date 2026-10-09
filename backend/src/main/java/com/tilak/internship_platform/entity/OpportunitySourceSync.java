package com.tilak.internship_platform.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "opportunity_source_sync")
public class OpportunitySourceSync {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sourceName;
    private String status;
    private LocalDateTime lastAttemptAt;
    private LocalDateTime lastSuccessfulAt;
    private Integer lastFetchedCount = 0;
    private Integer lastProcessedCount = 0;
    private Integer lastInsertedCount = 0;
    private Integer lastUpdatedCount = 0;
    @Column(columnDefinition = "TEXT")
    private String lastFailureMessage;

    public OpportunitySourceSync() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastAttemptAt() {
        return lastAttemptAt;
    }

    public void setLastAttemptAt(LocalDateTime lastAttemptAt) {
        this.lastAttemptAt = lastAttemptAt;
    }

    public LocalDateTime getLastSuccessfulAt() {
        return lastSuccessfulAt;
    }

    public void setLastSuccessfulAt(LocalDateTime lastSuccessfulAt) {
        this.lastSuccessfulAt = lastSuccessfulAt;
    }

    public Integer getLastFetchedCount() {
        return lastFetchedCount;
    }

    public void setLastFetchedCount(Integer lastFetchedCount) {
        this.lastFetchedCount = lastFetchedCount;
    }

    public Integer getLastProcessedCount() {
        return lastProcessedCount;
    }

    public void setLastProcessedCount(Integer lastProcessedCount) {
        this.lastProcessedCount = lastProcessedCount;
    }

    public Integer getLastInsertedCount() {
        return lastInsertedCount;
    }

    public void setLastInsertedCount(Integer lastInsertedCount) {
        this.lastInsertedCount = lastInsertedCount;
    }

    public Integer getLastUpdatedCount() {
        return lastUpdatedCount;
    }

    public void setLastUpdatedCount(Integer lastUpdatedCount) {
        this.lastUpdatedCount = lastUpdatedCount;
    }

    public String getLastFailureMessage() {
        return lastFailureMessage;
    }

    public void setLastFailureMessage(String lastFailureMessage) {
        this.lastFailureMessage = lastFailureMessage;
    }
}