package com.tilak.internship_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tilak.internship_platform.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}