package com.example.studyplanner.service;

import com.example.studyplanner.dto.LearningSessionDTO;
import com.example.studyplanner.dto.UpdateProgressRequest;
import com.example.studyplanner.entity.LearningSession;
import com.example.studyplanner.entity.Task;
import com.example.studyplanner.repository.LearningSessionRepository;
import com.example.studyplanner.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private LearningSessionRepository learningSessionRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    void updateProgressUpdatesTask() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(userId);
        task.setProgressPercent(0);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(learningSessionRepository.findByTaskId(taskId))
                .thenReturn(List.of());

        when(taskRepository.save(task))
                .thenReturn(task);

        Task result = progressService.updateProgress(
                taskId,
                userId,
                new UpdateProgressRequest(50, null, null)
        );

        assertEquals(50, result.getProgressPercent());
        assertEquals(0, result.getActualHours());

        verify(taskRepository).save(task);
    }

    @Test
    void updateProgressAddsLearningSession() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(userId);

        LearningSession session = new LearningSession();
        session.setTaskId(taskId);
        session.setDurationMinutes(30);
        session.setNotes("Test");

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(learningSessionRepository.save(any(LearningSession.class)))
                .thenReturn(session);

        when(learningSessionRepository.findByTaskId(taskId))
                .thenReturn(List.of(session));

        when(taskRepository.save(task))
                .thenReturn(task);

        Task result = progressService.updateProgress(
                taskId,
                userId,
                new UpdateProgressRequest(
                        30,
                        30,
                        "Test"
                )
        );

        assertEquals(30, result.getProgressPercent());
        assertEquals(0, result.getActualHours());

        verify(learningSessionRepository)
                .save(any(LearningSession.class));
    }

    @Test
    void updateProgressRejectsInvalidProgress() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(userId);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        assertThrows(
                IllegalArgumentException.class,
                () -> progressService.updateProgress(
                        taskId,
                        userId,
                        new UpdateProgressRequest(
                                101,
                                null,
                                null
                        )
                )
        );
    }

    @Test
    void updateProgressRejectsNegativeProgress() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(userId);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        assertThrows(
                IllegalArgumentException.class,
                () -> progressService.updateProgress(
                        taskId,
                        userId,
                        new UpdateProgressRequest(
                                -1,
                                null,
                                null
                        )
                )
        );
    }

    @Test
    void updateProgressRejectsInvalidSessionDuration() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(userId);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        assertThrows(
                IllegalArgumentException.class,
                () -> progressService.updateProgress(
                        taskId,
                        userId,
                        new UpdateProgressRequest(
                                50,
                                0,
                                null
                        )
                )
        );
    }

    @Test
    void updateProgressRejectsNullRequest() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(userId);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        assertThrows(
                IllegalArgumentException.class,
                () -> progressService.updateProgress(
                        taskId,
                        userId,
                        null
                )
        );
    }

    @Test
    void getLearningSessionsReturnsSessions() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(userId);

        LearningSession session = new LearningSession();
        session.setTaskId(taskId);
        session.setDurationMinutes(45);
        session.setNotes("Lernnotizen");

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(learningSessionRepository.findByTaskId(taskId))
                .thenReturn(List.of(session));

        List<LearningSessionDTO> result =
                progressService.getLearningSessions(
                        taskId,
                        userId
                );

        assertEquals(1, result.size());
        assertEquals(
                45,
                result.get(0).durationMinutes()
        );
        assertEquals(
                "Lernnotizen",
                result.get(0).notes()
        );
    }

    @Test
    void getLearningSessionsReturnsEmptyList() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(userId);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(learningSessionRepository.findByTaskId(taskId))
                .thenReturn(List.of());

        List<LearningSessionDTO> result =
                progressService.getLearningSessions(
                        taskId,
                        userId
                );

        assertEquals(0, result.size());
    }

    @Test
    void getTaskForUserRejectsMissingTask() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> progressService.getLearningSessions(
                        taskId,
                        userId
                )
        );
    }

    @Test
    void getTaskForUserRejectsOtherUser() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(otherUserId);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        assertThrows(
                ResponseStatusException.class,
                () -> progressService.getLearningSessions(
                        taskId,
                        userId
                )
        );
    }
}