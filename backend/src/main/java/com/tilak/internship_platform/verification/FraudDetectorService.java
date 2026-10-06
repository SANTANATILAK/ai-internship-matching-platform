package com.tilak.internship_platform.verification;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.entity.VerificationStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FraudDetectorService {

    private static final List<String> FRAUD_KEYWORDS = Arrays.asList(
            "registration fee", "security deposit", "training charge", "pay to apply",
            "100% placement guarantee", "job guarantee after training",
            "consultancy", "placement agency", "recruitment agency",
            "mlm", "network marketing", "data entry work from home",
            "telegram group", "join whatsapp group to apply", "dm on whatsapp",
            "paytm to apply", "send money"
    );

    private static final List<String> SUSPICIOUS_DOMAINS = Arrays.asList(
            "bit.ly", "tinyurl.com", "cutt.ly", "t.co", "is.gd", "rb.gy", "shorturl.at"
    );

    @Getter
    @Builder
    public static class FraudCheckResult {
        private boolean suspicious;
        private List<String> flaggedReasons;
        private VerificationStatus recommendedStatus;
    }

    public FraudCheckResult inspect(Opportunity opportunity) {
        List<String> reasons = new ArrayList<>();
        String textToInspect = (opportunity.getTitle() + " " +
                opportunity.getDescription() + " " +
                opportunity.getApplyUrl()).toLowerCase();

        // 1. Check for suspicious keywords
        for (String keyword : FRAUD_KEYWORDS) {
            if (textToInspect.contains(keyword)) {
                reasons.add("Contains suspicious keyword/phrase: '" + keyword + "'");
            }
        }

        // 2. Check for URL shorteners in apply URL
        String applyUrl = opportunity.getApplyUrl() != null ? opportunity.getApplyUrl().toLowerCase() : "";
        for (String shortDomain : SUSPICIOUS_DOMAINS) {
            if (applyUrl.contains(shortDomain)) {
                reasons.add("Uses suspicious URL shortener: '" + shortDomain + "'");
            }
        }

        // 3. Check for non-HTTPS apply URL
        if (applyUrl.startsWith("http://")) {
            reasons.add("Insecure HTTP protocol used in application link");
        }

        // 4. Determine status
        VerificationStatus status;
        if (reasons.isEmpty()) {
            status = VerificationStatus.VERIFIED;
        } else if (reasons.stream().anyMatch(r -> r.contains("registration fee") || r.contains("security deposit") || r.contains("send money"))) {
            status = VerificationStatus.REJECTED;
        } else {
            status = VerificationStatus.REVIEW;
        }

        return FraudCheckResult.builder()
                .suspicious(!reasons.isEmpty())
                .flaggedReasons(reasons)
                .recommendedStatus(status)
                .build();
    }
}
