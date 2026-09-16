package com.example.studyplanner.service;

import com.example.studyplanner.entity.Subtask;
import com.example.studyplanner.repository.SubtaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class SubtaskService {

    private final SubtaskRepository subtaskRepository;
    private final TaskService taskService;

    public SubtaskService(
            SubtaskRepository subtaskRepository,
            TaskService taskService
    ) {
        this.subtaskRepository = subtaskRepository;
        this.taskService = taskService;
    }

    @Transactional(readOnly = true)
    public List<Subtask> getSubtasks(
            UUID taskId,
            UUID userId
    ) {
        taskService.getTaskForUser(taskId, userId);

        return subtaskRepository
                .findByTaskIdOrderBySortOrderAsc(taskId);
    }

    @Transactional
    public Subtask createSubtask(
            UUID taskId,
            UUID userId,
            String title,
            boolean done,
            int sortOrder
    ) {
        taskService.getTaskForUser(taskId, userId);

        validateTitle(title);

        Subtask subtask = new Subtask();
        subtask.setTaskId(taskId);
        subtask.setTitle(title);
        subtask.setDone(done);
        subtask.setSortOrder(sortOrder);

        return subtaskRepository.save(subtask);
    }

    @Transactional
    public Subtask updateSubtask(
            UUID taskId,
            UUID subtaskId,
            UUID userId,
            String title,
            boolean done,
            int sortOrder
    ) {
        taskService.getTaskForUser(taskId, userId);

        validateTitle(title);

        Subtask subtask = getSubtask(subtaskId);

        checkSubtaskBelongsToTask(subtask, taskId);

        subtask.setTitle(title);
        subtask.setDone(done);
        subtask.setSortOrder(sortOrder);

        return subtaskRepository.save(subtask);
    }

    @Transactional
    public void deleteSubtask(
            UUID taskId,
            UUID subtaskId,
            UUID userId
    ) {
        taskService.getTaskForUser(taskId, userId);

        Subtask subtask = getSubtask(subtaskId);

        checkSubtaskBelongsToTask(subtask, taskId);

        subtaskRepository.delete(subtask);
    }

    private Subtask getSubtask(UUID subtaskId) {
        return subtaskRepository.findById(subtaskId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Subtask nicht gefunden"
                        )
                );
    }

    private void checkSubtaskBelongsToTask(
            Subtask subtask,
            UUID taskId
    ) {
        if (!subtask.getTaskId().equals(taskId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Subtask gehört nicht zu dieser Aufgabe"
            );
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Subtask-Titel darf nicht leer sein"
            );
        }
    }
}