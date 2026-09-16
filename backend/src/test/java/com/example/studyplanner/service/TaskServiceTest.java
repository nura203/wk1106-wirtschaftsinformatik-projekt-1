package com.example.studyplanner.service;

import com.example.studyplanner.dto.CreateTaskRequest;
import com.example.studyplanner.entity.Task;
import com.example.studyplanner.enums.TaskType;
import com.example.studyplanner.enums.UrgencyLevel;
import com.example.studyplanner.repository.SubtaskRepository;
import com.example.studyplanner.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SubtaskRepository subtaskRepository;

    @Mock
    private UrgencyService urgencyService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getTasksForUserReturnsUserTasks() {
        UUID userId = UUID.randomUUID();

        Task task = new Task();

        when(taskRepository.findByUserIdOrderByDeadlineAsc(userId))
                .thenReturn(List.of(task));

        List<Task> result =
                taskService.getTasksForUser(userId);

        assertEquals(1, result.size());
        assertEquals(task, result.get(0));
    }

    @Test
    void createTaskCreatesAndSavesTask() {
        UUID userId = UUID.randomUUID();

        CreateTaskRequest request =
                new CreateTaskRequest(
                        "Klausur vorbereiten",
                        TaskType.EXAM,
                        LocalDate.now().plusDays(10).toString(),
                        "Beschreibung",
                        "Wirtschaftsinformatik",
                        10,
                        5
                );

        Task savedTask = new Task();

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        Task result =
                taskService.createTask(request, userId);

        assertEquals(savedTask, result);

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTaskRejectsEmptyTitle() {
        UUID userId = UUID.randomUUID();

        CreateTaskRequest request =
                new CreateTaskRequest(
                        "",
                        TaskType.EXAM,
                        LocalDate.now().plusDays(10).toString(),
                        null,
                        null,
                        10,
                        1
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(request, userId)
        );
    }

    @Test
    void createTaskRejectsMissingType() {
        UUID userId = UUID.randomUUID();

        CreateTaskRequest request =
                new CreateTaskRequest(
                        "Aufgabe",
                        null,
                        LocalDate.now().plusDays(10).toString(),
                        null,
                        null,
                        10,
                        1
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(request, userId)
        );
    }

    @Test
    void createTaskRejectsMissingDeadline() {
        UUID userId = UUID.randomUUID();

        CreateTaskRequest request =
                new CreateTaskRequest(
                        "Aufgabe",
                        TaskType.ASSIGNMENT,
                        "",
                        null,
                        null,
                        10,
                        1
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(request, userId)
        );
    }

    @Test
    void createTaskRejectsNegativeEstimatedHours() {
        UUID userId = UUID.randomUUID();

        CreateTaskRequest request =
                new CreateTaskRequest(
                        "Aufgabe",
                        TaskType.ASSIGNMENT,
                        LocalDate.now().plusDays(10).toString(),
                        null,
                        null,
                        -1,
                        1
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(request, userId)
        );
    }

    @Test
    void createTaskRejectsInvalidWeight() {
        UUID userId = UUID.randomUUID();

        CreateTaskRequest request =
                new CreateTaskRequest(
                        "Aufgabe",
                        TaskType.EXAM,
                        LocalDate.now().plusDays(10).toString(),
                        null,
                        null,
                        10,
                        11
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(request, userId)
        );
    }

    @Test
    void getTaskForUserReturnsOwnedTask() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(userId);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        Task result =
                taskService.getTaskForUser(taskId, userId);

        assertEquals(task, result);
    }

    @Test
    void getTaskForUserRejectsMissingTask() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> taskService.getTaskForUser(taskId, userId)
        );
    }

    @Test
    void getTaskForUserRejectsTaskOfAnotherUser() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(otherUserId);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        assertThrows(
                RuntimeException.class,
                () -> taskService.getTaskForUser(taskId, userId)
        );
    }

    @Test
    void deleteTaskDeletesOwnedTask() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(userId);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        taskService.deleteTask(taskId, userId);

        verify(taskRepository).delete(task);
    }

    @Test
    void updateTaskUpdatesOwnedTask() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task task = new Task();
        task.setUserId(userId);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        CreateTaskRequest request =
                new CreateTaskRequest(
                        "Neue Aufgabe",
                        TaskType.GOAL,
                        LocalDate.now().plusDays(20).toString(),
                        "Neue Beschreibung",
                        "Mathe",
                        8,
                        3
                );

        Task result =
                taskService.updateTask(
                        taskId,
                        userId,
                        request
                );

        assertEquals(task, result);
        verify(taskRepository).save(task);
    }

    @Test
    void saveTaskSavesTask() {
        Task task = new Task();

        when(taskRepository.save(task))
                .thenReturn(task);

        Task result =
                taskService.saveTask(task);

        assertEquals(task, result);
        verify(taskRepository).save(task);
    }

    @Test
    void calculateUrgencyUsesUrgencyService() {
        LocalDate deadline =
                LocalDate.now().plusDays(3);

        Task task = new Task();
        task.setDeadline(deadline);

        when(urgencyService.calculateUrgency(deadline))
                .thenReturn(UrgencyLevel.RED);

        UrgencyLevel result =
                taskService.calculateUrgency(task);

        assertEquals(
                UrgencyLevel.RED,
                result
        );
    }
}