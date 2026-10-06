-- ==========================================================
-- AI Internship Matching Platform (InternMatch) Database Schema
-- Database: internship_platform
-- ==========================================================

CREATE DATABASE IF NOT EXISTS internship_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE internship_platform;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(25),
    branch VARCHAR(100),
    college VARCHAR(180),
    graduation_year INT,
    location VARCHAR(120),
    preferred_location VARCHAR(120),
    preferred_work_type VARCHAR(50),
    role_preference VARCHAR(120),
    role VARCHAR(30) NOT NULL DEFAULT 'STUDENT',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_grad_year (graduation_year)
);

-- 2. Companies Table
CREATE TABLE IF NOT EXISTS companies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    official_domain VARCHAR(150) NOT NULL,
    career_url VARCHAR(500) NOT NULL,
    description TEXT,
    industry VARCHAR(100),
    location VARCHAR(150),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_status VARCHAR(30) NOT NULL DEFAULT 'REVIEW',
    trust_score INT DEFAULT 50,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_company_name (name),
    INDEX idx_company_domain (official_domain),
    INDEX idx_company_verification (verification_status)
);

-- 3. Resumes Table
CREATE TABLE IF NOT EXISTS resumes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    extracted_text LONGTEXT,
    parsed_skills TEXT,
    education_details TEXT,
    graduation_year INT,
    experience_years DOUBLE DEFAULT 0,
    parsed_projects TEXT,
    parsed_certifications TEXT,
    ats_score INT DEFAULT 0,
    ats_feedback_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_resume_user (user_id)
);

-- 4. Skills Table
CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    normalized_name VARCHAR(100) NOT NULL,
    category VARCHAR(60) DEFAULT 'TECHNICAL',
    INDEX idx_skill_norm (normalized_name)
);

-- 5. Opportunities Table
CREATE TABLE IF NOT EXISTS opportunities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description LONGTEXT,
    location VARCHAR(150),
    work_type VARCHAR(30) NOT NULL DEFAULT 'HYBRID',
    type VARCHAR(30) NOT NULL DEFAULT 'INTERNSHIP',
    stipend VARCHAR(80),
    salary VARCHAR(80),
    required_skills TEXT,
    graduation_years VARCHAR(100),
    degree_requirements VARCHAR(150),
    branch_requirements VARCHAR(150),
    experience_requirement VARCHAR(80),
    apply_url VARCHAR(500) NOT NULL,
    source_url VARCHAR(500),
    posted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date TIMESTAMP NULL,
    last_checked TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    verification_status VARCHAR(30) NOT NULL DEFAULT 'VERIFIED',
    source_type VARCHAR(50) DEFAULT 'OFFICIAL_CAREERS',
    external_id VARCHAR(120),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    INDEX idx_opp_status (status),
    INDEX idx_opp_verification (verification_status),
    INDEX idx_opp_type (type),
    INDEX idx_opp_external (external_id)
);

-- 6. Applications Table
CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    opportunity_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'APPLIED',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_opportunity (user_id, opportunity_id),
    INDEX idx_app_user (user_id),
    INDEX idx_app_status (status)
);

-- 7. Saved Opportunities Table
CREATE TABLE IF NOT EXISTS saved_opportunities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    opportunity_id BIGINT NOT NULL,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_saved_opp (user_id, opportunity_id)
);

-- 8. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'SYSTEM',
    is_read BOOLEAN DEFAULT FALSE,
    link VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notif_user (user_id)
);

-- 9. ATS Results Table
CREATE TABLE IF NOT EXISTS ats_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    resume_id BIGINT NOT NULL,
    ats_score INT NOT NULL,
    strengths_json TEXT,
    weaknesses_json TEXT,
    missing_sections_json TEXT,
    suggestions_json TEXT,
    detected_keywords_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 10. Collector Logs Table
CREATE TABLE IF NOT EXISTS collector_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_name VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    opportunities_found INT DEFAULT 0,
    opportunities_added INT DEFAULT 0,
    opportunities_updated INT DEFAULT 0,
    opportunities_expired INT DEFAULT 0,
    error_message TEXT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
