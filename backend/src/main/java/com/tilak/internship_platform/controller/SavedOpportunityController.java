package com.tilak.internship_platform.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.entity.SavedOpportunity;
import com.tilak.internship_platform.repository.OpportunityRepository;
import com.tilak.internship_platform.repository.SavedOpportunityRepository;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:5174",
        "http://127.0.0.1:5175"
})
@RestController
@RequestMapping({"/api/saved-opportunities", "/api/saved"})
public class SavedOpportunityController {

    private final SavedOpportunityRepository savedRepository;
    private final OpportunityRepository opportunityRepository;

    public SavedOpportunityController(
            SavedOpportunityRepository savedRepository,
            OpportunityRepository opportunityRepository) {
        this.savedRepository = savedRepository;
        this.opportunityRepository = opportunityRepository;
    }

    @GetMapping({"", "/"})
    public List<Map<String, Object>> getSavedByPrincipal(java.security.Principal principal) {
        if (principal == null) return List.of();
        Long userId = Long.valueOf(principal.getName());
        return getSavedByUser(userId);
    }

    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getSavedByUser(@PathVariable Long userId) {
        List<SavedOpportunity> savedList = savedRepository.findByUserIdOrderBySavedAtDesc(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (SavedOpportunity s : savedList) {
            Optional<Opportunity> oppOpt = opportunityRepository.findById(s.getOpportunityId());
            if (oppOpt.isPresent()) {
                Opportunity opp = oppOpt.get();
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("savedId", s.getId());
                map.put("savedAt", s.getSavedAt());
                map.put("opportunityId", opp.getId());
                map.put("opportunity", opp);
                map.put("company", opp.getCompany());
                map.put("title", opp.getTitle());
                map.put("location", opp.getLocation());
                map.put("workMode", opp.getWorkMode());
                map.put("type", opp.getType() != null ? opp.getType() : opp.getJobType());
                map.put("stipend", opp.getStipend() != null ? opp.getStipend() : opp.getSalary());
                map.put("skills", opp.getSkills());
                map.put("applyUrl", opp.getApplyUrl());
                map.put("deadline", opp.getDeadline());
                map.put("verificationStatus", opp.getVerificationStatus());
                map.put("isActive", opp.getIsActive());
                result.add(map);
            }
        }
        return result;
    }

    @PostMapping({"", "/"})
    public Map<String, Object> saveOpportunity(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Long opportunityId = Long.valueOf(body.get("opportunityId").toString());

        Optional<SavedOpportunity> existing = savedRepository.findByUserIdAndOpportunityId(userId, opportunityId);
        if (existing.isPresent()) {
            return Map.of("message", "Already saved", "savedId", existing.get().getId());
        }

        SavedOpportunity saved = new SavedOpportunity(userId, opportunityId);
        SavedOpportunity res = savedRepository.save(saved);
        return Map.of("message", "Saved successfully", "savedId", res.getId());
    }

    @PostMapping({"/{opportunityId}", "/opportunity/{opportunityId}"})
    public Map<String, Object> saveByPrincipal(@PathVariable Long opportunityId, java.security.Principal principal) {
        if (principal == null) return Map.of("error", "Unauthorized");
        Long userId = Long.valueOf(principal.getName());
        return saveOpportunity(Map.of("userId", userId, "opportunityId", opportunityId));
    }

    @Transactional
    @DeleteMapping({"/{opportunityId}", "/opportunity/{opportunityId}"})
    public Map<String, Object> deleteByPrincipal(@PathVariable Long opportunityId, java.security.Principal principal) {
        if (principal == null) return Map.of("error", "Unauthorized");
        Long userId = Long.valueOf(principal.getName());
        return removeSaved(userId, opportunityId);
    }

    @GetMapping("/check/{opportunityId}")
    public Map<String, Object> checkSaved(@PathVariable Long opportunityId, java.security.Principal principal) {
        if (principal == null) return Map.of("saved", false);
        Long userId = Long.valueOf(principal.getName());
        boolean saved = savedRepository.existsByUserIdAndOpportunityId(userId, opportunityId);
        return Map.of("saved", saved, "data", Map.of("saved", saved));
    }

    @Transactional
    @DeleteMapping("/user/{userId}/opportunity/{opportunityId}")
    public Map<String, Object> removeSaved(
            @PathVariable Long userId,
            @PathVariable Long opportunityId) {
        savedRepository.deleteByUserIdAndOpportunityId(userId, opportunityId);
        return Map.of("message", "Removed from saved opportunities");
    }
}
