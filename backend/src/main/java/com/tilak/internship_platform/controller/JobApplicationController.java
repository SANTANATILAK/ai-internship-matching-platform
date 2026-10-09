package com.tilak.internship_platform.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tilak.internship_platform.entity.JobApplication;
import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.repository.JobApplicationRepository;
import com.tilak.internship_platform.repository.OpportunityRepository;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:5174",
        "http://127.0.0.1:5175"
})
@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;

    public JobApplicationController(
            JobApplicationRepository applicationRepository,
            OpportunityRepository opportunityRepository) {
        this.applicationRepository = applicationRepository;
        this.opportunityRepository = opportunityRepository;
    }

    @GetMapping({"", "/user"})
    public List<Map<String, Object>> getApplicationsByPrincipal(java.security.Principal principal) {
        if (principal == null) return List.of();
        Long userId = Long.valueOf(principal.getName());
        return getApplicationsByUser(userId);
    }

    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getApplicationsByUser(@PathVariable Long userId) {
        List<JobApplication> apps = applicationRepository.findByUserIdOrderByAppliedAtDesc(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (JobApplication a : apps) {
            Optional<Opportunity> oppOpt = opportunityRepository.findById(a.getOpportunityId());
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("applicationId", a.getId());
            map.put("status", a.getStatus());
            map.put("appliedAt", a.getAppliedAt());
            map.put("notes", a.getNotes());
            map.put("opportunityId", a.getOpportunityId());

            if (oppOpt.isPresent()) {
                Opportunity opp = oppOpt.get();
                map.put("opportunity", opp);
                map.put("company", opp.getCompany());
                map.put("title", opp.getTitle());
                map.put("location", opp.getLocation());
                map.put("workMode", opp.getWorkMode());
                map.put("stipend", opp.getStipend() != null ? opp.getStipend() : opp.getSalary());
                map.put("applyUrl", opp.getApplyUrl());
            } else {
                map.put("company", "Company");
                map.put("title", "Opportunity #" + a.getOpportunityId());
            }
            result.add(map);
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> trackApplication(
            @RequestBody Map<String, Object> body,
            java.security.Principal principal) {
        Long userId = body.get("userId") != null 
                ? Long.valueOf(body.get("userId").toString())
                : (principal != null ? Long.valueOf(principal.getName()) : null);
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        Long opportunityId = Long.valueOf(body.get("opportunityId").toString());
        String status = body.get("status") != null ? body.get("status").toString() : "APPLIED";
        String notes = body.get("notes") != null ? body.get("notes").toString() : "";

        Optional<JobApplication> existing = applicationRepository.findByUserIdAndOpportunityId(userId, opportunityId);
        if (existing.isPresent()) {
            JobApplication a = existing.get();
            a.setStatus(status);
            if (!notes.isBlank()) a.setNotes(notes);
            applicationRepository.save(a);
            return Map.of("message", "Application updated", "applicationId", a.getId(), "status", a.getStatus());
        }

        JobApplication app = new JobApplication(userId, opportunityId, status);
        app.setNotes(notes);
        JobApplication saved = applicationRepository.save(app);
        return Map.of("message", "Application recorded", "applicationId", saved.getId(), "status", saved.getStatus());
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        JobApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (body.get("status") != null) {
            app.setStatus(body.get("status").toString());
        }
        if (body.get("notes") != null) {
            app.setNotes(body.get("notes").toString());
        }
        applicationRepository.save(app);
        return Map.of("message", "Status updated successfully", "status", app.getStatus());
    }
}
