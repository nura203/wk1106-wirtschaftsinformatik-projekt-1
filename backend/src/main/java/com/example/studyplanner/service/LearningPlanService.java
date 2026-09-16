package com.example.studyplanner.service;

import com.example.studyplanner.dto.LearningPlanDTO;
import com.example.studyplanner.dto.PlanTaskDTO;
import com.example.studyplanner.dto.WeekEntryDTO;
import com.example.studyplanner.entity.Task;
import com.example.studyplanner.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class LearningPlanService {

    private final TaskRepository taskRepository;

    public LearningPlanService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public LearningPlanDTO generateLearningPlan(UUID userId) {
        LocalDate today = LocalDate.now();

        LocalDate monday = today.with(DayOfWeek.MONDAY);

        List<Task> tasks = taskRepository
                .findByUserIdOrderByDeadlineAsc(userId)
                .stream()
                .filter(task -> task.getProgressPercent() < 100)
                .sorted(
                        Comparator.comparingDouble(
                                (Task task) ->
                                        calculatePriority(task, today)
                        ).reversed()
                )
                .toList();

        List<WeekEntryDTO> weekEntries =
                new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);

            List<PlanTaskDTO> planTasks = tasks.stream()
                    .map(task ->
                            createPlanTask(task, today)
                    )
                    .toList();

            weekEntries.add(
                    new WeekEntryDTO(
                            date,
                            planTasks
                    )
            );
        }

        return new LearningPlanDTO(
                today,
                weekEntries
        );
    }

    @Transactional(readOnly = true)
    public LearningPlanDTO recalculatePlan(UUID userId) {
        return generateLearningPlan(userId);
    }

    private PlanTaskDTO createPlanTask(
            Task task,
            LocalDate today
    ) {
        long remainingDays = Math.max(
                1,
                ChronoUnit.DAYS.between(
                        today,
                        task.getDeadline()
                )
        );

        int estimatedHours =
                task.getEstimatedHours();

        if (estimatedHours <= 0) {
            estimatedHours = 1;
        }

        double remainingEffort =
                estimatedHours
                        * (
                                1.0
                                        - task.getProgressPercent()
                                        / 100.0
                        );

        double dailyEffort =
                remainingEffort / remainingDays;

        int recommendedMinutes =
                (int) Math.ceil(
                        dailyEffort * 60
                );

        String urgency =
                calculateUrgency(
                        task.getDeadline(),
                        today
                );

        return new PlanTaskDTO(
                task.getId().toString(),
                task.getTitle(),
                recommendedMinutes,
                urgency
        );
    }

    private double calculatePriority(
            Task task,
            LocalDate today
    ) {
        long remainingDays = Math.max(
                1,
                ChronoUnit.DAYS.between(
                        today,
                        task.getDeadline()
                )
        );

        int estimatedHours =
                task.getEstimatedHours();

        if (estimatedHours <= 0) {
            estimatedHours = 1;
        }

        double remainingEffort =
                estimatedHours
                        * (
                                1.0
                                        - task.getProgressPercent()
                                        / 100.0
                        );

        double dailyEffort =
                remainingEffort / remainingDays;

        return dailyEffort * task.getWeight();
    }

    private String calculateUrgency(
            LocalDate deadline,
            LocalDate today
    ) {
        long remainingDays =
                ChronoUnit.DAYS.between(
                        today,
                        deadline
                );

        if (remainingDays <= 3) {
            return "RED";
        }

        if (remainingDays <= 7) {
            return "YELLOW";
        }

        return "GREEN";
    }
}