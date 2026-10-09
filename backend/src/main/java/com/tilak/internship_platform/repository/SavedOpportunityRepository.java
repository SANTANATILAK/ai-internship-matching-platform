package com.tilak.internship_platform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tilak.internship_platform.entity.SavedOpportunity;

@Repository
public interface SavedOpportunityRepository extends JpaRepository<SavedOpportunity, Long> {
    List<SavedOpportunity> findByUserIdOrderBySavedAtDesc(Long userId);
    Optional<SavedOpportunity> findByUserIdAndOpportunityId(Long userId, Long opportunityId);
    boolean existsByUserIdAndOpportunityId(Long userId, Long opportunityId);
    void deleteByUserIdAndOpportunityId(Long userId, Long opportunityId);
}
