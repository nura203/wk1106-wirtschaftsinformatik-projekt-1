package com.example.studyplanner.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private ObjectProvider<JavaMailSender> mailSenderProvider;

    @Mock
    private JavaMailSender mailSender;

    @Test
    void isConfiguredReturnsTrueWhenMailIsConfigured() {
        when(mailSenderProvider.getIfAvailable())
                .thenReturn(mailSender);

        EmailService emailService =
                new EmailService(
                        mailSenderProvider,
                        "smtp.example.com",
                        "test@example.com"
                );

        assertTrue(emailService.isConfigured());
    }

    @Test
    void isConfiguredReturnsFalseWhenMailSenderIsMissing() {
        when(mailSenderProvider.getIfAvailable())
                .thenReturn(null);

        EmailService emailService =
                new EmailService(
                        mailSenderProvider,
                        "smtp.example.com",
                        "test@example.com"
                );

        assertFalse(emailService.isConfigured());
    }

    @Test
    void isConfiguredReturnsFalseWhenHostIsEmpty() {
        when(mailSenderProvider.getIfAvailable())
                .thenReturn(mailSender);

        EmailService emailService =
                new EmailService(
                        mailSenderProvider,
                        "",
                        "test@example.com"
                );

        assertFalse(emailService.isConfigured());
    }

    @Test
    void isConfiguredReturnsFalseWhenUserIsEmpty() {
        when(mailSenderProvider.getIfAvailable())
                .thenReturn(mailSender);

        EmailService emailService =
                new EmailService(
                        mailSenderProvider,
                        "smtp.example.com",
                        ""
                );

        assertFalse(emailService.isConfigured());
    }

    @Test
    void sendReminderReturnsFalseWhenNotConfigured() {
        when(mailSenderProvider.getIfAvailable())
                .thenReturn(null);

        EmailService emailService =
                new EmailService(
                        mailSenderProvider,
                        "",
                        ""
                );

        boolean result =
                emailService.sendReminder(
                        "test@example.com",
                        "Testaufgabe",
                        "2026-09-20"
                );

        assertFalse(result);
    }

    @Test
    void sendReminderReturnsFalseForEmptyRecipient() {
        when(mailSenderProvider.getIfAvailable())
                .thenReturn(mailSender);

        EmailService emailService =
                new EmailService(
                        mailSenderProvider,
                        "smtp.example.com",
                        "test@example.com"
                );

        boolean result =
                emailService.sendReminder(
                        "",
                        "Testaufgabe",
                        "2026-09-20"
                );

        assertFalse(result);
    }

    @Test
    void sendReminderSendsMailWhenConfigured() {
        when(mailSenderProvider.getIfAvailable())
                .thenReturn(mailSender);

        EmailService emailService =
                new EmailService(
                        mailSenderProvider,
                        "smtp.example.com",
                        "test@example.com"
                );

        boolean result =
                emailService.sendReminder(
                        "recipient@example.com",
                        "Testaufgabe",
                        "2026-09-20"
                );

        assertTrue(result);

        verify(mailSender).send(
                any(SimpleMailMessage.class)
        );
    }

    @Test
    void sendReminderReturnsFalseWhenSendingFails() {
        when(mailSenderProvider.getIfAvailable())
                .thenReturn(mailSender);

        doThrow(new RuntimeException("SMTP Fehler"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        EmailService emailService =
                new EmailService(
                        mailSenderProvider,
                        "smtp.example.com",
                        "test@example.com"
                );

        boolean result =
                emailService.sendReminder(
                        "recipient@example.com",
                        "Testaufgabe",
                        "2026-09-20"
                );

        assertFalse(result);
    }
}