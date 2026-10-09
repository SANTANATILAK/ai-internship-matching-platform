package com.tilak.internship_platform.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 512)
    private String normalizedName;

    private String officialDomain;
    @Column(columnDefinition = "TEXT")
    private String careersUrl;
    @Column(columnDefinition = "TEXT")
    private String acceptedBranches;
    private String verificationStatus = "REVIEW";
    private Boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastCheckedAt;

    public Company() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public String getOfficialDomain() {
        return officialDomain;
    }

    public void setOfficialDomain(String officialDomain) {
        this.officialDomain = officialDomain;
    }

    public String getCareersUrl() {
        return careersUrl;
    }

    public void setCareersUrl(String careersUrl) {
        this.careersUrl = careersUrl;
    }

    public String getAcceptedBranches() {
        return acceptedBranches;
    }

    public void setAcceptedBranches(String acceptedBranches) {
        this.acceptedBranches = acceptedBranches;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }
}