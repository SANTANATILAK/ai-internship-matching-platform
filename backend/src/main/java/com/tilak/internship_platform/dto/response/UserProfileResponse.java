package com.tilak.internship_platform.dto.response;

import com.tilak.internship_platform.entity.Role;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String branch;
    private String college;
    private Integer graduationYear;
    private String location;
    private String preferredLocation;
    private String preferredWorkType;
    private String rolePreference;
    private Role role;
    private boolean hasResume;
    private Integer latestAtsScore;
    private LocalDateTime createdAt;
}
