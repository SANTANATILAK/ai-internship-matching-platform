package com.tilak.internship_platform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tilak.internship_platform.entity.JobApplication;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserIdOrderByAppliedAtDesc(Long userId);
    Optional<JobApplication> findByUserIdAndOpportunityId(Long userId, Long opportunityId);
    long countByUserId(Long userId);
}
