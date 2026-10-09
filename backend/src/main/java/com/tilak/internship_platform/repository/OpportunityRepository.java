package com.tilak.internship_platform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tilak.internship_platform.entity.Opportunity;

public interface OpportunityRepository
                extends JpaRepository<Opportunity, Long>, JpaSpecificationExecutor<Opportunity> {

        List<Opportunity> findByIsActiveTrue();

        List<Opportunity> findByIsActiveTrueAndCountryIgnoreCase(
                        String country);

        List<Opportunity> findByIsActiveTrueAndCountryIgnoreCaseAndVerificationStatusAndOpportunityStatus(
                        String country,
                        String verificationStatus,
                        String opportunityStatus);

        List<Opportunity> findBySourceNameIgnoreCase(String sourceName);

        List<Opportunity> findByCompanyIgnoreCaseAndTitleIgnoreCase(
                        String company,
                        String title);

        Optional<Opportunity> findFirstBySourceNameIgnoreCaseAndSourceJobId(
                        String sourceName, String sourceJobId);

        Optional<Opportunity> findFirstByCanonicalApplyUrlHash(String canonicalApplyUrlHash);
}