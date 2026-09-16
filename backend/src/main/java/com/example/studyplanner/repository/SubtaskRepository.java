package com.example.studyplanner.repository;

import com.example.studyplanner.entity.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubtaskRepository extends JpaRepository<Subtask, UUID> {

    List<Subtask> findByTaskIdOrderBySortOrderAsc(UUID taskId);
}