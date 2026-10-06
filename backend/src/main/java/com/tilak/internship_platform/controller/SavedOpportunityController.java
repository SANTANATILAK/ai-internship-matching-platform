package com.tilak.internship_platform.controller;

import com.tilak.internship_platform.dto.response.ApiResponse;
import com.tilak.internship_platform.entity.SavedOpportunity;
import com.tilak.internship_platform.security.UserPrincipal;
import com.tilak.internship_platform.service.SavedOpportunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saved")
public class SavedOpportunityController {

    private final SavedOpportunityService savedOpportunityService;

    public SavedOpportunityController(SavedOpportunityService savedOpportunityService) {
        this.savedOpportunityService = savedOpportunityService;
    }

    @PostMapping("/{opportunityId}")
    public ResponseEntity<ApiResponse<SavedOpportunity>> saveOpportunity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long opportunityId) {
        SavedOpportunity saved = savedOpportunityService.save(userPrincipal.getId(), opportunityId);
        return ResponseEntity.ok(ApiResponse.success("Opportunity saved to bookmarks", saved));
    }

    @DeleteMapping("/{opportunityId}")
    public ResponseEntity<ApiResponse<Void>> removeSavedOpportunity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long opportunityId) {
        savedOpportunityService.remove(userPrincipal.getId(), opportunityId);
        return ResponseEntity.ok(ApiResponse.success("Opportunity removed from bookmarks", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SavedOpportunity>>> getUserSaved(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<SavedOpportunity> list = savedOpportunityService.getUserSaved(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/check/{opportunityId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkIfSaved(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long opportunityId) {
        boolean isSaved = savedOpportunityService.isSaved(userPrincipal.getId(), opportunityId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("saved", isSaved)));
    }
}
