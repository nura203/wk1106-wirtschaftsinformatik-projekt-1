package com.example.studyplanner.dto;

import java.time.LocalDate;
import java.util.List;

public record WeekEntryDTO(
        LocalDate date,
        List<PlanTaskDTO> tasks
) {
}