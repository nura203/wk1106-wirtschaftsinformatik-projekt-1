package com.example.studyplanner.dto;

import com.example.studyplanner.enums.TaskStatus;
import com.example.studyplanner.enums.TaskType;
import com.example.studyplanner.enums.UrgencyLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TaskDTO(
        UUID id,
        String title,
        String description,
        TaskType type,
        String subject,
        LocalDate deadline,
        int estimatedHours,
        int actualHours,
        int progressPercent,
        TaskStatus status,
        int weight,
        UrgencyLevel urgency,
        List<SubtaskDTO> subtasks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}