package com.example.studyplanner.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "subtask")
public class Subtask {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private boolean done = false;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    public UUID getId() {
        return id;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}