package com.tilak.internship_platform.service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.tilak.internship_platform.entity.Opportunity;

@Component
public class OfficialCareerFeedSource implements OpportunitySource {

    private static final String SOURCE_NAME = "Configured Official Career Feeds";

    private final WebClient webClient;
    private final String configuredFeeds;
    private final Set<String> currentUrls = new HashSet<>();
    private boolean completeSnapshot;
    private String lastFetchFailureMessage;

    public OfficialCareerFeedSource(
            WebClient webClient,
            @Value("${opportunities.official-feeds:}") String configuredFeeds) {
        this.webClient = webClient;
        this.configuredFeeds = configuredFeeds;
    }

    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public boolean isConfigured() {
        return configuredFeeds != null && !configuredFeeds.isBlank();
    }

    @Override
    public String getLastFetchFailureMessage() {
        return lastFetchFailureMessage;
    }

    @Override
    public List<Opportunity> fetchOpportunities() {
        List<Opportunity> opportunities = new ArrayList<>();
        currentUrls.clear();
        completeSnapshot = false;
        lastFetchFailureMessage = null;
        String[] feeds = configuredFeeds == null ? new String[0] : configuredFeeds.split(";");
        boolean hasFeeds = false;
        boolean allFeedsFetched = true;

        for (String feed : feeds) {
            String[] fields = feed.split("\\|", 3);
            if (fields.length != 3 || fields[0].isBlank()
                    || fields[1].isBlank() || fields[2].isBlank()) {
                if (!feed.isBlank()) {
                    allFeedsFetched = false;
                }
                continue;
            }
            hasFeeds = true;
            String company = fields[0].trim();
            String domain = fields[1].trim().toLowerCase();
            try {
                URI feedUri = URI.create(fields[2].trim());
                if (!"https".equalsIgnoreCase(feedUri.getScheme())
                        || !isDomain(feedUri.getHost(), domain)) {
                    allFeedsFetched = false;
                    continue;
                }
                JsonNode root = webClient.get().uri(feedUri)
                        .retrieve().bodyToMono(JsonNode.class).block();
                if (root == null) {
                    allFeedsFetched = false;
                    continue;
                }
                List<JsonNode> jobs = findJobs(root);
                if (jobs == null) {
                    allFeedsFetched = false;
                    continue;
                }
                for (JsonNode job : jobs) {
                    Opportunity opportunity = toOpportunity(job, company, domain);
                    if (opportunity != null) {
                        opportunities.add(opportunity);
                        currentUrls.add(opportunity.getApplyUrl());
                    } else {
                        allFeedsFetched = false;
                    }
                }
            } catch (Exception exception) {
                allFeedsFetched = false;
                lastFetchFailureMessage = "Official feed failed for " + company + ": "
                        + exception.getClass().getSimpleName() + ": " + exception.getMessage();
            }
        }
        if (hasFeeds && !allFeedsFetched && lastFetchFailureMessage == null) {
            lastFetchFailureMessage = "One or more feed responses were incomplete or invalid";
        }
        completeSnapshot = hasFeeds && allFeedsFetched;
        return opportunities;
    }

    @Override
    public boolean isCompleteSnapshot() {
        return completeSnapshot;
    }

    @Override
    public Set<String> getCurrentOpportunityUrls() {
        return Set.copyOf(currentUrls);
    }

    private Opportunity toOpportunity(JsonNode job, String company, String domain) {
        String title = text(job, "title", "role", "name");
        String applyUrl = text(job, "applyUrl", "applicationUrl", "url");
        if (title == null || applyUrl == null) {
            return null;
        }
        Opportunity opportunity = new Opportunity();
        opportunity.setCompany(company);
        opportunity.setSourceJobId(text(job, "id", "jobId", "job_id", "requisitionId"));
        opportunity.setTitle(title);
        opportunity.setCompanyDomain(domain);
        opportunity.setDescription(text(job, "description", "summary"));
        opportunity.setLocation(text(job, "location", "locations"));
        opportunity.setCountry(text(job, "country"));
        String jobType = text(job, "jobType", "employmentType", "type");
        opportunity.setJobType(jobType);
        opportunity.setType(jobType);
        opportunity.setWorkMode(text(job, "workMode", "workplaceType", "remoteType"));
        opportunity.setDepartment(text(job, "department", "jobCategory", "team"));
        opportunity.setSkills(text(job, "skills", "requiredSkills"));
        opportunity.setGraduationYears(text(job, "graduationYears", "eligibleGraduationYears"));
        opportunity.setBranch(text(job, "branch", "eligibleBranches"));
        opportunity.setStipend(text(job, "stipend"));
        opportunity.setSalary(text(job, "salary", "compensation"));
        opportunity.setApplyUrl(applyUrl);
        opportunity.setSourceUrl(text(job, "sourceUrl") == null
                ? applyUrl
                : text(job, "sourceUrl"));
        opportunity.setSourceName(SOURCE_NAME);
        opportunity.setPostedDate(date(text(job, "postedDate", "datePosted")));
        opportunity.setDeadline(date(text(job, "deadline", "closingDate", "expiryDate")));
        opportunity.setClosedAt(date(text(job, "closedDate", "closedAt")));
        String status = text(job, "status", "jobStatus");
        opportunity.setIsActive(status == null || !(status.equalsIgnoreCase("CLOSED")
                || status.equalsIgnoreCase("EXPIRED") || status.equalsIgnoreCase("CANCELLED")));
        if (opportunity.getCountry() == null
                && opportunity.getLocation() != null
                && opportunity.getLocation().toLowerCase().matches(
                        ".*(india|bengaluru|bangalore|hyderabad|chennai|mumbai|pune|delhi|gurugram|gurgaon|noida|kolkata).*")) {
            opportunity.setCountry("India");
        }
        return opportunity;
    }

    private List<JsonNode> findJobs(JsonNode root) {
        if (root.isArray()) {
            List<JsonNode> jobs = new ArrayList<>();
            root.forEach(jobs::add);
            return jobs;
        }
        for (String key : List.of("jobs", "opportunities", "results", "data")) {
            JsonNode value = root.get(key);
            if (value != null && value.isArray()) {
                List<JsonNode> jobs = new ArrayList<>();
                value.forEach(jobs::add);
                return jobs;
            }
        }
        return null;
    }

    private String text(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node.get(name);
            if (value != null && !value.isNull()) {
                if (value.isArray()) {
                    List<String> values = new ArrayList<>();
                    Iterator<JsonNode> elements = value.elements();
                    elements.forEachRemaining(element -> values.add(element.asText()));
                    return String.join(", ", values);
                }
                return value.asText();
            }
        }
        return null;
    }

    private LocalDateTime date(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (Exception ignored) {
            try {
                return java.time.OffsetDateTime.parse(value).toLocalDateTime();
            } catch (Exception alsoIgnored) {
                try {
                    return java.time.LocalDate.parse(value).atStartOfDay();
                } catch (Exception dateIgnored) {
                    return null;
                }
            }
        }
    }

    private boolean isDomain(String host, String domain) {
        return host != null && (host.equalsIgnoreCase(domain)
                || host.toLowerCase().endsWith("." + domain));
    }
}