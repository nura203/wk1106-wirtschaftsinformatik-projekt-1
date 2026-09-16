package com.example.studyplanner.service;

import com.example.studyplanner.enums.UrgencyLevel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class UrgencyService {

    public UrgencyLevel calculateUrgency(LocalDate deadline) {
        long daysUntilDeadline =
                ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        deadline
                );

        if (daysUntilDeadline <= 3) {
            return UrgencyLevel.RED;
        }

        if (daysUntilDeadline <= 7) {
            return UrgencyLevel.YELLOW;
        }

        return UrgencyLevel.GREEN;
    }
}