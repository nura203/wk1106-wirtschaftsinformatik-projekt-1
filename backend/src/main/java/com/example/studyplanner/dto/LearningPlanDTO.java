package com.example.studyplanner.dto;

import java.time.LocalDate;
import java.util.List;

public record LearningPlanDTO(
        LocalDate generatedAt,
        List<WeekEntryDTO> weekEntries
) {
}