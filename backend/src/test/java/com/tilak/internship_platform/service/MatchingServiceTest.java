package com.tilak.internship_platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.repository.InternshipRepository;
import com.tilak.internship_platform.repository.OpportunityRepository;

class MatchingServiceTest {

        @Test
        void matchesVerifiedOpportunitiesForEligibleGraduationYearAndBranch() {
                InternshipRepository internshipRepository = mock(InternshipRepository.class);
                OpportunityRepository opportunityRepository = mock(OpportunityRepository.class);
                when(internshipRepository.findAll()).thenReturn(List.of());
                when(opportunityRepository
                                .findByIsActiveTrueAndCountryIgnoreCaseAndVerificationStatusAndOpportunityStatus(
                                                "India", "VERIFIED", "OPEN"))
                                .thenReturn(List.of(
                                                opportunity("2028, 2029", "CSE", "VERIFIED"),
                                                opportunity("2027", "CSE", "VERIFIED"),
                                                opportunity("2028", "Mechanical Engineering", "VERIFIED")));
                when(opportunityRepository
                                .findByIsActiveTrueAndCountryIgnoreCaseAndVerificationStatusAndOpportunityStatus(
                                                "India", "REVIEW", "OPEN"))
                                .thenReturn(List.of(opportunity(null, null, "REVIEW")));
                MatchingService matchingService = new MatchingService(
                                internshipRepository, opportunityRepository);

                List<Map<String, Object>> matches = matchingService.matchSkills(
                                List.of("Java", "Spring Boot"), 2028, "CSE");

                assertEquals(2, matches.size());
                assertEquals("Acme", matches.get(0).get("company"));
                assertEquals(100.0, matches.get(0).get("matchPercentage"));
                assertEquals("REVIEW", matches.get(1).get("verificationStatus"));
                assertEquals(100.0, matches.get(1).get("matchPercentage"));
        }

        private Opportunity opportunity(String graduationYears, String branch, String verificationStatus) {
                Opportunity opportunity = new Opportunity();
                opportunity.setId(1L);
                opportunity.setCompany("Acme");
                opportunity.setTitle("Software Intern");
                opportunity.setSkills("Java, Spring Boot");
                opportunity.setGraduationYears(graduationYears);
                opportunity.setBranch(branch);
                opportunity.setVerificationStatus(verificationStatus);
                return opportunity;
        }
}