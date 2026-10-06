package com.tilak.internship_platform.service;

import com.tilak.internship_platform.entity.*;
import com.tilak.internship_platform.exception.BadRequestException;
import com.tilak.internship_platform.exception.ResourceNotFoundException;
import com.tilak.internship_platform.repository.OpportunityRepository;
import com.tilak.internship_platform.repository.SavedOpportunityRepository;
import com.tilak.internship_platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SavedOpportunityService {

    private final SavedOpportunityRepository savedOpportunityRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    public SavedOpportunityService(SavedOpportunityRepository savedOpportunityRepository,
                                   OpportunityRepository opportunityRepository,
                                   UserRepository userRepository) {
        this.savedOpportunityRepository = savedOpportunityRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SavedOpportunity save(Long userId, Long opportunityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Opportunity opp = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + opportunityId));

        if (savedOpportunityRepository.existsByUserIdAndOpportunityId(userId, opportunityId)) {
            throw new BadRequestException("Opportunity is already saved");
        }

        SavedOpportunity saved = SavedOpportunity.builder()
                .user(user)
                .opportunity(opp)
                .build();

        return savedOpportunityRepository.save(saved);
    }

    @Transactional
    public void remove(Long userId, Long opportunityId) {
        if (!savedOpportunityRepository.existsByUserIdAndOpportunityId(userId, opportunityId)) {
            throw new ResourceNotFoundException("Opportunity was not saved");
        }
        savedOpportunityRepository.deleteByUserIdAndOpportunityId(userId, opportunityId);
    }

    @Transactional(readOnly = true)
    public List<SavedOpportunity> getUserSaved(Long userId) {
        return savedOpportunityRepository.findByUserIdOrderBySavedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public boolean isSaved(Long userId, Long opportunityId) {
        return savedOpportunityRepository.existsByUserIdAndOpportunityId(userId, opportunityId);
    }
}
