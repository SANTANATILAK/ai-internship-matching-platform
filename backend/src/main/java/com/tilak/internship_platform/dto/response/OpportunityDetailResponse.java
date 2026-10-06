package com.tilak.internship_platform.dto.response;

import com.tilak.internship_platform.entity.Company;
import com.tilak.internship_platform.entity.Opportunity;
import com.tilak.internship_platform.entity.OpportunityStatus;
import com.tilak.internship_platform.entity.OpportunityType;
import com.tilak.internship_platform.entity.VerificationStatus;
import com.tilak.internship_platform.entity.WorkType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityDetailResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String companyDomain;
    private boolean companyVerified;
    private String title;
    private String description;
    private String location;
    private WorkType workType;
    private OpportunityType type;
    private String stipend;
    private String salary;
    private String requiredSkills;
    private String graduationYears;
    private String degreeRequirements;
    private String branchRequirements;
    private String experienceRequirement;
    private String applyUrl;
    private String sourceUrl;
    private LocalDateTime postedDate;
    private LocalDateTime expiryDate;
    private LocalDateTime lastChecked;
    private LocalDateTime lastUpdated;
    private OpportunityStatus status;
    private VerificationStatus verificationStatus;
    private String sourceType;

    public static OpportunityDetailResponse fromEntity(Opportunity opp) {
        Company comp = opp.getCompany();
        return OpportunityDetailResponse.builder()
                .id(opp.getId())
                .companyId(comp != null ? comp.getId() : null)
                .companyName(comp != null ? comp.getName() : "Unknown")
                .companyDomain(comp != null ? comp.getOfficialDomain() : "")
                .companyVerified(comp != null && comp.isVerified())
                .title(opp.getTitle())
                .description(opp.getDescription())
                .location(opp.getLocation())
                .workType(opp.getWorkType())
                .type(opp.getType())
                .stipend(opp.getStipend())
                .salary(opp.getSalary())
                .requiredSkills(opp.getRequiredSkills())
                .graduationYears(opp.getGraduationYears())
                .degreeRequirements(opp.getDegreeRequirements())
                .branchRequirements(opp.getBranchRequirements())
                .experienceRequirement(opp.getExperienceRequirement())
                .applyUrl(opp.getApplyUrl())
                .sourceUrl(opp.getSourceUrl())
                .postedDate(opp.getPostedDate())
                .expiryDate(opp.getExpiryDate())
                .lastChecked(opp.getLastChecked())
                .lastUpdated(opp.getLastUpdated())
                .status(opp.getStatus())
                .verificationStatus(opp.getVerificationStatus())
                .sourceType(opp.getSourceType())
                .build();
    }
}
