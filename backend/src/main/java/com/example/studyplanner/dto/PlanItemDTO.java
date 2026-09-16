package com.example.studyplanner.dto;

import java.time.LocalDate;

public record PlanItemDTO(
        java.util.UUID taskId,
        String title,
        LocalDate deadline,
        int estimatedHours,
        int progressPercent,
        int weight
) {
}