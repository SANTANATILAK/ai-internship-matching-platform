package com.tilak.internship_platform.dto.request;

import com.tilak.internship_platform.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private ApplicationStatus status;
    private String notes;
}
