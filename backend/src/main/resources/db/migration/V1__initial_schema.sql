CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE task (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    subject VARCHAR(100),
    deadline DATE NOT NULL,
    estimated_hours INTEGER NOT NULL DEFAULT 0,
    actual_hours INTEGER NOT NULL DEFAULT 0,
    progress_percent INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    weight INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_task_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE TABLE reminder (
    id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    days_before INTEGER NOT NULL,
    channel VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_sent_at TIMESTAMP,

    CONSTRAINT fk_reminder_task
        FOREIGN KEY (task_id)
        REFERENCES task(id)
        ON DELETE CASCADE
);

CREATE TABLE subtask (
    id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    done BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT fk_subtask_task
        FOREIGN KEY (task_id)
        REFERENCES task(id)
        ON DELETE CASCADE
);

CREATE TABLE learning_session (
    id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    duration_minutes INTEGER NOT NULL,
    notes TEXT,
    recorded_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_learning_session_task
        FOREIGN KEY (task_id)
        REFERENCES task(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_task_user_id
    ON task(user_id);

CREATE INDEX idx_task_deadline
    ON task(deadline);

CREATE INDEX idx_reminder_task_id
    ON reminder(task_id);

CREATE INDEX idx_subtask_task_id
    ON subtask(task_id);

CREATE INDEX idx_learning_session_task_id
    ON learning_session(task_id);