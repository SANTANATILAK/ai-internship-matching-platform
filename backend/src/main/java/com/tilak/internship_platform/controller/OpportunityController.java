package com.tilak.internship_platform.controller;

import com.tilak.internship_platform.dto.response.ApiResponse;
import com.tilak.internship_platform.dto.response.OpportunityDetailResponse;
import com.tilak.internship_platform.entity.OpportunityType;
import com.tilak.internship_platform.entity.WorkType;
import com.tilak.internship_platform.service.OpportunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OpportunityDetailResponse>>> getAllOpportunities() {
        return ResponseEntity.ok(ApiResponse.success(opportunityService.getVerifiedOpportunities()));
    }

    @GetMapping("/verified")
    public ResponseEntity<ApiResponse<List<OpportunityDetailResponse>>> getVerifiedOpportunities() {
        return ResponseEntity.ok(ApiResponse.success(opportunityService.getVerifiedOpportunities()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OpportunityDetailResponse>> getOpportunityById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(opportunityService.getOpportunityById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<OpportunityDetailResponse>>> searchOpportunities(
            @RequestParam(name = "query", required = false) String query) {
        return ResponseEntity.ok(ApiResponse.success(opportunityService.searchOpportunities(query)));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<OpportunityDetailResponse>>> filterOpportunities(
            @RequestParam(name = "type", required = false) OpportunityType type,
            @RequestParam(name = "workType", required = false) WorkType workType,
            @RequestParam(name = "location", required = false) String location,
            @RequestParam(name = "skill", required = false) String skill) {
        return ResponseEntity.ok(ApiResponse.success(
                opportunityService.filterOpportunities(type, workType, location, skill)));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<OpportunityDetailResponse>>> getRecentOpportunities() {
        return ResponseEntity.ok(ApiResponse.success(opportunityService.getRecentOpportunities()));
    }

    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<List<OpportunityDetailResponse>>> getExpiringOpportunities() {
        return ResponseEntity.ok(ApiResponse.success(opportunityService.getExpiringSoonOpportunities()));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<List<OpportunityDetailResponse>>> getOpportunitiesByCompany(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.success(opportunityService.getOpportunitiesByCompany(companyId)));
    }
}
