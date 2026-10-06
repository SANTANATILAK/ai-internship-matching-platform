package com.tilak.internship_platform.repository;

import com.tilak.internship_platform.entity.Company;
import com.tilak.internship_platform.entity.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByNameIgnoreCase(String name);
    Optional<Company> findByOfficialDomainIgnoreCase(String domain);
    boolean existsByNameIgnoreCase(String name);
    List<Company> findByVerificationStatus(VerificationStatus status);
    List<Company> findByVerifiedTrue();
    long countByVerifiedTrue();
    long countByVerificationStatus(VerificationStatus status);
}
