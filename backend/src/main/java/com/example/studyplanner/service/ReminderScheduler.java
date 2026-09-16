package com.example.studyplanner.service;

import com.example.studyplanner.entity.Reminder;
import com.example.studyplanner.entity.Task;
import com.example.studyplanner.entity.User;
import com.example.studyplanner.enums.ReminderChannel;
import com.example.studyplanner.repository.ReminderRepository;
import com.example.studyplanner.repository.TaskRepository;
import com.example.studyplanner.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(ReminderScheduler.class);

    private final ReminderRepository reminderRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ReminderScheduler(
            ReminderRepository reminderRepository,
            TaskRepository taskRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.reminderRepository = reminderRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void processReminders() {

        try {
            LocalDate today = LocalDate.now();

            List<Reminder> reminders =
                    reminderRepository.findAll();

            logger.info(
                    "Reminder-Prüfung gestartet: {} Reminder",
                    reminders.size()
            );

            for (Reminder reminder : reminders) {

                try {
                    processReminder(reminder, today);
                } catch (Exception exception) {
                    logger.error(
                            "Fehler bei der Verarbeitung von Reminder {}",
                            reminder.getId(),
                            exception
                    );
                }
            }

            logger.info("Reminder-Prüfung beendet");

        } catch (Exception exception) {
            logger.error(
                    "Fehler bei der Reminder-Prüfung",
                    exception
            );
        }
    }

    private void processReminder(
            Reminder reminder,
            LocalDate today
    ) {
        if (!reminder.isActive()) {
            return;
        }

        Task task = taskRepository
                .findById(reminder.getTaskId())
                .orElse(null);

        if (task == null) {
            logger.warn(
                    "Reminder {} übersprungen: Task {} nicht gefunden",
                    reminder.getId(),
                    reminder.getTaskId()
            );
            return;
        }

        LocalDate reminderDate =
                task.getDeadline()
                        .minusDays(reminder.getDaysBefore());

        if (!today.equals(reminderDate)) {
            return;
        }

        if (reminder.getLastSentAt() != null) {
            return;
        }

        ReminderChannel channel = reminder.getChannel();

        boolean processed = false;

        if (channel == ReminderChannel.IN_APP
                || channel == ReminderChannel.BOTH) {

            logger.info(
                    "In-App Reminder fällig für Aufgabe '{}'",
                    task.getTitle()
            );

            processed = true;
        }

        if (channel == ReminderChannel.EMAIL
                || channel == ReminderChannel.BOTH) {

            User user = userRepository
                    .findById(task.getUserId())
                    .orElse(null);

            if (user == null) {
                logger.warn(
                        "E-Mail Reminder übersprungen: Benutzer {} nicht gefunden",
                        task.getUserId()
                );
            } else if (!emailService.isConfigured()) {
                logger.info(
                        "E-Mail Reminder für Aufgabe '{}' nicht gesendet: "
                                + "SMTP ist nicht konfiguriert",
                        task.getTitle()
                );
            } else {
                boolean sent = emailService.sendReminder(
                        user.getEmail(),
                        task.getTitle(),
                        task.getDeadline().toString()
                );

                if (sent) {
                    logger.info(
                            "E-Mail Reminder für Aufgabe '{}' erfolgreich gesendet",
                            task.getTitle()
                    );
                    processed = true;
                } else {
                    logger.warn(
                            "E-Mail Reminder für Aufgabe '{}' konnte nicht gesendet werden",
                            task.getTitle()
                    );
                }
            }
        }

        if (processed) {
            reminder.setLastSentAt(
                    LocalDateTime.now()
            );

            reminderRepository.save(reminder);
        }
    }
}