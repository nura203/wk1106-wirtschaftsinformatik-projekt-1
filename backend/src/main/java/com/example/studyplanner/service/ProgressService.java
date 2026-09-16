package com.example.studyplanner.service;

import com.example.studyplanner.dto.LearningSessionDTO;
import com.example.studyplanner.dto.UpdateProgressRequest;
import com.example.studyplanner.entity.LearningSession;
import com.example.studyplanner.entity.Task;
import com.example.studyplanner.repository.LearningSessionRepository;
import com.example.studyplanner.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ProgressService {

    private final TaskRepository taskRepository;
    private final LearningSessionRepository learningSessionRepository;

    public ProgressService(
            TaskRepository taskRepository,
            LearningSessionRepository learningSessionRepository
    ) {
        this.taskRepository = taskRepository;
        this.learningSessionRepository = learningSessionRepository;
    }

    @Transactional
    public Task updateProgress(
            UUID taskId,
            UUID userId,
            UpdateProgressRequest request
    ) {
        Task task = getTaskForUser(taskId, userId);

        if (request == null) {
            throw new IllegalArgumentException(
                    "Fortschrittsanfrage darf nicht null sein"
            );
        }

        if (request.progressPercent() < 0
                || request.progressPercent() > 100) {

            throw new IllegalArgumentException(
                    "Fortschritt muss zwischen 0 und 100 liegen"
            );
        }

        task.setProgressPercent(
                request.progressPercent()
        );

        if (request.sessionDurationMinutes() != null) {

            if (request.sessionDurationMinutes() <= 0) {
                throw new IllegalArgumentException(
                        "Lernzeit muss größer als 0 sein"
                );
            }

            addLearningSession(
                    taskId,
                    request.sessionDurationMinutes(),
                    request.notes()
            );
        }

        int totalMinutes =
                learningSessionRepository
                        .findByTaskId(taskId)
                        .stream()
                        .mapToInt(
                                LearningSession::getDurationMinutes
                        )
                        .sum();

        task.setActualHours(
                totalMinutes / 60
        );

        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<LearningSessionDTO> getLearningSessions(
            UUID taskId,
            UUID userId
    ) {
        getTaskForUser(taskId, userId);

        return learningSessionRepository
                .findByTaskId(taskId)
                .stream()
                .map(this::toLearningSessionDTO)
                .toList();
    }

    private LearningSession addLearningSession(
            UUID taskId,
            int durationMinutes,
            String notes
    ) {
        LearningSession session =
                new LearningSession();

        session.setTaskId(taskId);
        session.setDurationMinutes(durationMinutes);
        session.setNotes(notes);

        return learningSessionRepository.save(session);
    }

    private LearningSessionDTO toLearningSessionDTO(
            LearningSession session
    ) {
        return new LearningSessionDTO(
                session.getId(),
                session.getDurationMinutes(),
                session.getNotes(),
                session.getRecordedAt()
        );
    }

    private Task getTaskForUser(
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

        return task;
    }
}