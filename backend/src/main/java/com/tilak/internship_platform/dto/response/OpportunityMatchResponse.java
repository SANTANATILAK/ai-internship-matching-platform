package com.tilak.internship_platform.dto.response;

import com.tilak.internship_platform.entity.OpportunityType;
import com.tilak.internship_platform.entity.VerificationStatus;
import com.tilak.internship_platform.entity.WorkType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityMatchResponse {
    private Long opportunityId;
    private String companyName;
    private String companyDomain;
    private boolean companyVerified;
    private String title;
    private String location;
    private WorkType workType;
    private OpportunityType type;
    private String stipend;
    private String salary;
    private String applyUrl;
    private LocalDateTime postedDate;
    private LocalDateTime lastUpdated;
    private VerificationStatus verificationStatus;

    // AI Matching metrics
    private int matchPercentage;
    private String matchLevel;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private int totalRequiredSkills;
    private boolean eligible;
    private List<String> matchingReasons;
}
