package com.tilak.internship_platform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tilak.internship_platform.dto.response.OpportunityMatchResponse;
import com.tilak.internship_platform.entity.*;
import com.tilak.internship_platform.exception.ResourceNotFoundException;
import com.tilak.internship_platform.matcher.MatchingEngine;
import com.tilak.internship_platform.repository.OpportunityRepository;
import com.tilak.internship_platform.repository.ResumeRepository;
import com.tilak.internship_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final OpportunityRepository opportunityRepository;
    private final MatchingEngine matchingEngine;
    private final ObjectMapper objectMapper;

    @Value("${app.matching.min-threshold:30}")
    private int minThreshold;

    public MatchingService(UserRepository userRepository,
                           ResumeRepository resumeRepository,
                           OpportunityRepository opportunityRepository,
                           MatchingEngine matchingEngine,
                           ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.opportunityRepository = opportunityRepository;
        this.matchingEngine = matchingEngine;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<OpportunityMatchResponse> getMatchesForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Get user skills from latest resume
        Set<String> userSkills = new LinkedHashSet<>();
        Optional<Resume> latestResume = resumeRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
        if (latestResume.isPresent() && latestResume.get().getParsedSkills() != null) {
            try {
                List<String> list = objectMapper.readValue(
                        latestResume.get().getParsedSkills(), new TypeReference<>() {});
                userSkills.addAll(list);
            } catch (Exception ignored) {}
        }

        // Get verified opportunities
        List<Opportunity> activeVerified = opportunityRepository
                .findByStatusAndVerificationStatus(OpportunityStatus.ACTIVE, VerificationStatus.VERIFIED);

        return activeVerified.stream()
                .map(opp -> matchingEngine.match(user, userSkills, opp))
                .filter(res -> res.getMatchPercentage() >= minThreshold)
                .sorted((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OpportunityMatchResponse getMatchForOpportunity(Long userId, Long opportunityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Opportunity opp = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + opportunityId));

        Set<String> userSkills = new LinkedHashSet<>();
        Optional<Resume> latestResume = resumeRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
        if (latestResume.isPresent() && latestResume.get().getParsedSkills() != null) {
            try {
                List<String> list = objectMapper.readValue(
                        latestResume.get().getParsedSkills(), new TypeReference<>() {});
                userSkills.addAll(list);
            } catch (Exception ignored) {}
        }

        return matchingEngine.match(user, userSkills, opp);
    }
}
