CREATE INDEX idx_task_status
    ON task(status);

CREATE INDEX idx_ls_task_id
    ON learning_session(task_id);

CREATE INDEX idx_reminder_task
    ON reminder(task_id);