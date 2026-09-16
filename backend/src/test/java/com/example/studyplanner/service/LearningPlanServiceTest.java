package com.example.studyplanner.service;

import com.example.studyplanner.dto.LearningPlanDTO;
import com.example.studyplanner.dto.PlanTaskDTO;
import com.example.studyplanner.entity.Task;
import com.example.studyplanner.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LearningPlanServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private LearningPlanService learningPlanService;

    @Test
    void generateLearningPlanIgnoresDoneTasks() {
        UUID userId = UUID.randomUUID();

        Task openTask = createTask(
                "Offene Aufgabe",
                10,
                0,
                1,
                LocalDate.now().plusDays(10)
        );

        Task doneTask = mock(Task.class);

        when(doneTask.getProgressPercent())
                .thenReturn(100);

        when(taskRepository.findByUserIdOrderByDeadlineAsc(userId))
                .thenReturn(List.of(openTask, doneTask));

        LearningPlanDTO plan =
                learningPlanService.generateLearningPlan(userId);

        assertEquals(7, plan.weekEntries().size());

        for (var weekEntry : plan.weekEntries()) {
            assertEquals(1, weekEntry.tasks().size());
            assertEquals(
                    "Offene Aufgabe",
                    weekEntry.tasks().get(0).title()
            );
        }
    }

    @Test
    void generateLearningPlanUsesFallbackForZeroEstimatedHours() {
        UUID userId = UUID.randomUUID();

        Task task = createTask(
                "Aufgabe ohne Aufwand",
                0,
                0,
                1,
                LocalDate.now().plusDays(10)
        );

        when(taskRepository.findByUserIdOrderByDeadlineAsc(userId))
                .thenReturn(List.of(task));

        LearningPlanDTO plan =
                learningPlanService.generateLearningPlan(userId);

        PlanTaskDTO planTask =
                plan.weekEntries()
                        .get(0)
                        .tasks()
                        .get(0);

        assertFalse(plan.weekEntries().isEmpty());
        assertEquals(6, planTask.recommendedMinutes());
    }

    @Test
    void generateLearningPlanCalculatesRemainingEffort() {
        UUID userId = UUID.randomUUID();

        Task task = createTask(
                "Fortschrittsaufgabe",
                10,
                50,
                1,
                LocalDate.now().plusDays(10)
        );

        when(taskRepository.findByUserIdOrderByDeadlineAsc(userId))
                .thenReturn(List.of(task));

        LearningPlanDTO plan =
                learningPlanService.generateLearningPlan(userId);

        PlanTaskDTO planTask =
                plan.weekEntries()
                        .get(0)
                        .tasks()
                        .get(0);

        assertEquals(30, planTask.recommendedMinutes());
    }

    @Test
    void generateLearningPlanCalculatesUrgency() {
        UUID userId = UUID.randomUUID();

        Task task = createTask(
                "Dringende Aufgabe",
                10,
                0,
                1,
                LocalDate.now().plusDays(2)
        );

        when(taskRepository.findByUserIdOrderByDeadlineAsc(userId))
                .thenReturn(List.of(task));

        LearningPlanDTO plan =
                learningPlanService.generateLearningPlan(userId);

        PlanTaskDTO planTask =
                plan.weekEntries()
                        .get(0)
                        .tasks()
                        .get(0);

        assertEquals("RED", planTask.urgency());
    }

    @Test
    void generateLearningPlanOrdersTasksByPriority() {
        UUID userId = UUID.randomUUID();

        Task lowPriorityTask = createTask(
                "Niedrige Priorität",
                2,
                0,
                1,
                LocalDate.now().plusDays(10)
        );

        Task highPriorityTask = createTask(
                "Hohe Priorität",
                10,
                0,
                5,
                LocalDate.now().plusDays(10)
        );

        when(taskRepository.findByUserIdOrderByDeadlineAsc(userId))
                .thenReturn(
                        List.of(
                                lowPriorityTask,
                                highPriorityTask
                        )
                );

        LearningPlanDTO plan =
                learningPlanService.generateLearningPlan(userId);

        List<PlanTaskDTO> tasks =
                plan.weekEntries()
                        .get(0)
                        .tasks();

        assertEquals(
                "Hohe Priorität",
                tasks.get(0).title()
        );

        assertEquals(
                "Niedrige Priorität",
                tasks.get(1).title()
        );
    }

    private Task createTask(
            String title,
            int estimatedHours,
            int progressPercent,
            int weight,
            LocalDate deadline
    ) {
        Task task = mock(Task.class);

        UUID taskId = UUID.randomUUID();

        lenient()
                .when(task.getId())
                .thenReturn(taskId);

        lenient()
                .when(task.getTitle())
                .thenReturn(title);

        lenient()
                .when(task.getDeadline())
                .thenReturn(deadline);

        lenient()
                .when(task.getEstimatedHours())
                .thenReturn(estimatedHours);

        lenient()
                .when(task.getProgressPercent())
                .thenReturn(progressPercent);

        lenient()
                .when(task.getWeight())
                .thenReturn(weight);

        return task;
    }
}