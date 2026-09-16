package com.example.studyplanner.repository;

import com.example.studyplanner.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    List<Reminder> findByTaskId(UUID taskId);
}