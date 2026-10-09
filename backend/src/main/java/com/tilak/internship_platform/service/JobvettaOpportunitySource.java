package com.tilak.internship_platform.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.tilak.internship_platform.entity.Opportunity;

@Component
public class JobvettaOpportunitySource implements OpportunitySource {

    private final WebClient webClient;

    @Value("${jobvetta.api.url:https://api.jobvetta.com/v1/jobs}")
    private String apiUrl;

    @Value("${JOBVETTA_API_KEY:}")
    private String apiKey;
    private String lastFetchFailureMessage;

    public JobvettaOpportunitySource(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String getSourceName() {
        return "Jobvetta";
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public String getLastFetchFailureMessage() {
        return lastFetchFailureMessage;
    }

    @Override
    public List<Opportunity> fetchOpportunities() {

        List<Opportunity> opportunities = new ArrayList<>();
        lastFetchFailureMessage = null;

        if (!isConfigured()) {
            return opportunities;
        }

        try {

            String requestUrl = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("q", "internship OR PPO OR full-time graduate")
                    .queryParam("location", "India")
                    .queryParam("days", "7")
                    .queryParam("limit", "100")
                    .build().toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(requestUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                System.out.println("Jobvetta returned no response.");
                return opportunities;
            }

            Object jobsObject = response.get("jobs");

            if (!(jobsObject instanceof List<?> jobs)) {
                System.out.println("Jobvetta returned no jobs.");
                return opportunities;
            }

            for (Object jobObject : jobs) {

                if (!(jobObject instanceof Map<?, ?> job)) {
                    continue;
                }

                Opportunity opportunity = new Opportunity();

                opportunity.setCompany(getString(job, "company"));
                opportunity.setTitle(getString(job, "title"));
                opportunity.setLocation(getString(job, "location"));
                opportunity.setSourceJobId(firstNonBlank(
                        getString(job, "id"), getString(job, "job_id")));
                opportunity.setJobType(getString(job, "employment_type"));
                opportunity.setType(getString(job, "employment_type"));
                opportunity.setDepartment(firstNonBlank(
                        getString(job, "department"), getString(job, "category")));
                opportunity.setWorkMode(firstNonBlank(
                        getString(job, "work_mode"), getString(job, "workplace_type")));
                opportunity.setGraduationYears(firstNonBlank(
                        getString(job, "graduation_years"), getString(job, "eligible_graduation_years")));
                opportunity.setBranch(firstNonBlank(
                        getString(job, "eligible_branches"), getString(job, "branches")));
                opportunity.setApplyUrl(getString(job, "url"));
                opportunity.setSourceUrl(getString(job, "url"));
                opportunity.setSourceName("Jobvetta");
                opportunity.setCountry("India");
                opportunity.setIsActive(true);

                opportunity.setDescription(
                        getString(job, "description"));

                Object skillsObject = job.get("skills_required");

                if (skillsObject instanceof List<?> skillsList) {
                    opportunity.setSkills(
                            String.join(", ",
                                    skillsList.stream()
                                            .map(String::valueOf)
                                            .toList()));
                }

                opportunity.setSalary(getString(job, "salary"));
                opportunity.setStipend(getString(job, "stipend"));

                if (opportunity.getCompany() != null
                        && !opportunity.getCompany().isBlank()
                        && opportunity.getTitle() != null
                        && !opportunity.getTitle().isBlank()) {

                    opportunities.add(opportunity);
                }
            }

            System.out.println(
                    "Jobvetta jobs received: " + opportunities.size());

        } catch (Exception e) {

            lastFetchFailureMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            System.out.println("Jobvetta collection failed.");
            System.out.println(e.getMessage());
        }

        return opportunities;
    }

    private String getString(Map<?, ?> map, String key) {

        Object value = map.get(key);

        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank())
                return value;
        }
        return null;
    }
}