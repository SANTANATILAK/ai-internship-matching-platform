package com.tilak.internship_platform.controller;

import com.tilak.internship_platform.dto.request.ApplicationStatusUpdateRequest;
import com.tilak.internship_platform.dto.response.ApiResponse;
import com.tilak.internship_platform.entity.Application;
import com.tilak.internship_platform.security.UserPrincipal;
import com.tilak.internship_platform.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Application>> recordApplication(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Map<String, Object> body) {

        Long opportunityId = Long.valueOf(body.get("opportunityId").toString());
        String notes = body.get("notes") != null ? body.get("notes").toString() : "";

        Application app = applicationService.apply(userPrincipal.getId(), opportunityId, notes);
        return ResponseEntity.ok(ApiResponse.success("Application recorded successfully", app));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<Application>>> getUserApplications(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Application> apps = applicationService.getUserApplications(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(apps));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Application>> updateApplicationStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateRequest req) {

        Application updated = applicationService.updateStatus(id, userPrincipal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Application status updated", updated));
    }
}
