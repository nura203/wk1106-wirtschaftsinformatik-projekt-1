package com.example.studyplanner.mapper;

import com.example.studyplanner.dto.SubtaskDTO;
import com.example.studyplanner.dto.TaskDTO;
import com.example.studyplanner.entity.Subtask;
import com.example.studyplanner.entity.Task;
import com.example.studyplanner.enums.UrgencyLevel;
import com.example.studyplanner.repository.SubtaskRepository;
import com.example.studyplanner.service.UrgencyService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {

    private final UrgencyService urgencyService;
    private final SubtaskRepository subtaskRepository;

    public TaskMapper(
            UrgencyService urgencyService,
            SubtaskRepository subtaskRepository
    ) {
        this.urgencyService = urgencyService;
        this.subtaskRepository = subtaskRepository;
    }

    public TaskDTO toDto(Task task) {

        UrgencyLevel urgency =
                urgencyService.calculateUrgency(task.getDeadline());

        List<SubtaskDTO> subtasks =
                subtaskRepository
                        .findByTaskIdOrderBySortOrderAsc(task.getId())
                        .stream()
                        .map(this::toSubtaskDto)
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
                urgency,
                subtasks,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private SubtaskDTO toSubtaskDto(Subtask subtask) {
        return new SubtaskDTO(
                subtask.getId(),
                subtask.getTitle(),
                subtask.isDone(),
                subtask.getSortOrder()
        );
    }
}