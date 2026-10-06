package com.tilak.internship_platform.collector;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.repository.OpportunityRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

@Service
public class OpportunityDeduplicationService {

    private final OpportunityRepository opportunityRepository;

    public OpportunityDeduplicationService(OpportunityRepository opportunityRepository) {
        this.opportunityRepository = opportunityRepository;
    }

    public String normalizeUrl(String url) {
        if (url == null || url.trim().isEmpty()) return "";
        try {
            URI uri = URI.create(url.trim());
            String path = uri.getPath() != null ? uri.getPath() : "";
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            return (uri.getScheme() != null ? uri.getScheme().toLowerCase() : "https") + "://" +
                    (uri.getHost() != null ? uri.getHost().toLowerCase() : "") +
                    path;
        } catch (Exception e) {
            return url.trim().toLowerCase();
        }
    }

    public Optional<Opportunity> findExistingDuplicate(Opportunity opp) {
        // 1. Check by external ID if present
        if (opp.getExternalId() != null && !opp.getExternalId().trim().isEmpty()) {
            Optional<Opportunity> byExternalId = opportunityRepository.findByExternalId(opp.getExternalId());
            if (byExternalId.isPresent()) {
                return byExternalId;
            }
        }

        // 2. Check by Company + Title match
        if (opp.getCompany() != null && opp.getCompany().getId() != null) {
            var existingList = opportunityRepository.findByCompanyId(opp.getCompany().getId());
            for (Opportunity existing : existingList) {
                if (isSameOpportunity(existing, opp)) {
                    return Optional.of(existing);
                }
            }
        }

        return Optional.empty();
    }

    private boolean isSameOpportunity(Opportunity o1, Opportunity o2) {
        if (!o1.getTitle().trim().equalsIgnoreCase(o2.getTitle().trim())) {
            return false;
        }

        String norm1 = normalizeUrl(o1.getApplyUrl());
        String norm2 = normalizeUrl(o2.getApplyUrl());
        if (norm1.equalsIgnoreCase(norm2)) {
            return true;
        }

        String loc1 = o1.getLocation() != null ? o1.getLocation().trim().toLowerCase() : "";
        String loc2 = o2.getLocation() != null ? o2.getLocation().trim().toLowerCase() : "";
        return loc1.equals(loc2);
    }
}
