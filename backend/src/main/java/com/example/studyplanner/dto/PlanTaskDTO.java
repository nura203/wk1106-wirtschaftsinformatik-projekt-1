package com.example.studyplanner.dto;

import java.time.LocalDate;

public record PlanTaskDTO(
        String taskId,
        String title,
        int recommendedMinutes,
        String urgency
) {
}