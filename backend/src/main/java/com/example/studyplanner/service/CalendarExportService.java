package com.example.studyplanner.service;

import com.example.studyplanner.entity.Task;
import com.example.studyplanner.enums.TaskStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalendarExportService {

    public String generateIcal(List<Task> tasks) {

        StringBuilder ical = new StringBuilder();

        ical.append("BEGIN:VCALENDAR\r\n");
        ical.append("VERSION:2.0\r\n");
        ical.append("PRODID:-//StudyPlanner//StudyPlanner//DE\r\n");
        ical.append("CALSCALE:GREGORIAN\r\n");

        for (Task task : tasks) {

            if (task.getStatus() != TaskStatus.OPEN) {
                continue;
            }

            LocalDate deadline = task.getDeadline();

            ical.append("BEGIN:VEVENT\r\n");

            ical.append("UID:")
                    .append(task.getId())
                    .append("@studyplanner\r\n");

            ical.append("DTSTART;VALUE=DATE:")
                    .append(deadline.toString().replace("-", ""))
                    .append("\r\n");

            ical.append("DTEND;VALUE=DATE:")
                    .append(deadline.plusDays(1)
                            .toString()
                            .replace("-", ""))
                    .append("\r\n");

            ical.append("SUMMARY:")
                    .append(escapeIcal(task.getTitle()))
                    .append("\r\n");

            if (task.getDescription() != null) {
                ical.append("DESCRIPTION:")
                        .append(escapeIcal(task.getDescription()))
                        .append("\r\n");
            }

            ical.append("END:VEVENT\r\n");
        }

        ical.append("END:VCALENDAR\r\n");

        return ical.toString();
    }

    private String escapeIcal(String value) {
        return value
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}