package com.tilak.internship_platform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String phone;

    @NotBlank(message = "Branch is required")
    private String branch;

    @NotBlank(message = "College is required")
    private String college;

    @NotNull(message = "Graduation year is required")
    private Integer graduationYear;

    private String location;
    private String preferredLocation;
    private String preferredWorkType;
    private String rolePreference;
}
