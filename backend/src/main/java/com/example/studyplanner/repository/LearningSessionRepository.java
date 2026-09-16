package com.example.studyplanner.repository;

import com.example.studyplanner.entity.LearningSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LearningSessionRepository extends JpaRepository<LearningSession, UUID> {

    List<LearningSession> findByTaskId(UUID taskId);
}