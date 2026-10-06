package com.tilak.internship_platform.service;

import com.tilak.internship_platform.dto.request.CompanyCreateRequest;
import com.tilak.internship_platform.entity.Company;
import com.tilak.internship_platform.entity.VerificationStatus;
import com.tilak.internship_platform.exception.BadRequestException;
import com.tilak.internship_platform.exception.ResourceNotFoundException;
import com.tilak.internship_platform.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Company> getVerifiedCompanies() {
        return companyRepository.findByVerifiedTrue();
    }

    @Transactional(readOnly = true)
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
    }

    @Transactional
    public Company createCompany(CompanyCreateRequest req) {
        if (companyRepository.existsByNameIgnoreCase(req.getName().trim())) {
            throw new BadRequestException("Company with name '" + req.getName() + "' already exists");
        }

        Company comp = Company.builder()
                .name(req.getName().trim())
                .officialDomain(req.getOfficialDomain().trim().toLowerCase())
                .careerUrl(req.getCareerUrl().trim())
                .description(req.getDescription())
                .industry(req.getIndustry())
                .location(req.getLocation())
                .verified(req.isVerified())
                .verificationStatus(req.isVerified() ? VerificationStatus.VERIFIED : VerificationStatus.REVIEW)
                .trustScore(req.isVerified() ? 90 : 50)
                .build();

        return companyRepository.save(comp);
    }

    @Transactional
    public Company verifyCompany(Long id) {
        Company comp = getCompanyById(id);
        comp.setVerified(true);
        comp.setVerificationStatus(VerificationStatus.VERIFIED);
        comp.setTrustScore(95);
        return companyRepository.save(comp);
    }

    @Transactional
    public Company rejectCompany(Long id) {
        Company comp = getCompanyById(id);
        comp.setVerified(false);
        comp.setVerificationStatus(VerificationStatus.REJECTED);
        comp.setTrustScore(10);
        return companyRepository.save(comp);
    }
}
