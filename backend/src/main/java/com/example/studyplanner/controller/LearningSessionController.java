package com.example.studyplanner.controller;

import com.example.studyplanner.dto.LearningSessionDTO;
import com.example.studyplanner.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/learning-sessions")
public class LearningSessionController {

    private final ProgressService progressService;

    public LearningSessionController(
            ProgressService progressService
    ) {
        this.progressService = progressService;
    }

    @GetMapping
    public ResponseEntity<List<LearningSessionDTO>> getLearningSessions(
            @PathVariable UUID taskId,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                progressService.getLearningSessions(
                        taskId,
                        userId
                )
        );
    }
}
