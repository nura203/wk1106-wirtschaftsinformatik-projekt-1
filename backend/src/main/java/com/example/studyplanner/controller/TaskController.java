package com.example.studyplanner.controller;

import com.example.studyplanner.dto.CreateTaskRequest;
import com.example.studyplanner.dto.TaskDTO;
import com.example.studyplanner.dto.UpdateProgressRequest;
import com.example.studyplanner.entity.Task;
import com.example.studyplanner.service.ProgressService;
import com.example.studyplanner.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProgressService progressService;

    public TaskController(
            TaskService taskService,
            ProgressService progressService
    ) {
        this.taskService = taskService;
        this.progressService = progressService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getTasks(
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                taskService.getTaskDTOsForUser(userId)
        );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTask(
            @PathVariable UUID taskId,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                taskService.getTaskDTOForUser(taskId, userId)
        );
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @RequestBody CreateTaskRequest request,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        Task createdTask = taskService.createTask(
                request,
                userId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.toTaskDTO(createdTask));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable UUID taskId,
            @RequestBody CreateTaskRequest request,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        Task updatedTask = taskService.updateTask(
                taskId,
                userId,
                request
        );

        return ResponseEntity.ok(
                taskService.toTaskDTO(updatedTask)
        );
    }

    @PatchMapping("/{taskId}/progress")
    public ResponseEntity<TaskDTO> updateProgress(
            @PathVariable UUID taskId,
            @RequestBody UpdateProgressRequest request,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        Task updatedTask = progressService.updateProgress(
                taskId,
                userId,
                request
        );

        return ResponseEntity.ok(
                taskService.toTaskDTO(updatedTask)
        );
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID taskId,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        taskService.deleteTask(
                taskId,
                userId
        );

        return ResponseEntity.noContent().build();
    }
}