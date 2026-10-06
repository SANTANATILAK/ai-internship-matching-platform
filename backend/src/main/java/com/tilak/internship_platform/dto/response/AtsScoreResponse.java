package com.tilak.internship_platform.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsScoreResponse {
    private Long id;
    private Long resumeId;
    private Integer atsScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> missingSections;
    private List<String> suggestions;
    private List<String> detectedKeywords;
    private LocalDateTime createdAt;
}
