package com.tilak.internship_platform.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsResponse {
    private long totalUsers;
    private long totalStudents;
    private long totalVerifiedCompanies;
    private long totalOpportunities;
    private long activeOpportunities;
    private long expiredOpportunities;
    private long pendingReviewOpportunities;
    private long rejectedOpportunities;
    private long totalApplications;
    private String lastCollectorRun;
    private String collectorStatus;
}
