package com.tilak.internship_platform.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.entity.OpportunitySourceSync;
import com.tilak.internship_platform.repository.JobApplicationRepository;
import com.tilak.internship_platform.repository.OpportunityRepository;
import com.tilak.internship_platform.repository.OpportunitySourceSyncRepository;
import com.tilak.internship_platform.repository.ResumeRepository;
import com.tilak.internship_platform.repository.UserRepository;
import com.tilak.internship_platform.service.OpportunityCollector;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:5174",
        "http://127.0.0.1:5175"
})
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final OpportunityRepository opportunityRepository;
    private final ResumeRepository resumeRepository;
    private final JobApplicationRepository applicationRepository;
    private final OpportunitySourceSyncRepository syncRepository;
    private final OpportunityCollector opportunityCollector;

    public AdminController(
            UserRepository userRepository,
            OpportunityRepository opportunityRepository,
            ResumeRepository resumeRepository,
            JobApplicationRepository applicationRepository,
            OpportunitySourceSyncRepository syncRepository,
            OpportunityCollector opportunityCollector) {
        this.userRepository = userRepository;
        this.opportunityRepository = opportunityRepository;
        this.resumeRepository = resumeRepository;
        this.applicationRepository = applicationRepository;
        this.syncRepository = syncRepository;
        this.opportunityCollector = opportunityCollector;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalOpportunities", opportunityRepository.count());
        stats.put("verifiedOpportunities", opportunityRepository.findAll().stream()
                .filter(o -> "VERIFIED".equalsIgnoreCase(o.getVerificationStatus()))
                .count());
        stats.put("openOpportunities", opportunityRepository.findAll().stream()
                .filter(o -> Boolean.TRUE.equals(o.getIsActive()))
                .count());
        stats.put("totalResumes", resumeRepository.count());
        stats.put("totalApplications", applicationRepository.count());
        return stats;
    }

    @PostMapping("/sync")
    public Map<String, Object> triggerSync() {
        try {
            opportunityCollector.collectOpportunities();
            return Map.of("success", true, "message", "Synchronization executed successfully.");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Sync error: " + e.getMessage());
        }
    }

    @GetMapping("/sync-logs")
    public List<OpportunitySourceSync> getSyncLogs() {
        return syncRepository.findAll();
    }

    @PutMapping("/opportunities/{id}/status")
    public Map<String, Object> updateOpportunityStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));
        if (body.containsKey("verificationStatus")) {
            opp.setVerificationStatus(body.get("verificationStatus"));
        }
        if (body.containsKey("opportunityStatus")) {
            opp.setOpportunityStatus(body.get("opportunityStatus"));
            opp.setIsActive("OPEN".equalsIgnoreCase(body.get("opportunityStatus")));
        }
        opportunityRepository.save(opp);
        return Map.of("success", true, "message", "Opportunity status updated");
    }

    @PostMapping({"/collector/run"})
    public Map<String, Object> runCollector() {
        return triggerSync();
    }

    @GetMapping({"/logs"})
    public List<OpportunitySourceSync> getLogs() {
        return getSyncLogs();
    }

    @GetMapping({"/opportunities"})
    public List<Opportunity> getAllOpportunities() {
        return opportunityRepository.findAll();
    }

    @GetMapping({"/users"})
    public List<com.tilak.internship_platform.entity.User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping({"/opportunities/{id}/verify"})
    public Map<String, Object> verifyOpportunity(@PathVariable Long id) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));
        opp.setVerificationStatus("VERIFIED");
        opp.setOpportunityStatus("OPEN");
        opp.setIsActive(true);
        opportunityRepository.save(opp);
        return Map.of("success", true, "message", "Opportunity verified");
    }

    @PostMapping({"/opportunities/{id}/reject"})
    public Map<String, Object> rejectOpportunity(@PathVariable Long id) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));
        opp.setVerificationStatus("REJECTED");
        opp.setOpportunityStatus("CLOSED");
        opp.setIsActive(false);
        opportunityRepository.save(opp);
        return Map.of("success", true, "message", "Opportunity rejected");
    }
}
