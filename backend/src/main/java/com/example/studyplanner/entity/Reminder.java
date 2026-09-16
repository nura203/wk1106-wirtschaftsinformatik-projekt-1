package com.example.studyplanner.entity;

import com.example.studyplanner.enums.ReminderChannel;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reminder")
public class Reminder {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "days_before", nullable = false)
    private int daysBefore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderChannel channel;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "last_sent_at")
    private LocalDateTime lastSentAt;

    public UUID getId() {
        return id;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public int getDaysBefore() {
        return daysBefore;
    }

    public void setDaysBefore(int daysBefore) {
        this.daysBefore = daysBefore;
    }

    public ReminderChannel getChannel() {
        return channel;
    }

    public void setChannel(ReminderChannel channel) {
        this.channel = channel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastSentAt() {
        return lastSentAt;
    }

    public void setLastSentAt(LocalDateTime lastSentAt) {
        this.lastSentAt = lastSentAt;
    }
}