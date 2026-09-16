package com.example.studyplanner.controller;

import com.example.studyplanner.entity.Reminder;
import com.example.studyplanner.enums.ReminderChannel;
import com.example.studyplanner.service.ReminderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks/{taskId}/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping
    public ResponseEntity<List<Reminder>> getReminders(
            @PathVariable UUID taskId,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                reminderService.getRemindersForTask(taskId, userId)
        );
    }

    @PostMapping
    public ResponseEntity<Reminder> createReminder(
            @PathVariable UUID taskId,
            @RequestParam int daysBefore,
            @RequestParam ReminderChannel channel,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        Reminder reminder = reminderService.createReminder(
                taskId,
                userId,
                daysBefore,
                channel
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reminder);
    }

    @PutMapping("/{reminderId}")
    public ResponseEntity<Reminder> updateReminder(
            @PathVariable UUID taskId,
            @PathVariable UUID reminderId,
            @RequestParam int daysBefore,
            @RequestParam ReminderChannel channel,
            @RequestParam boolean active,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        Reminder reminder = reminderService.updateReminder(
                reminderId,
                taskId,
                userId,
                daysBefore,
                channel,
                active
        );

        return ResponseEntity.ok(reminder);
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(
            @PathVariable UUID taskId,
            @PathVariable UUID reminderId,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        reminderService.deleteReminder(
                reminderId,
                taskId,
                userId
        );

        return ResponseEntity.noContent().build();
    }
}