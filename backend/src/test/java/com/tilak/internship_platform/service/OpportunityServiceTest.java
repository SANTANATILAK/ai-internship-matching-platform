package com.tilak.internship_platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.repository.OpportunityRepository;

class OpportunityServiceTest {

    private OpportunityRepository repository;
    private OpportunityService service;

    @BeforeEach
    void setUp() {
        repository = mock(OpportunityRepository.class);
        service = new OpportunityService(repository, "Acme=acme.example");
        when(repository.findByCompanyIgnoreCaseAndTitleIgnoreCase(any(), any()))
                .thenReturn(List.of());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void verifiesOnlyConfiguredCompanyAndOfficialHttpsUrls() {
        Opportunity opportunity = opportunity();

        Opportunity saved = service.saveCollectedOpportunity(opportunity);

        assertEquals("VERIFIED", saved.getVerificationStatus());
        assertEquals("OPEN", saved.getOpportunityStatus());
        assertEquals("India", saved.getCountry());
    }

    @Test
    void keepsManuallySubmittedOpportunityInReview() {
        Opportunity saved = service.saveOpportunity(opportunity());

        assertEquals("REVIEW", saved.getVerificationStatus());
    }

    @Test
    void rejectsApplicationUrlsOutsideTheOfficialCompanyDomain() {
        Opportunity opportunity = opportunity();
        opportunity.setApplyUrl("https://jobs-unrelated.example/apply/1");

        Opportunity saved = service.saveCollectedOpportunity(opportunity);

        assertEquals("REVIEW", saved.getVerificationStatus());
    }

    private Opportunity opportunity() {
        Opportunity opportunity = new Opportunity();
        opportunity.setCompany("Acme");
        opportunity.setTitle("Software Engineering Intern");
        opportunity.setCountry("India");
        opportunity.setCompanyDomain("acme.example");
        opportunity.setApplyUrl("https://careers.acme.example/jobs/1");
        opportunity.setSourceUrl("https://careers.acme.example/jobs/1");
        opportunity.setIsActive(true);
        return opportunity;
    }
}