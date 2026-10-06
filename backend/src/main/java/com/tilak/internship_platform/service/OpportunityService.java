package com.tilak.internship_platform.service;

import com.tilak.internship_platform.dto.request.OpportunityCreateRequest;
import com.tilak.internship_platform.dto.response.OpportunityDetailResponse;
import com.tilak.internship_platform.entity.*;
import com.tilak.internship_platform.exception.ResourceNotFoundException;
import com.tilak.internship_platform.repository.CompanyRepository;
import com.tilak.internship_platform.repository.OpportunityRepository;
import com.tilak.internship_platform.verification.OpportunityVerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final CompanyRepository companyRepository;
    private final OpportunityVerificationService verificationService;

    public OpportunityService(OpportunityRepository opportunityRepository,
                              CompanyRepository companyRepository,
                              OpportunityVerificationService verificationService) {
        this.opportunityRepository = opportunityRepository;
        this.companyRepository = companyRepository;
        this.verificationService = verificationService;
    }

    @Transactional(readOnly = true)
    public List<OpportunityDetailResponse> getVerifiedOpportunities() {
        return opportunityRepository.findByStatusAndVerificationStatus(OpportunityStatus.ACTIVE, VerificationStatus.VERIFIED)
                .stream()
                .map(OpportunityDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OpportunityDetailResponse getOpportunityById(Long id) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
        return OpportunityDetailResponse.fromEntity(opp);
    }

    @Transactional(readOnly = true)
    public List<OpportunityDetailResponse> searchOpportunities(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getVerifiedOpportunities();
        }
        return opportunityRepository.searchOpportunities(query.trim())
                .stream()
                .map(OpportunityDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OpportunityDetailResponse> getRecentOpportunities() {
        return opportunityRepository.findRecentActiveVerifiedOpportunities()
                .stream()
                .limit(10)
                .map(OpportunityDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OpportunityDetailResponse> getExpiringSoonOpportunities() {
        LocalDateTime threshold = LocalDateTime.now().plusDays(7);
        return opportunityRepository.findExpiringSoonOpportunities(threshold)
                .stream()
                .map(OpportunityDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OpportunityDetailResponse> getOpportunitiesByCompany(Long companyId) {
        return opportunityRepository.findByCompanyId(companyId)
                .stream()
                .map(OpportunityDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OpportunityDetailResponse> filterOpportunities(OpportunityType type,
                                                               WorkType workType,
                                                               String location,
                                                               String skill) {
        List<Opportunity> all = opportunityRepository.findByStatusAndVerificationStatus(OpportunityStatus.ACTIVE, VerificationStatus.VERIFIED);
        return all.stream()
                .filter(o -> type == null || o.getType() == type)
                .filter(o -> workType == null || o.getWorkType() == workType)
                .filter(o -> location == null || location.trim().isEmpty() ||
                        (o.getLocation() != null && o.getLocation().toLowerCase().contains(location.toLowerCase().trim())))
                .filter(o -> skill == null || skill.trim().isEmpty() ||
                        (o.getRequiredSkills() != null && o.getRequiredSkills().toLowerCase().contains(skill.toLowerCase().trim())))
                .map(OpportunityDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public OpportunityDetailResponse createOpportunity(OpportunityCreateRequest req) {
        Company comp = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + req.getCompanyId()));

        Opportunity opp = Opportunity.builder()
                .company(comp)
                .title(req.getTitle().trim())
                .description(req.getDescription())
                .location(req.getLocation())
                .workType(req.getWorkType() != null ? req.getWorkType() : WorkType.HYBRID)
                .type(req.getType() != null ? req.getType() : OpportunityType.INTERNSHIP)
                .stipend(req.getStipend())
                .salary(req.getSalary())
                .requiredSkills(req.getRequiredSkills())
                .graduationYears(req.getGraduationYears())
                .degreeRequirements(req.getDegreeRequirements())
                .branchRequirements(req.getBranchRequirements())
                .experienceRequirement(req.getExperienceRequirement())
                .applyUrl(req.getApplyUrl())
                .sourceUrl(req.getSourceUrl())
                .expiryDate(req.getExpiryDate())
                .status(OpportunityStatus.ACTIVE)
                .sourceType("ADMIN_POSTED")
                .build();

        // Run automated verification
        VerificationStatus vStatus = verificationService.verifyOpportunity(opp);
        opp.setVerificationStatus(vStatus);

        opp = opportunityRepository.save(opp);
        return OpportunityDetailResponse.fromEntity(opp);
    }

    @Transactional
    public OpportunityDetailResponse updateOpportunity(Long id, OpportunityCreateRequest req) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));

        if (req.getCompanyId() != null) {
            Company comp = companyRepository.findById(req.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + req.getCompanyId()));
            opp.setCompany(comp);
        }

        opp.setTitle(req.getTitle().trim());
        opp.setDescription(req.getDescription());
        opp.setLocation(req.getLocation());
        if (req.getWorkType() != null) opp.setWorkType(req.getWorkType());
        if (req.getType() != null) opp.setType(req.getType());
        opp.setStipend(req.getStipend());
        opp.setSalary(req.getSalary());
        opp.setRequiredSkills(req.getRequiredSkills());
        opp.setGraduationYears(req.getGraduationYears());
        opp.setDegreeRequirements(req.getDegreeRequirements());
        opp.setBranchRequirements(req.getBranchRequirements());
        opp.setExperienceRequirement(req.getExperienceRequirement());
        opp.setApplyUrl(req.getApplyUrl());
        opp.setSourceUrl(req.getSourceUrl());
        opp.setExpiryDate(req.getExpiryDate());

        opp = opportunityRepository.save(opp);
        return OpportunityDetailResponse.fromEntity(opp);
    }

    @Transactional
    public void deleteOpportunity(Long id) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
        opportunityRepository.delete(opp);
    }

    @Transactional
    public OpportunityDetailResponse verifyOpportunity(Long id) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
        opp.setVerificationStatus(VerificationStatus.VERIFIED);
        opp.setStatus(OpportunityStatus.ACTIVE);
        return OpportunityDetailResponse.fromEntity(opportunityRepository.save(opp));
    }

    @Transactional
    public OpportunityDetailResponse rejectOpportunity(Long id) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
        opp.setVerificationStatus(VerificationStatus.REJECTED);
        opp.setStatus(OpportunityStatus.REMOVED);
        return OpportunityDetailResponse.fromEntity(opportunityRepository.save(opp));
    }
}
