package com.example.studyplanner.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LearningSessionDTO(
        UUID id,
        int durationMinutes,
        String notes,
        LocalDateTime recordedAt
) {
}