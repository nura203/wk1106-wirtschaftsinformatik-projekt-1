package com.example.studyplanner.dto;

import java.util.UUID;

public record SubtaskDTO(
        UUID id,
        String title,
        boolean done,
        int sortOrder
) {
}