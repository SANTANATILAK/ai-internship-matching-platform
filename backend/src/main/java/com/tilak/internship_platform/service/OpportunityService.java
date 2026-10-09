package com.tilak.internship_platform.service;

import java.time.LocalDateTime;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tilak.internship_platform.entity.Company;
import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.repository.CompanyRepository;
import com.tilak.internship_platform.repository.OpportunityRepository;

@Service
public class OpportunityService {

    private final OpportunityRepository repository;
    private final CompanyRepository companyRepository;
    private final Map<String, String> verifiedCompanyDomains;
    private final Set<String> excludedKeywords;
    private volatile LocalDateTime lastRefreshAt;

    @Autowired
    public OpportunityService(
            OpportunityRepository repository,
            CompanyRepository companyRepository,
            @Value("${opportunities.verified-company-domains:}") String configuredDomains,
            @Value("${opportunities.exclude-keywords:consultancy,placement agency,staffing agency,training institute,mlm,registration fee,pay to apply,job fee}") String configuredExclusions) {

        this.repository = repository;
        this.companyRepository = companyRepository;
        this.verifiedCompanyDomains = parseCompanyDomains(configuredDomains);
        this.excludedKeywords = java.util.Arrays.stream(configuredExclusions.split(","))
                .map(this::normalize)
                .filter(keyword -> !keyword.isBlank())
                .collect(Collectors.toSet());
    }

    public OpportunityService(
            OpportunityRepository repository,
            String configuredDomains) {
        this.repository = repository;
        this.companyRepository = null;
        this.verifiedCompanyDomains = parseCompanyDomains(configuredDomains);
        this.excludedKeywords = Set.of("consultancy", "placement agency", "staffing agency",
                "training institute", "mlm", "registration fee", "pay to apply", "job fee");
    }

    public List<Opportunity> getActiveOpportunities() {
        List<Opportunity> verified = repository
                .findByIsActiveTrueAndCountryIgnoreCaseAndVerificationStatusAndOpportunityStatus(
                        "India", "VERIFIED", "OPEN");
        if (verified != null && !verified.isEmpty()) {
            return verified;
        }
        return repository.findByIsActiveTrue();
    }

    public Opportunity saveOpportunity(
            Opportunity opportunity) {

        return saveOpportunityInternal(opportunity, false).opportunity();
    }

    public Opportunity saveCollectedOpportunity(
            Opportunity opportunity) {

        return saveCollectedOpportunityWithStats(opportunity).opportunity();
    }

    public OpportunitySaveResult saveCollectedOpportunityWithStats(Opportunity opportunity) {
        return saveOpportunityInternal(opportunity, true);
    }

    @Transactional
    private OpportunitySaveResult saveOpportunityInternal(
            Opportunity opportunity,
            boolean collectedFromConfiguredSource) {

        LocalDateTime now = LocalDateTime.now();

        if (!collectedFromConfiguredSource
                && (opportunity.getCountry() == null
                        || opportunity.getCountry().isBlank())) {

            opportunity.setCountry("India");
        }

        opportunity.setVerificationStatus(
                collectedFromConfiguredSource
                        ? verify(opportunity)
                        : "REVIEW");
        opportunity.setJobType(firstNonBlank(opportunity.getJobType(), opportunity.getType()));
        opportunity.setType(firstNonBlank(opportunity.getType(), opportunity.getJobType()));
        opportunity.setCanonicalApplyUrlHash(canonicalApplyUrlHash(opportunity.getApplyUrl()));
        opportunity.setLastCheckedAt(now);
        opportunity.setOpportunityStatus(resolveStatus(opportunity, now));
        opportunity.setIsActive("OPEN".equals(opportunity.getOpportunityStatus()));

        Opportunity old = findExistingOpportunity(opportunity);

        if (old != null) {
            if ("VERIFIED".equals(old.getVerificationStatus())
                    && !"VERIFIED".equals(opportunity.getVerificationStatus())
                    && (!collectedFromConfiguredSource
                            || !"Configured Official Career Feeds".equals(opportunity.getSourceName()))) {
                old.setLastCheckedAt(now);
                return new OpportunitySaveResult(repository.save(old), false, false);
            }
            boolean changed = copyChangedFields(opportunity, old);
            changed |= !Objects.equals(old.getVerificationStatus(), opportunity.getVerificationStatus())
                    || !Objects.equals(old.getOpportunityStatus(), opportunity.getOpportunityStatus());
            old.setLastCheckedAt(now);
            old.setVerificationStatus(opportunity.getVerificationStatus());
            old.setOpportunityStatus(opportunity.getOpportunityStatus());
            old.setIsActive(opportunity.getIsActive());
            if ("CLOSED".equals(old.getOpportunityStatus()) || "EXPIRED".equals(old.getOpportunityStatus())) {
                if (old.getClosedAt() == null)
                    old.setClosedAt(now);
            } else {
                old.setClosedAt(null);
            }
            if (old.getDetectedAt() == null) {
                old.setDetectedAt(now);
                changed = true;
            }
            if (changed || old.getLastUpdatedAt() == null) {
                old.setLastUpdatedAt(now);
            }
            Opportunity saved = repository.save(old);
            upsertCompany(saved, now);
            return new OpportunitySaveResult(saved, false, changed);
        }

        opportunity.setDetectedAt(now);
        opportunity.setLastUpdatedAt(now);
        if ("CLOSED".equals(opportunity.getOpportunityStatus())
                || "EXPIRED".equals(opportunity.getOpportunityStatus())) {
            opportunity.setClosedAt(now);
        }
        Opportunity saved = repository.save(opportunity);
        upsertCompany(saved, now);
        return new OpportunitySaveResult(saved, true, false);
    }

    private Opportunity findExistingOpportunity(Opportunity opportunity) {
        if (opportunity.getSourceName() != null && opportunity.getSourceJobId() != null
                && !opportunity.getSourceJobId().isBlank()) {
            Opportunity bySourceId = repository
                    .findFirstBySourceNameIgnoreCaseAndSourceJobId(
                            opportunity.getSourceName(), opportunity.getSourceJobId())
                    .orElse(null);
            if (bySourceId != null)
                return bySourceId;
        }
        if (opportunity.getCanonicalApplyUrlHash() != null) {
            Opportunity byUrl = repository
                    .findFirstByCanonicalApplyUrlHash(opportunity.getCanonicalApplyUrlHash())
                    .orElse(null);
            if (byUrl != null)
                return byUrl;
        }
        return repository.findByCompanyIgnoreCaseAndTitleIgnoreCase(
                opportunity.getCompany(), opportunity.getTitle()).stream()
                .filter(item -> sameRole(item, opportunity))
                .findFirst().orElse(null);
    }

    private void upsertCompany(Opportunity opportunity, LocalDateTime now) {
        if (companyRepository == null || opportunity.getCompany() == null
                || opportunity.getCompany().isBlank())
            return;

        String normalizedName = normalizeCompanyName(opportunity.getCompany());
        Company company = companyRepository.findByNormalizedName(normalizedName)
                .orElseGet(() -> {
                    Company created = new Company();
                    created.setName(opportunity.getCompany().trim());
                    created.setNormalizedName(normalizedName);
                    created.setCreatedAt(now);
                    created.setActive(true);
                    return created;
                });

        String configuredDomain = verifiedCompanyDomains.get(normalizedName);
        if (configuredDomain != null) {
            company.setOfficialDomain(configuredDomain);
        }
        if (opportunity.getBranch() != null && !opportunity.getBranch().isBlank()) {
            company.setAcceptedBranches(opportunity.getBranch());
        }
        if ("VERIFIED".equals(opportunity.getVerificationStatus())
                && configuredDomain != null) {
            company.setVerificationStatus("VERIFIED");
            company.setCareersUrl(opportunity.getSourceUrl());
        } else if (!"VERIFIED".equals(company.getVerificationStatus())) {
            company.setVerificationStatus("REVIEW");
        }
        company.setLastCheckedAt(now);
        company.setUpdatedAt(now);
        companyRepository.save(company);
    }

    private String normalizeCompanyName(String value) {
        return normalize(value).replaceAll("\\s+", " ");
    }

    private String canonicalApplyUrlHash(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            URI uri = new URI(value.trim());
            if (!"http".equalsIgnoreCase(uri.getScheme())
                    && !"https".equalsIgnoreCase(uri.getScheme()))
                return null;
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase().replaceFirst("^www\\.", "");
            String path = uri.getPath() == null ? "" : uri.getPath().replaceAll("/+$", "");
            String query = uri.getQuery();
            if (query != null) {
                query = java.util.Arrays.stream(query.split("&"))
                        .filter(param -> !param.toLowerCase().matches("(utm_.*|ref|source|gh_src)=.*"))
                        .sorted().collect(Collectors.joining("&"));
            }
            String canonical = host + path + (query == null || query.isBlank() ? "" : "?" + query);
            if (path.isBlank() || path.equalsIgnoreCase("/careers")
                    || path.equalsIgnoreCase("/jobs") || path.equalsIgnoreCase("/search"))
                return null;
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(canonical.toLowerCase().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (URISyntaxException | NoSuchAlgorithmException exception) {
            return null;
        }
    }

    private String firstNonBlank(String first, String fallback) {
        return first == null || first.isBlank() ? fallback : first;
    }

    public void recordRefresh() {
        lastRefreshAt = LocalDateTime.now();
    }

    public Map<String, Object> getOpportunitySummary() {
        List<Opportunity> opportunities = getActiveOpportunities();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> summary = new HashMap<>();
        summary.put("opportunities", opportunities);
        summary.put("pendingVerification", repository
                .findByIsActiveTrueAndCountryIgnoreCaseAndVerificationStatusAndOpportunityStatus(
                        "India", "REVIEW", "OPEN"));
        summary.put("lastUpdated", lastRefreshAt);
        summary.put("newToday", opportunities.stream()
                .filter(item -> isToday(item.getDetectedAt(), now)).count());
        summary.put("recentlyUpdated", opportunities.stream()
                .filter(item -> item.getLastUpdatedAt() != null
                        && item.getLastUpdatedAt().isAfter(now.minusDays(7)))
                .count());
        summary.put("expiringSoon", opportunities.stream()
                .filter(item -> item.getDeadline() != null
                        && !item.getDeadline().isBefore(now)
                        && !item.getDeadline().isAfter(now.plusDays(7)))
                .count());
        summary.put("verifiedCompanies", opportunities.stream()
                .map(Opportunity::getCompany)
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toSet()).size());
        return summary;
    }

    public void closeMissingFromSnapshot(
            String sourceName,
            Set<String> currentUrls) {

        Set<String> normalizedUrls = currentUrls.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toCollection(HashSet::new));
        LocalDateTime now = LocalDateTime.now();
        for (Opportunity opportunity : repository.findBySourceNameIgnoreCase(sourceName)) {
            String applyUrl = opportunity.getApplyUrl();
            if (applyUrl != null && !normalizedUrls.contains(applyUrl.toLowerCase())
                    && "OPEN".equals(opportunity.getOpportunityStatus())) {
                opportunity.setOpportunityStatus("CLOSED");
                opportunity.setIsActive(false);
                opportunity.setLastCheckedAt(now);
                opportunity.setLastUpdatedAt(now);
                repository.save(opportunity);
            }
        }
    }

    public void expirePastDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        for (Opportunity opportunity : repository.findAll()) {
            if (opportunity.getDeadline() != null
                    && opportunity.getDeadline().isBefore(now)
                    && !"EXPIRED".equals(opportunity.getOpportunityStatus())) {
                opportunity.setOpportunityStatus("EXPIRED");
                opportunity.setIsActive(false);
                opportunity.setLastUpdatedAt(now);
                repository.save(opportunity);
            }
        }
    }

    private String verify(Opportunity opportunity) {
        String company = normalize(opportunity.getCompany());
        String officialDomain = verifiedCompanyDomains.get(company);
        if (officialDomain == null || opportunity.getTitle() == null
                || opportunity.getTitle().isBlank()
                || !"india".equals(normalize(opportunity.getCountry()))) {
            return "REVIEW";
        }
        String content = (String.valueOf(opportunity.getCompany()) + " "
                + String.valueOf(opportunity.getTitle()) + " "
                + String.valueOf(opportunity.getDescription())).toLowerCase();
        if (excludedKeywords.stream().anyMatch(content::contains)) {
            return "REJECTED";
        }
        return isOfficialHttpsUrl(opportunity.getApplyUrl(), officialDomain)
                && isOfficialHttpsUrl(opportunity.getSourceUrl(), officialDomain)
                && officialDomain.equals(normalizeDomain(opportunity.getCompanyDomain()))
                        ? "VERIFIED"
                        : "REVIEW";
    }

    private String resolveStatus(Opportunity opportunity, LocalDateTime now) {
        if (opportunity.getDeadline() != null
                && opportunity.getDeadline().isBefore(now)) {
            return "EXPIRED";
        }
        return Boolean.FALSE.equals(opportunity.getIsActive()) ? "CLOSED" : "OPEN";
    }

    private boolean copyChangedFields(Opportunity from, Opportunity to) {
        boolean changed = false;
        changed |= setIfChanged(from.getCompany(), to.getCompany(), to::setCompany);
        changed |= setIfChanged(from.getTitle(), to.getTitle(), to::setTitle);
        changed |= setIfChanged(from.getDescription(), to.getDescription(), to::setDescription);
        changed |= setIfChanged(from.getLocation(), to.getLocation(), to::setLocation);
        changed |= setIfChanged(from.getCountry(), to.getCountry(), to::setCountry);
        changed |= setIfChanged(from.getDepartment(), to.getDepartment(), to::setDepartment);
        changed |= setIfChanged(from.getWorkMode(), to.getWorkMode(), to::setWorkMode);
        changed |= setIfChanged(from.getJobType(), to.getJobType(), to::setJobType);
        changed |= setIfChanged(from.getType(), to.getType(), to::setType);
        changed |= setIfChanged(from.getSkills(), to.getSkills(), to::setSkills);
        changed |= setIfChanged(from.getGraduationYears(), to.getGraduationYears(), to::setGraduationYears);
        changed |= setIfChanged(from.getBranch(), to.getBranch(), to::setBranch);
        changed |= setIfChanged(from.getStipend(), to.getStipend(), to::setStipend);
        changed |= setIfChanged(from.getSalary(), to.getSalary(), to::setSalary);
        changed |= setIfChanged(from.getApplyUrl(), to.getApplyUrl(), to::setApplyUrl);
        changed |= setIfChanged(from.getSourceUrl(), to.getSourceUrl(), to::setSourceUrl);
        changed |= setIfChanged(from.getSourceName(), to.getSourceName(), to::setSourceName);
        changed |= setIfChanged(from.getSourceJobId(), to.getSourceJobId(), to::setSourceJobId);
        changed |= setIfChanged(from.getCanonicalApplyUrlHash(), to.getCanonicalApplyUrlHash(),
                to::setCanonicalApplyUrlHash);
        changed |= setIfChanged(from.getCompanyDomain(), to.getCompanyDomain(), to::setCompanyDomain);
        changed |= setIfChanged(from.getPostedDate(), to.getPostedDate(), to::setPostedDate);
        changed |= setIfChanged(from.getDeadline(), to.getDeadline(), to::setDeadline);
        changed |= setIfChanged(from.getClosedAt(), to.getClosedAt(), to::setClosedAt);
        return changed;
    }

    private boolean sameRole(Opportunity first, Opportunity second) {
        String firstUrl = normalize(first.getApplyUrl());
        String secondUrl = normalize(second.getApplyUrl());
        if (!firstUrl.isBlank() && !secondUrl.isBlank()) {
            return firstUrl.equals(secondUrl)
                    || Objects.equals(normalize(first.getLocation()), normalize(second.getLocation()));
        }
        return Objects.equals(normalize(first.getLocation()), normalize(second.getLocation()));
    }

    private boolean isToday(LocalDateTime dateTime, LocalDateTime now) {
        return dateTime != null && dateTime.toLocalDate().equals(now.toLocalDate());
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String normalizeDomain(String value) {
        return value == null ? ""
                : value.trim().toLowerCase()
                        .replaceFirst("^https?://", "").replaceFirst("^www\\.", "");
    }

    private boolean isOfficialHttpsUrl(String value, String officialDomain) {
        try {
            java.net.URI uri = java.net.URI.create(value);
            String host = normalizeDomain(uri.getHost());
            return "https".equalsIgnoreCase(uri.getScheme())
                    && (host.equals(officialDomain) || host.endsWith("." + officialDomain));
        } catch (Exception exception) {
            return false;
        }
    }

    private Map<String, String> parseCompanyDomains(String configuredDomains) {
        Map<String, String> domains = new HashMap<>();
        for (String item : configuredDomains.split(";")) {
            String[] parts = item.split("=", 2);
            if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
                domains.put(normalize(parts[0]), normalizeDomain(parts[1]));
            }
        }
        return domains;
    }

    private <T> boolean setIfChanged(T value, T current, java.util.function.Consumer<T> setter) {
        if (value == null || (value instanceof String text && text.isBlank())) {
            return false;
        }
        if (!Objects.equals(value, current)) {
            setter.accept(value);
            return true;
        }
        return false;
    }
}