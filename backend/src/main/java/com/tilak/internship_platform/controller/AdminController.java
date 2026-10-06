package com.tilak.internship_platform.controller;

import com.tilak.internship_platform.collector.OpportunityCollectorService;
import com.tilak.internship_platform.dto.request.CompanyCreateRequest;
import com.tilak.internship_platform.dto.request.OpportunityCreateRequest;
import com.tilak.internship_platform.dto.response.AdminStatsResponse;
import com.tilak.internship_platform.dto.response.ApiResponse;
import com.tilak.internship_platform.dto.response.OpportunityDetailResponse;
import com.tilak.internship_platform.entity.CollectorLog;
import com.tilak.internship_platform.entity.Company;
import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.service.AdminService;
import com.tilak.internship_platform.service.CompanyService;
import com.tilak.internship_platform.service.OpportunityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final CompanyService companyService;
    private final OpportunityService opportunityService;
    private final OpportunityCollectorService collectorService;

    public AdminController(AdminService adminService,
                           CompanyService companyService,
                           OpportunityService opportunityService,
                           OpportunityCollectorService collectorService) {
        this.adminService = adminService;
        this.companyService = companyService;
        this.opportunityService = opportunityService;
        this.collectorService = collectorService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getStats()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllUsers()));
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<CollectorLog>>> getCollectorLogs() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getCollectorLogs()));
    }

    @PostMapping("/collector/run")
    public ResponseEntity<ApiResponse<Map<String, String>>> triggerCollector() {
        collectorService.runCollectionCycle();
        return ResponseEntity.ok(ApiResponse.success("Collector cycle successfully triggered and completed",
                Map.of("status", "SUCCESS")));
    }

    // Company management
    @PostMapping("/companies")
    public ResponseEntity<ApiResponse<Company>> createCompany(@Valid @RequestBody CompanyCreateRequest request) {
        Company comp = companyService.createCompany(request);
        return ResponseEntity.ok(ApiResponse.success("Company created", comp));
    }

    @PutMapping("/companies/{id}/verify")
    public ResponseEntity<ApiResponse<Company>> verifyCompany(@PathVariable Long id) {
        Company comp = companyService.verifyCompany(id);
        return ResponseEntity.ok(ApiResponse.success("Company verified successfully", comp));
    }

    @PutMapping("/companies/{id}/reject")
    public ResponseEntity<ApiResponse<Company>> rejectCompany(@PathVariable Long id) {
        Company comp = companyService.rejectCompany(id);
        return ResponseEntity.ok(ApiResponse.success("Company rejected", comp));
    }

    // Opportunity management
    @PostMapping("/opportunities")
    public ResponseEntity<ApiResponse<OpportunityDetailResponse>> createOpportunity(@Valid @RequestBody OpportunityCreateRequest request) {
        OpportunityDetailResponse opp = opportunityService.createOpportunity(request);
        return ResponseEntity.ok(ApiResponse.success("Opportunity posted successfully", opp));
    }

    @PutMapping("/opportunities/{id}")
    public ResponseEntity<ApiResponse<OpportunityDetailResponse>> updateOpportunity(
            @PathVariable Long id,
            @Valid @RequestBody OpportunityCreateRequest request) {
        OpportunityDetailResponse opp = opportunityService.updateOpportunity(id, request);
        return ResponseEntity.ok(ApiResponse.success("Opportunity updated", opp));
    }

    @DeleteMapping("/opportunities/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOpportunity(@PathVariable Long id) {
        opportunityService.deleteOpportunity(id);
        return ResponseEntity.ok(ApiResponse.success("Opportunity deleted", null));
    }

    @PutMapping("/opportunities/{id}/verify")
    public ResponseEntity<ApiResponse<OpportunityDetailResponse>> verifyOpportunity(@PathVariable Long id) {
        OpportunityDetailResponse opp = opportunityService.verifyOpportunity(id);
        return ResponseEntity.ok(ApiResponse.success("Opportunity verified", opp));
    }

    @PutMapping("/opportunities/{id}/reject")
    public ResponseEntity<ApiResponse<OpportunityDetailResponse>> rejectOpportunity(@PathVariable Long id) {
        OpportunityDetailResponse opp = opportunityService.rejectOpportunity(id);
        return ResponseEntity.ok(ApiResponse.success("Opportunity rejected / removed", opp));
    }
}
