package com.example.studyplanner.dto;

public record UpdateProgressRequest(
        int progressPercent,
        Integer sessionDurationMinutes,
        String notes
) {
}