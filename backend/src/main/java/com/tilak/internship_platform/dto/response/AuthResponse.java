package com.tilak.internship_platform.dto.response;

import com.tilak.internship_platform.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long userId;
    private String name;
    private String email;
    private Role role;
}
