package com.example.studyplanner.service;

import com.example.studyplanner.entity.Task;
import com.example.studyplanner.enums.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CalendarExportServiceTest {

    private final CalendarExportService calendarExportService =
            new CalendarExportService();

    @Test
    void generateIcalExportsOpenTask() {
        UUID taskId = UUID.randomUUID();

        Task task = mock(Task.class);

        when(task.getStatus())
                .thenReturn(TaskStatus.OPEN);

        when(task.getId())
                .thenReturn(taskId);

        when(task.getDeadline())
                .thenReturn(LocalDate.of(2026, 9, 20));

        when(task.getTitle())
                .thenReturn("Klausur");

        when(task.getDescription())
                .thenReturn("Lernen für die Klausur");

        String result =
                calendarExportService.generateIcal(
                        List.of(task)
                );

        assertTrue(
                result.contains("BEGIN:VCALENDAR\r\n")
        );

        assertTrue(
                result.contains("VERSION:2.0\r\n")
        );

        assertTrue(
                result.contains(
                        "UID:" + taskId + "@studyplanner\r\n"
                )
        );

        assertTrue(
                result.contains(
                        "DTSTART;VALUE=DATE:20260920\r\n"
                )
        );

        assertTrue(
                result.contains(
                        "DTEND;VALUE=DATE:20260921\r\n"
                )
        );

        assertTrue(
                result.contains(
                        "SUMMARY:Klausur\r\n"
                )
        );

        assertTrue(
                result.contains(
                        "DESCRIPTION:Lernen für die Klausur\r\n"
                )
        );

        assertTrue(
                result.contains("END:VEVENT\r\n")
        );

        assertTrue(
                result.endsWith("END:VCALENDAR\r\n")
        );
    }

    @Test
    void generateIcalSkipsDoneTask() {
        Task task = mock(Task.class);

        when(task.getStatus())
                .thenReturn(TaskStatus.DONE);

        when(task.getTitle())
                .thenReturn("Erledigte Aufgabe");

        String result =
                calendarExportService.generateIcal(
                        List.of(task)
                );

        assertFalse(
                result.contains("BEGIN:VEVENT")
        );

        assertFalse(
                result.contains("Erledigte Aufgabe")
        );

        assertTrue(
                result.contains("BEGIN:VCALENDAR\r\n")
        );

        assertTrue(
                result.contains("END:VCALENDAR\r\n")
        );
    }

    @Test
    void generateIcalExportsTaskWithoutDescription() {
        Task task = mock(Task.class);

        when(task.getStatus())
                .thenReturn(TaskStatus.OPEN);

        when(task.getId())
                .thenReturn(UUID.randomUUID());

        when(task.getDeadline())
                .thenReturn(LocalDate.of(2026, 10, 1));

        when(task.getTitle())
                .thenReturn("Aufgabe ohne Beschreibung");

        when(task.getDescription())
                .thenReturn(null);

        String result =
                calendarExportService.generateIcal(
                        List.of(task)
                );

        assertTrue(
                result.contains(
                        "SUMMARY:Aufgabe ohne Beschreibung\r\n"
                )
        );

        assertFalse(
                result.contains("DESCRIPTION:")
        );
    }

    @Test
    void generateIcalEscapesSpecialCharacters() {
        Task task = mock(Task.class);

        when(task.getStatus())
                .thenReturn(TaskStatus.OPEN);

        when(task.getId())
                .thenReturn(UUID.randomUUID());

        when(task.getDeadline())
                .thenReturn(LocalDate.of(2026, 9, 25));

        when(task.getTitle())
                .thenReturn(
                        "Test, Aufgabe; wichtig\\"
                );

        when(task.getDescription())
                .thenReturn(
                        "Zeile 1\nZeile 2"
                );

        String result =
                calendarExportService.generateIcal(
                        List.of(task)
                );

        assertTrue(
                result.contains(
                        "SUMMARY:Test\\, Aufgabe\\; wichtig\\\\\r\n"
                )
        );

        assertTrue(
                result.contains(
                        "DESCRIPTION:Zeile 1\\nZeile 2\r\n"
                )
        );
    }

    @Test
    void generateIcalReturnsEmptyCalendarForEmptyTaskList() {
        String result =
                calendarExportService.generateIcal(
                        List.of()
                );

        assertEquals(
                "BEGIN:VCALENDAR\r\n"
                        + "VERSION:2.0\r\n"
                        + "PRODID:-//StudyPlanner//StudyPlanner//DE\r\n"
                        + "CALSCALE:GREGORIAN\r\n"
                        + "END:VCALENDAR\r\n",
                result
        );
    }
}