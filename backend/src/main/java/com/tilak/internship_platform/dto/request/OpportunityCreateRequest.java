package com.tilak.internship_platform.dto.request;

import com.tilak.internship_platform.entity.OpportunityType;
import com.tilak.internship_platform.entity.WorkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityCreateRequest {

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotBlank(message = "Title is required")
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

    @NotBlank(message = "Apply URL is required")
    private String applyUrl;

    private String sourceUrl;
    private LocalDateTime expiryDate;
}
