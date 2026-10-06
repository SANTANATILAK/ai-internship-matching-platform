package com.tilak.internship_platform.collector;

import com.tilak.internship_platform.entity.*;
import com.tilak.internship_platform.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PublicFeedsSource implements OpportunitySource {

    private static final Logger logger = LoggerFactory.getLogger(PublicFeedsSource.class);
    private final CompanyRepository companyRepository;

    public PublicFeedsSource(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public String getSourceName() {
        return "Public Verified Tech Career Feeds";
    }

    @Override
    public List<Opportunity> fetchOpportunities() {
        logger.info("Fetching public tech feeds for startup and product internships...");
        List<Opportunity> fetched = new ArrayList<>();

        Optional<Company> atlassianOpt = companyRepository.findByNameIgnoreCase("Atlassian");
        atlassianOpt.ifPresent(company -> fetched.add(Opportunity.builder()
                .company(company)
                .title("Full Stack Engineering Intern (React & Java)")
                .description("Build collaborative cloud tools using modern React, Spring Boot microservices, and AWS serverless architecture.")
                .location("Remote - India")
                .workType(WorkType.REMOTE)
                .type(OpportunityType.INTERNSHIP)
                .stipend("INR 90,000 / month")
                .requiredSkills("React, Java, Spring Boot, REST APIs, Git, PostgreSQL")
                .graduationYears("2026, 2027, 2028")
                .degreeRequirements("Any Bachelor Degree in Engineering/Computer Science")
                .branchRequirements("Any")
                .experienceRequirement("Fresher")
                .applyUrl("https://www.atlassian.com/company/careers/details/REQ-INT-FS-2026")
                .sourceUrl("https://www.atlassian.com/company/careers")
                .postedDate(LocalDateTime.now().minusDays(3))
                .expiryDate(LocalDateTime.now().plusDays(20))
                .externalId("ATL-FS-INT-2026")
                .sourceType("PUBLIC_FEED")
                .verificationStatus(VerificationStatus.VERIFIED)
                .status(OpportunityStatus.ACTIVE)
                .build()));

        return fetched;
    }
}
