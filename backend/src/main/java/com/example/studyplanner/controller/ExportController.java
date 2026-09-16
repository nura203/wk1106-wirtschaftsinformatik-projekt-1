package com.example.studyplanner.controller;

import com.example.studyplanner.entity.Task;
import com.example.studyplanner.service.CalendarExportService;
import com.example.studyplanner.service.TaskService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/export")
public class ExportController {

    private final TaskService taskService;
    private final CalendarExportService calendarExportService;

    public ExportController(
            TaskService taskService,
            CalendarExportService calendarExportService
    ) {
        this.taskService = taskService;
        this.calendarExportService = calendarExportService;
    }

    @GetMapping("/ical")
    public ResponseEntity<String> exportIcal(
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        List<Task> tasks = taskService.getTasksForUser(userId);

        String ical = calendarExportService.generateIcal(tasks);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"study-planer.ics\""
                )
                .contentType(
                        MediaType.parseMediaType("text/calendar")
                )
                .body(ical);
    }
}