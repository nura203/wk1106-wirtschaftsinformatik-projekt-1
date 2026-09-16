package com.example.studyplanner.service;

import com.example.studyplanner.entity.Reminder;
import com.example.studyplanner.entity.Task;
import com.example.studyplanner.enums.ReminderChannel;
import com.example.studyplanner.repository.ReminderRepository;
import com.example.studyplanner.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final TaskRepository taskRepository;

    public ReminderService(
            ReminderRepository reminderRepository,
            TaskRepository taskRepository
    ) {
        this.reminderRepository = reminderRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<Reminder> getRemindersForTask(
            UUID taskId,
            UUID userId
    ) {
        checkTaskOwnership(taskId, userId);

        return reminderRepository.findByTaskId(taskId);
    }

    @Transactional
    public Reminder createReminder(
            UUID taskId,
            UUID userId,
            int daysBefore,
            ReminderChannel channel
    ) {
        checkTaskOwnership(taskId, userId);

        if (daysBefore < 1) {
            throw new IllegalArgumentException(
                    "daysBefore muss mindestens 1 sein"
            );
        }

        if (channel == null) {
            throw new IllegalArgumentException(
                    "ReminderChannel darf nicht null sein"
            );
        }

        Reminder reminder = new Reminder();

        reminder.setTaskId(taskId);
        reminder.setDaysBefore(daysBefore);
        reminder.setChannel(channel);
        reminder.setActive(true);

        return reminderRepository.save(reminder);
    }

    @Transactional
    public Reminder updateReminder(
            UUID reminderId,
            UUID taskId,
            UUID userId,
            int daysBefore,
            ReminderChannel channel,
            boolean active
    ) {
        checkTaskOwnership(taskId, userId);

        Reminder reminder = getReminder(reminderId);

        if (!reminder.getTaskId().equals(taskId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Erinnerung gehört nicht zu dieser Aufgabe"
            );
        }

        if (daysBefore < 1) {
            throw new IllegalArgumentException(
                    "daysBefore muss mindestens 1 sein"
            );
        }

        if (channel == null) {
            throw new IllegalArgumentException(
                    "ReminderChannel darf nicht null sein"
            );
        }

        reminder.setDaysBefore(daysBefore);
        reminder.setChannel(channel);
        reminder.setActive(active);

        return reminderRepository.save(reminder);
    }

    @Transactional
    public void deleteReminder(
            UUID reminderId,
            UUID taskId,
            UUID userId
    ) {
        checkTaskOwnership(taskId, userId);

        Reminder reminder = getReminder(reminderId);

        if (!reminder.getTaskId().equals(taskId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Erinnerung gehört nicht zu dieser Aufgabe"
            );
        }

        reminderRepository.delete(reminder);
    }

    private Reminder getReminder(UUID reminderId) {
        return reminderRepository.findById(reminderId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Erinnerung nicht gefunden"
                        )
                );
    }

    private void checkTaskOwnership(
            UUID taskId,
            UUID userId
    ) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Aufgabe nicht gefunden"
                        )
                );

        if (!task.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Zugriff verweigert"
            );
        }
    }
}