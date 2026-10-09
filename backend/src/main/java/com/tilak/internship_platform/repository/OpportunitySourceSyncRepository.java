package com.tilak.internship_platform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tilak.internship_platform.entity.OpportunitySourceSync;

public interface OpportunitySourceSyncRepository extends JpaRepository<OpportunitySourceSync, Long> {
    Optional<OpportunitySourceSync> findBySourceNameIgnoreCase(String sourceName);
}