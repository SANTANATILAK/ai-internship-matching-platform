package com.tilak.internship_platform.service;

import com.tilak.internship_platform.dto.response.AdminStatsResponse;
import com.tilak.internship_platform.entity.*;
import com.tilak.internship_platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final OpportunityRepository opportunityRepository;
    private final ApplicationRepository applicationRepository;
    private final CollectorLogRepository collectorLogRepository;

    public AdminService(UserRepository userRepository,
                        CompanyRepository companyRepository,
                        OpportunityRepository opportunityRepository,
                        ApplicationRepository applicationRepository,
                        CollectorLogRepository collectorLogRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.opportunityRepository = opportunityRepository;
        this.applicationRepository = applicationRepository;
        this.collectorLogRepository = collectorLogRepository;
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.countByRole(Role.STUDENT);
        long verifiedCompanies = companyRepository.countByVerifiedTrue();
        long totalOpps = opportunityRepository.count();
        long activeOpps = opportunityRepository.countByStatus(OpportunityStatus.ACTIVE);
        long expiredOpps = opportunityRepository.countByStatus(OpportunityStatus.EXPIRED);
        long reviewOpps = opportunityRepository.countByVerificationStatus(VerificationStatus.REVIEW);
        long rejectedOpps = opportunityRepository.countByVerificationStatus(VerificationStatus.REJECTED);
        long totalApps = applicationRepository.count();

        List<CollectorLog> latestLogs = collectorLogRepository.findTop20ByOrderByStartedAtDesc();
        String lastRun = !latestLogs.isEmpty() && latestLogs.get(0).getCompletedAt() != null
                ? latestLogs.get(0).getCompletedAt().toString() : "Not executed yet";
        String status = !latestLogs.isEmpty() ? latestLogs.get(0).getStatus() : "IDLE";

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalStudents(totalStudents)
                .totalVerifiedCompanies(verifiedCompanies)
                .totalOpportunities(totalOpps)
                .activeOpportunities(activeOpps)
                .expiredOpportunities(expiredOpps)
                .pendingReviewOpportunities(reviewOpps)
                .rejectedOpportunities(rejectedOpps)
                .totalApplications(totalApps)
                .lastCollectorRun(lastRun)
                .collectorStatus(status)
                .build();
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CollectorLog> getCollectorLogs() {
        return collectorLogRepository.findTop20ByOrderByStartedAtDesc();
    }
}
