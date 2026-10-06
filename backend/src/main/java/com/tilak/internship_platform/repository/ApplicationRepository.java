package com.tilak.internship_platform.repository;

import com.tilak.internship_platform.entity.Application;
import com.tilak.internship_platform.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserIdOrderByAppliedAtDesc(Long userId);
    List<Application> findByOpportunityId(Long opportunityId);
    Optional<Application> findByUserIdAndOpportunityId(Long userId, Long opportunityId);
    boolean existsByUserIdAndOpportunityId(Long userId, Long opportunityId);
    long countByUserId(Long userId);
    long countByStatus(ApplicationStatus status);
}
