package com.tilak.internship_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tilak.internship_platform.entity.Internship;

public interface InternshipRepository extends JpaRepository<Internship, Long> {
}