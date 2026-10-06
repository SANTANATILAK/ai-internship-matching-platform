package com.tilak.internship_platform.controller;

import com.tilak.internship_platform.dto.response.ApiResponse;
import com.tilak.internship_platform.dto.response.ResumeAnalysisResponse;
import com.tilak.internship_platform.entity.Resume;
import com.tilak.internship_platform.security.UserPrincipal;
import com.tilak.internship_platform.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponse>> uploadResume(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) {

        ResumeAnalysisResponse response = resumeService.uploadAndProcessResume(userPrincipal.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Resume parsed and analyzed successfully", response));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<Resume>> getLatestResume(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Resume resume = resumeService.getLatestResume(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(resume));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Resume>> getResumeById(@PathVariable Long id) {
        Resume resume = resumeService.getResumeById(id);
        return ResponseEntity.ok(ApiResponse.success(resume));
    }
}
