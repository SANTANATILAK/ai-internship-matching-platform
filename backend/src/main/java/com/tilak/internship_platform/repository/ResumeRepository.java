package com.tilak.internship_platform.repository;

import com.tilak.internship_platform.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Resume> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByUserId(Long userId);
}
