package com.tilak.internship_platform.repository;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.entity.OpportunityStatus;
import com.tilak.internship_platform.entity.VerificationStatus;
import com.tilak.internship_platform.entity.OpportunityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    Optional<Opportunity> findByExternalId(String externalId);

    List<Opportunity> findByStatusAndVerificationStatus(OpportunityStatus status, VerificationStatus verificationStatus);

    List<Opportunity> findByStatus(OpportunityStatus status);

    List<Opportunity> findByVerificationStatus(VerificationStatus verificationStatus);

    List<Opportunity> findByCompanyId(Long companyId);

    @Query("SELECT o FROM Opportunity o WHERE o.status = 'ACTIVE' AND o.verificationStatus = 'VERIFIED' ORDER BY o.postedDate DESC")
    List<Opportunity> findRecentActiveVerifiedOpportunities();

    @Query("SELECT o FROM Opportunity o WHERE o.status = 'ACTIVE' AND o.expiryDate IS NOT NULL AND o.expiryDate <= :expiryThreshold ORDER BY o.expiryDate ASC")
    List<Opportunity> findExpiringSoonOpportunities(@Param("expiryThreshold") LocalDateTime expiryThreshold);

    @Query("SELECT o FROM Opportunity o WHERE o.status = 'ACTIVE' AND o.expiryDate IS NOT NULL AND o.expiryDate < :now")
    List<Opportunity> findExpiredOpportunities(@Param("now") LocalDateTime now);

    @Query("SELECT o FROM Opportunity o WHERE o.status = 'ACTIVE' AND o.verificationStatus = 'VERIFIED' AND " +
           "(LOWER(o.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.company.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.requiredSkills) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.location) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Opportunity> searchOpportunities(@Param("query") String query);

    long countByStatus(OpportunityStatus status);

    long countByVerificationStatus(VerificationStatus verificationStatus);

    long countByStatusAndVerificationStatus(OpportunityStatus status, VerificationStatus verificationStatus);
}
