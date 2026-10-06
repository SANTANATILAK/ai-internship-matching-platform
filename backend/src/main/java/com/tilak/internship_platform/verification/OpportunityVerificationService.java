package com.tilak.internship_platform.verification;

import com.tilak.internship_platform.entity.Company;
import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.entity.VerificationStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
public class OpportunityVerificationService {

    private final FraudDetectorService fraudDetectorService;

    private static final List<String> TRUSTED_ATS_DOMAINS = Arrays.asList(
            "myworkdayjobs.com", "greenhouse.io", "lever.co", "smartrecruiters.com",
            "taleo.net", "ashbyhq.com", "icims.com", "jobvite.com", "brassring.com",
            "linkedin.com", "careers.google.com", "amazon.jobs", "careers.microsoft.com"
    );

    public OpportunityVerificationService(FraudDetectorService fraudDetectorService) {
        this.fraudDetectorService = fraudDetectorService;
    }

    public VerificationStatus verifyOpportunity(Opportunity opp) {
        // Step 1: Run fraud inspection
        FraudDetectorService.FraudCheckResult fraudResult = fraudDetectorService.inspect(opp);
        if (fraudResult.getRecommendedStatus() == VerificationStatus.REJECTED) {
            return VerificationStatus.REJECTED;
        }

        Company company = opp.getCompany();
        if (company == null) {
            return VerificationStatus.REVIEW;
        }

        // Step 2: Validate URL Domain alignment
        String applyUrl = opp.getApplyUrl();
        if (applyUrl == null || applyUrl.isEmpty()) {
            return VerificationStatus.REVIEW;
        }

        try {
            URI uri = URI.create(applyUrl);
            String host = uri.getHost() != null ? uri.getHost().toLowerCase() : "";

            // Check if domain matches company official domain
            String officialDomain = company.getOfficialDomain().toLowerCase();
            boolean domainMatches = host.endsWith(officialDomain) || host.contains(officialDomain);

            // Or check if domain is a trusted enterprise ATS system
            boolean isTrustedAts = TRUSTED_ATS_DOMAINS.stream().anyMatch(host::contains);

            if ((domainMatches || isTrustedAts) && !fraudResult.isSuspicious()) {
                return VerificationStatus.VERIFIED;
            } else if (fraudResult.isSuspicious()) {
                return VerificationStatus.REVIEW;
            } else {
                return VerificationStatus.VERIFIED;
            }
        } catch (Exception e) {
            return VerificationStatus.REVIEW;
        }
    }
}
