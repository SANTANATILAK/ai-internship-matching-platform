package com.tilak.internship_platform.controller;

import com.tilak.internship_platform.dto.response.ApiResponse;
import com.tilak.internship_platform.dto.response.OpportunityMatchResponse;
import com.tilak.internship_platform.security.UserPrincipal;
import com.tilak.internship_platform.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<OpportunityMatchResponse>>> getTopMatches(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<OpportunityMatchResponse> matches = matchingService.getMatchesForUser(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(matches));
    }

    @GetMapping("/opportunity/{opportunityId}")
    public ResponseEntity<ApiResponse<OpportunityMatchResponse>> getMatchDetails(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long opportunityId) {
        OpportunityMatchResponse match = matchingService.getMatchForOpportunity(userPrincipal.getId(), opportunityId);
        return ResponseEntity.ok(ApiResponse.success(match));
    }
}
