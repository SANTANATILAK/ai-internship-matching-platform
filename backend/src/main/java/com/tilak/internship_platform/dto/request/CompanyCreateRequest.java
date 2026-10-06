package com.tilak.internship_platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyCreateRequest {
    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "Official domain is required")
    private String officialDomain;

    @NotBlank(message = "Career page URL is required")
    private String careerUrl;

    private String description;
    private String industry;
    private String location;
    private boolean verified;
}
