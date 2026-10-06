package com.tilak.internship_platform.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {
    private String name;
    private String phone;
    private String branch;
    private String college;
    private Integer graduationYear;
    private String location;
    private String preferredLocation;
    private String preferredWorkType;
    private String rolePreference;
}
