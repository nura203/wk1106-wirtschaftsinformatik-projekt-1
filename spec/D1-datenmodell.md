# D1 — Datenmodell

D1 beschreibt die persistierten Entitäten, ihre Attribute, Beziehungen und wesentlichen Invarianten. Die fachlichen Typen der Attribute sind in D2 definiert.

---

## D1.1 Entity-Relationship-Diagramm

```mermaid
erDiagram

    USER {
        UUID id PK
        VARCHAR email UK
        VARCHAR username
        VARCHAR password_hash
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    TASK {
        UUID id PK
        UUID user_id FK
        VARCHAR title
        TEXT description
        TaskType type
        VARCHAR subject
        DATE deadline
        INT estimated_hours
        INT actual_hours
        INT progress_percent
        TaskStatus status
        INT weight
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    SUBTASK {
        UUID id PK
        UUID task_id FK
        VARCHAR title
        BOOLEAN done
        INT sort_order
    }

    LEARNING_SESSION {
        UUID id PK
        UUID task_id FK
        INT duration_minutes
        TEXT notes
        TIMESTAMP recorded_at
    }

    REMINDER {
        UUID id PK
        UUID task_id FK
        INT days_before
        ReminderChannel channel
        BOOLEAN active
        TIMESTAMP last_sent_at
    }

    USER ||--o{ TASK : "besitzt"
    TASK ||--o{ SUBTASK : "enthält"
    TASK ||--o{ LEARNING_SESSION : "hat"
    TASK ||--o{ REMINDER : "hat"