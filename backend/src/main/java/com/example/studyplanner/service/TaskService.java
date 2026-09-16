package com.example.studyplanner.service;

import com.example.studyplanner.dto.CreateTaskRequest;
import com.example.studyplanner.dto.SubtaskDTO;
import com.example.studyplanner.dto.TaskDTO;
import com.example.studyplanner.entity.Subtask;
import com.example.studyplanner.entity.Task;
import com.example.studyplanner.enums.UrgencyLevel;
import com.example.studyplanner.repository.SubtaskRepository;
import com.example.studyplanner.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final SubtaskRepository subtaskRepository;
    private final UrgencyService urgencyService;

    public TaskService(
            TaskRepository taskRepository,
            SubtaskRepository subtaskRepository,
            UrgencyService urgencyService
    ) {
        this.taskRepository = taskRepository;
        this.subtaskRepository = subtaskRepository;
        this.urgencyService = urgencyService;
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksForUser(UUID userId) {
        return taskRepository.findByUserIdOrderByDeadlineAsc(userId);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTaskDTOsForUser(UUID userId) {
        return getTasksForUser(userId)
                .stream()
                .map(this::toTaskDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Task getTaskForUser(
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

        checkOwnership(task, userId);

        return task;
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskDTOForUser(
            UUID taskId,
            UUID userId
    ) {
        return toTaskDTO(
                getTaskForUser(taskId, userId)
        );
    }

    @Transactional
    public Task createTask(
            CreateTaskRequest request,
            UUID userId
    ) {
        validateTaskRequest(request);

        Task task = new Task();

        task.setUserId(userId);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setType(request.type());
        task.setSubject(request.subject());
        task.setDeadline(LocalDate.parse(request.deadline()));

        task.setEstimatedHours(
                request.estimatedHours() != null
                        ? request.estimatedHours()
                        : 0
        );

        task.setWeight(
                request.weight() != null
                        ? request.weight()
                        : 1
        );

        task.setProgressPercent(0);
        task.setActualHours(0);

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(
            UUID taskId,
            UUID userId,
            CreateTaskRequest request
    ) {
        validateTaskRequest(request);

        Task task = getTaskForUser(
                taskId,
                userId
        );

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setType(request.type());
        task.setSubject(request.subject());
        task.setDeadline(LocalDate.parse(request.deadline()));

        task.setEstimatedHours(
                request.estimatedHours() != null
                        ? request.estimatedHours()
                        : 0
        );

        task.setWeight(
                request.weight() != null
                        ? request.weight()
                        : 1
        );

        return taskRepository.save(task);
    }

    @Transactional
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(
            UUID taskId,
            UUID userId
    ) {
        Task task = getTaskForUser(
                taskId,
                userId
        );

        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public TaskDTO toTaskDTO(Task task) {
        List<SubtaskDTO> subtasks =
                subtaskRepository
                        .findByTaskIdOrderBySortOrderAsc(task.getId())
                        .stream()
                        .map(this::toSubtaskDTO)
                        .toList();

        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getType(),
                task.getSubject(),
                task.getDeadline(),
                task.getEstimatedHours(),
                task.getActualHours(),
                task.getProgressPercent(),
                task.getStatus(),
                task.getWeight(),
                calculateUrgency(task),
                subtasks,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private SubtaskDTO toSubtaskDTO(Subtask subtask) {
        return new SubtaskDTO(
                subtask.getId(),
                subtask.getTitle(),
                subtask.isDone(),
                subtask.getSortOrder()
        );
    }

    public UrgencyLevel calculateUrgency(Task task) {
        return urgencyService.calculateUrgency(
                task.getDeadline()
        );
    }

    private void checkOwnership(
            Task task,
            UUID userId
    ) {
        if (!task.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Zugriff verweigert"
            );
        }
    }

    private void validateTaskRequest(
            CreateTaskRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Aufgabe darf nicht null sein"
            );
        }

        if (request.title() == null
                || request.title().isBlank()) {
            throw new IllegalArgumentException(
                    "Titel darf nicht leer sein"
            );
        }

        if (request.type() == null) {
            throw new IllegalArgumentException(
                    "Aufgabentyp darf nicht null sein"
            );
        }

        if (request.deadline() == null
                || request.deadline().isBlank()) {
            throw new IllegalArgumentException(
                    "Deadline darf nicht leer sein"
            );
        }

        if (request.estimatedHours() != null
                && request.estimatedHours() < 0) {
            throw new IllegalArgumentException(
                    "Geschätzte Stunden dürfen nicht negativ sein"
            );
        }

        if (request.weight() != null
                && (request.weight() < 1 || request.weight() > 10)) {
            throw new IllegalArgumentException(
                    "Gewichtung muss zwischen 1 und 10 liegen"
            );
        }
    }
}