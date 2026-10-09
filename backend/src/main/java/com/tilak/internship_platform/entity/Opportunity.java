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
@Table(name = "opportunities", uniqueConstraints = {
        @UniqueConstraint(name = "uk_opportunity_source_job", columnNames = { "sourceName", "sourceJobId" }),
        @UniqueConstraint(name = "uk_opportunity_canonical_url_hash", columnNames = { "canonicalApplyUrlHash" })
})
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;
    private String country;
    private String department;
    private String workMode;
    private String jobType;
    private String type;

    @Column(columnDefinition = "TEXT")
    private String skills;

    private String graduationYears;

    @Column(columnDefinition = "TEXT")
    private String branch;
    private String stipend;
    private String salary;

    @Column(columnDefinition = "TEXT")
    private String applyUrl;

    @Column(columnDefinition = "TEXT")
    private String sourceUrl;

    private String sourceName;
    private String sourceJobId;
    @Column(length = 64)
    private String canonicalApplyUrlHash;
    private String companyDomain;
    private LocalDateTime postedDate;
    private LocalDateTime deadline;
    private LocalDateTime closedAt;
    private LocalDateTime detectedAt;
    private LocalDateTime lastCheckedAt;
    private LocalDateTime lastUpdatedAt;
    private String verificationStatus;
    private String opportunityStatus;
    private Boolean isActive;

    public Opportunity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getGraduationYears() {
        return graduationYears;
    }

    public void setGraduationYears(String graduationYears) {
        this.graduationYears = graduationYears;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getStipend() {
        return stipend;
    }

    public void setStipend(String stipend) {
        this.stipend = stipend;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getApplyUrl() {
        return applyUrl;
    }

    public void setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceJobId() {
        return sourceJobId;
    }

    public void setSourceJobId(String sourceJobId) {
        if (sourceJobId != null && sourceJobId.length() > 255) {
            this.sourceJobId = sourceJobId.substring(0, 255);
        } else {
            this.sourceJobId = sourceJobId;
        }
    }

    public String getCanonicalApplyUrlHash() {
        return canonicalApplyUrlHash;
    }

    public void setCanonicalApplyUrlHash(String canonicalApplyUrlHash) {
        this.canonicalApplyUrlHash = canonicalApplyUrlHash;
    }

    public String getCompanyDomain() {
        return companyDomain;
    }

    public void setCompanyDomain(String companyDomain) {
        this.companyDomain = companyDomain;
    }

    public LocalDateTime getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDateTime postedDate) {
        this.postedDate = postedDate;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getOpportunityStatus() {
        return opportunityStatus;
    }

    public void setOpportunityStatus(String opportunityStatus) {
        this.opportunityStatus = opportunityStatus;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}