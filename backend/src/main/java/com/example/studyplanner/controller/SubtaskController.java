package com.example.studyplanner.controller;

import com.example.studyplanner.entity.Subtask;
import com.example.studyplanner.service.SubtaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/subtasks")
public class SubtaskController {

    private final SubtaskService subtaskService;

    public SubtaskController(SubtaskService subtaskService) {
        this.subtaskService = subtaskService;
    }

    @GetMapping
    public ResponseEntity<List<Subtask>> getSubtasks(
            @PathVariable UUID taskId,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                subtaskService.getSubtasks(taskId, userId)
        );
    }

    @PostMapping
    public ResponseEntity<Subtask> createSubtask(
            @PathVariable UUID taskId,
            @RequestBody SubtaskRequest request,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        Subtask subtask = subtaskService.createSubtask(
                taskId,
                userId,
                request.title(),
                request.done(),
                request.sortOrder()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(subtask);
    }

    @PutMapping("/{subtaskId}")
    public ResponseEntity<Subtask> updateSubtask(
            @PathVariable UUID taskId,
            @PathVariable UUID subtaskId,
            @RequestBody SubtaskRequest request,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        Subtask subtask = subtaskService.updateSubtask(
                taskId,
                subtaskId,
                userId,
                request.title(),
                request.done(),
                request.sortOrder()
        );

        return ResponseEntity.ok(subtask);
    }

    @DeleteMapping("/{subtaskId}")
    public ResponseEntity<Void> deleteSubtask(
            @PathVariable UUID taskId,
            @PathVariable UUID subtaskId,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        subtaskService.deleteSubtask(
                taskId,
                subtaskId,
                userId
        );

        return ResponseEntity.noContent().build();
    }

    public record SubtaskRequest(
            String title,
            boolean done,
            int sortOrder
    ) {
    }
}