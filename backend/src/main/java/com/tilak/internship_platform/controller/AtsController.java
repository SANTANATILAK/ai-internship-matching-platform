package com.tilak.internship_platform.controller;

import com.tilak.internship_platform.dto.response.ApiResponse;
import com.tilak.internship_platform.dto.response.AtsScoreResponse;
import com.tilak.internship_platform.security.UserPrincipal;
import com.tilak.internship_platform.service.AtsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ats")
public class AtsController {

    private final AtsService atsService;

    public AtsController(AtsService atsService) {
        this.atsService = atsService;
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<AtsScoreResponse>> getLatestAtsScore(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        AtsScoreResponse response = atsService.getLatestAtsScore(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<AtsScoreResponse>>> getAtsHistory(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<AtsScoreResponse> list = atsService.getAtsHistory(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
