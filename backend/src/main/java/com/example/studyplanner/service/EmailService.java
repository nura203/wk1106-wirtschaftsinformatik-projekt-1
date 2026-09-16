package com.example.studyplanner.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String smtpHost;
    private final String smtpUser;

    public EmailService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${spring.mail.host:}") String smtpHost,
            @Value("${spring.mail.username:}") String smtpUser
    ) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.smtpHost = smtpHost;
        this.smtpUser = smtpUser;
    }

    public boolean isConfigured() {
        return mailSender != null
                && smtpHost != null
                && !smtpHost.isBlank()
                && smtpUser != null
                && !smtpUser.isBlank();
    }

    public boolean sendReminder(
            String recipient,
            String taskTitle,
            String deadline
    ) {
        if (!isConfigured()) {
            return false;
        }

        if (recipient == null || recipient.isBlank()) {
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(recipient);
            message.setSubject(
                    "StudyPlanner Erinnerung: " + taskTitle
            );

            message.setText(
                    "Erinnerung für deine Aufgabe:\n\n"
                            + "Aufgabe: " + taskTitle + "\n"
                            + "Deadline: " + deadline + "\n\n"
                            + "Diese Erinnerung wurde von StudyPlanner gesendet."
            );

            mailSender.send(message);

            return true;

        } catch (Exception exception) {
            return false;
        }
    }
}