package com.tilak.internship_platform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.repository.OpportunityRepository;
import com.tilak.internship_platform.service.OpportunitySearchService;
import com.tilak.internship_platform.service.OpportunityService;

@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174",
    "http://localhost:5175",
    "http://127.0.0.1:5173",
    "http://127.0.0.1:5174",
    "http://127.0.0.1:5175"
})
@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final OpportunityRepository opportunityRepository;
    private final OpportunitySearchService searchService;

    public OpportunityController(
            OpportunityService opportunityService,
            OpportunityRepository opportunityRepository,
            OpportunitySearchService searchService) {
        this.opportunityService = opportunityService;
        this.opportunityRepository = opportunityRepository;
        this.searchService = searchService;
    }

    @GetMapping
    public List<Opportunity> getOpportunities() {
        return opportunityService.getActiveOpportunities();
    }

    @GetMapping("/recent")
    public List<Opportunity> getRecentOpportunities() {
        return opportunityService.getActiveOpportunities().stream()
                .limit(10)
                .toList();
    }

    @GetMapping("/expiring")
    public List<Opportunity> getExpiringOpportunities() {
        return opportunityService.getActiveOpportunities().stream()
                .filter(o -> o.getDeadline() != null)
                .limit(10)
                .toList();
    }

    @GetMapping("/filter")
    public List<Opportunity> filterOpportunities(
            @RequestParam(required = false) String workType,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String branch) {
        return opportunityService.getActiveOpportunities().stream()
                .filter(o -> workType == null || workType.isBlank() || (o.getWorkMode() != null && o.getWorkMode().equalsIgnoreCase(workType)))
                .filter(o -> type == null || type.isBlank() || ((o.getType() != null && o.getType().equalsIgnoreCase(type)) || (o.getJobType() != null && o.getJobType().equalsIgnoreCase(type))))
                .filter(o -> location == null || location.isBlank() || (o.getLocation() != null && o.getLocation().toLowerCase().contains(location.toLowerCase())))
                .filter(o -> branch == null || branch.isBlank() || (o.getBranch() != null && o.getBranch().toLowerCase().contains(branch.toLowerCase())))
                .toList();
    }

    @GetMapping("/{id:[0-9]+}")
    public Opportunity getOpportunityById(@PathVariable Long id) {
        return opportunityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));
    }

    @GetMapping("/search")
    public Page<Opportunity> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer graduationYear,
            @RequestParam(required = false) String workMode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastUpdatedAt"));
        return searchService.search(query, category, branch, graduationYear, workMode, pageable);
    }

    @GetMapping("/summary")
    public Map<String, Object> getOpportunitySummary() {
        return opportunityService.getOpportunitySummary();
    }

    @PostMapping
    public Opportunity createOpportunity(
            @RequestBody Opportunity opportunity) {
        return opportunityService.saveOpportunity(opportunity);
    }
}