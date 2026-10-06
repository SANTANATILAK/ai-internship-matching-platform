package com.tilak.internship_platform.service;

import com.tilak.internship_platform.dto.request.ApplicationStatusUpdateRequest;
import com.tilak.internship_platform.entity.*;
import com.tilak.internship_platform.exception.BadRequestException;
import com.tilak.internship_platform.exception.ResourceNotFoundException;
import com.tilak.internship_platform.repository.ApplicationRepository;
import com.tilak.internship_platform.repository.OpportunityRepository;
import com.tilak.internship_platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              OpportunityRepository opportunityRepository,
                              UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Application apply(Long userId, Long opportunityId, String notes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Opportunity opp = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + opportunityId));

        if (applicationRepository.existsByUserIdAndOpportunityId(userId, opportunityId)) {
            throw new BadRequestException("You have already recorded an application for this opportunity");
        }

        Application app = Application.builder()
                .user(user)
                .opportunity(opp)
                .status(ApplicationStatus.APPLIED)
                .notes(notes)
                .build();

        return applicationRepository.save(app);
    }

    @Transactional(readOnly = true)
    public List<Application> getUserApplications(Long userId) {
        return applicationRepository.findByUserIdOrderByAppliedAtDesc(userId);
    }

    @Transactional
    public Application updateStatus(Long applicationId, Long userId, ApplicationStatusUpdateRequest req) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (!app.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only modify your own applications");
        }

        app.setStatus(req.getStatus());
        if (req.getNotes() != null) {
            app.setNotes(req.getNotes());
        }

        return applicationRepository.save(app);
    }
}
