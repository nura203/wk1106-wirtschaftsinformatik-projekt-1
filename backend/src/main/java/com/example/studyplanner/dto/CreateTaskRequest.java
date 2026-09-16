package com.example.studyplanner.dto;

import com.example.studyplanner.enums.TaskType;

public record CreateTaskRequest(
        String title,
        TaskType type,
        String deadline,
        String description,
        String subject,
        Integer estimatedHours,
        Integer weight
) {
}