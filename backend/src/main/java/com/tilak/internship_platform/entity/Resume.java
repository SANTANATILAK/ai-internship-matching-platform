package com.tilak.internship_platform.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    private Integer atsScore;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String missingSkills;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(columnDefinition = "TEXT")
    private String detectedSkills;

    private String detectedEducation;
    private Integer detectedGraduationYear;

    private LocalDateTime uploadedAt;

    public Resume() {
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public Integer getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(Integer atsScore) {
        this.atsScore = atsScore;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(String missingSkills) {
        this.missingSkills = missingSkills;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getDetectedSkills() {
        return detectedSkills;
    }

    public void setDetectedSkills(String detectedSkills) {
        this.detectedSkills = detectedSkills;
    }

    public String getDetectedEducation() {
        return detectedEducation;
    }

    public void setDetectedEducation(String detectedEducation) {
        this.detectedEducation = detectedEducation;
    }

    public Integer getDetectedGraduationYear() {
        return detectedGraduationYear;
    }

    public void setDetectedGraduationYear(Integer detectedGraduationYear) {
        this.detectedGraduationYear = detectedGraduationYear;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}