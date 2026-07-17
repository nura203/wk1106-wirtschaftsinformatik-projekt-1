# D2 — Datentypenverzeichnis

D2 ist das verbindliche Typenverzeichnis des Study Planers. Alle hier definierten Namen werden in D1 (Datenmodell), S1 (API), im Java-Backend und im TypeScript-Frontend identisch verwendet. Abweichungen im Code sind Fehler, keine Stilfrage (vgl. N2.6).

---

## D2.1 Einfache Typen

| Name | Java-Typ | DB-Typ | TypeScript-Typ | Validierungsregeln |
|------|----------|--------|----------------|--------------------|
| `UserId` | `UUID` | `UUID` | `string` | UUID v4; serverseitig generiert |
| `TaskId` | `UUID` | `UUID` | `string` | UUID v4; serverseitig generiert |
| `Email` | `String` | `VARCHAR(255)` | `string` | RFC 5322; max. 255 Zeichen; UNIQUE in USER |
| `Username` | `String` | `VARCHAR(100)` | `string` | 2–100 Zeichen |
| `PasswordRaw` | `String` | — | `string` | ≥ 8 Zeichen; nur im Request-Body; niemals persistiert |
| `PasswordHash` | `String` | `VARCHAR(60)` | — | bcrypt, 60 Zeichen; nur serverseitig |
| `Title` | `String` | `VARCHAR(255)` | `string` | 1–255 Zeichen; Pflichtfeld |
| `Description` | `String` | `TEXT` | `string \| null` | max. 5000 Zeichen; optional |
| `Subject` | `String` | `VARCHAR(100)` | `string \| null` | max. 100 Zeichen; optional |
| `Deadline` | `LocalDate` | `DATE` | `string` | ISO 8601 `YYYY-MM-DD`; Pflichtfeld |
| `EstimatedHours` | `int` | `INTEGER` | `number` | ≥ 0; ≤ 9999; Default 0 |
| `ActualHours` | `int` | `INTEGER` | `number` | ≥ 0; nur serverseitig gesetzt (AF-09) |
| `ProgressPercent` | `int` | `INTEGER` | `number` | 0–100 inklusiv |
| `Weight` | `int` | `INTEGER` | `number` | 1–10 inklusiv; Default 1 |
| `DaysBefore` | `int` | `INTEGER` | `number` | ≥ 1 |
| `DurationMinutes` | `int` | `INTEGER` | `number` | > 0 |
| `JwtToken` | `String` | — | `string` | HS256-signiert; Ablauf 24 h |

---

## D2.2 Enum-Typen

### `TaskType`

Gibt den Charakter einer Aufgabe an und steuert die Darstellung im UI sowie die Pflichtfelder im Formular.

| Wert | Bedeutung | Typische Pflichtfelder |
|------|-----------|----------------------|
| `EXAM` | Institutionelle Prüfung (Klausur, mündlich) | Titel, Deadline, ggf. Gewichtung |
| `ASSIGNMENT` | Abgabe mit harter Deadline | Titel, Deadline |
| `GOAL` | Selbst gesetztes Lernziel | Titel, Zieldatum; ggf. Unteraufgaben |

### `TaskStatus`

Wird ausschließlich durch AF-03 aus `progressPercent` abgeleitet — niemals direkt vom Client gesetzt.

| Wert | Bedingung | Bedeutung |
|------|-----------|-----------|
| `OPEN` | `progressPercent = 0` | Noch nicht begonnen |
| `IN_PROGRESS` | `0 < progressPercent < 100` | Begonnen, nicht abgeschlossen |
| `DONE` | `progressPercent = 100` | Abgeschlossen |

Statusübergänge: `OPEN → IN_PROGRESS → DONE`. Technisch ist eine Rückwärtsänderung möglich (Fortschritt verringern), aber kein eigenständiger UC.

### `ReminderChannel`

| Wert | Bedeutung |
|------|-----------|
| `IN_APP` | Nur In-App-Benachrichtigung |
| `EMAIL` | Nur E-Mail (erfordert SMTP-Konfiguration) |
| `BOTH` | In-App und E-Mail |

### `UrgencyLevel`

Berechnetes, **nicht persistiertes** Attribut. Wird bei jedem Read serverseitig bestimmt (AF-02) und im `TaskDTO` mitgeliefert.

| Wert | Bedingung |
|------|-----------|
| `RED` | `deadline − heute ≤ 3 Tage` |
| `YELLOW` | `3 < deadline − heute ≤ 7 Tage` |
| `GREEN` | `deadline − heute > 7 Tage` |

---

## D2.3 Transfer-Objekte (DTOs)

DTOs sind die Datenstrukturen an der API-Grenze (S1). Sie sind keine Domänenobjekte und enthalten keine Geschäftslogik.

### `TaskDTO` (API-Antwort)

```typescript
interface TaskDTO {
  id: string;                // TaskId
  title: string;             // Title
  description: string | null;
  type: 'EXAM' | 'ASSIGNMENT' | 'GOAL';   // TaskType
  subject: string | null;
  deadline: string;          // Deadline (ISO 8601)
  estimatedHours: number;    // EstimatedHours
  actualHours: number;       // ActualHours
  progressPercent: number;   // ProgressPercent
  status: 'OPEN' | 'IN_PROGRESS' | 'DONE';  // TaskStatus
  weight: number;            // Weight
  urgency: 'RED' | 'YELLOW' | 'GREEN';  // UrgencyLevel — berechnet
  subtasks: SubtaskDTO[];
  createdAt: string;         // ISO 8601 Timestamp
  updatedAt: string;
}
```

### `CreateTaskRequest` (API-Eingabe)

```typescript
interface CreateTaskRequest {
  title: string;          // Pflicht
  type: TaskType;         // Pflicht
  deadline: string;       // Pflicht (ISO 8601)
  description?: string;
  subject?: string;
  estimatedHours?: number;  // Default: 0
  weight?: number;          // Default: 1
}
```

### `UpdateProgressRequest` (API-Eingabe)

```typescript
interface UpdateProgressRequest {
  progressPercent: number;        // Pflicht; 0–100
  sessionDurationMinutes?: number; // Optional; > 0
  notes?: string;                  // Optional
}
```

### `LearningPlanDTO` (API-Antwort)

```typescript
interface LearningPlanDTO {
  generatedAt: string;        // ISO 8601 Timestamp
  weekEntries: WeekEntryDTO[];
}

interface WeekEntryDTO {
  date: string;               // ISO 8601 Datum (Montag der Woche)
  tasks: PlanTaskDTO[];
}

interface PlanTaskDTO {
  taskId: string;
  title: string;
  recommendedMinutes: number;
  urgency: 'RED' | 'YELLOW' | 'GREEN';  // UrgencyLevel
}
```

---

## D2.4 SQL-Enum-Definitionen

```sql
CREATE TYPE task_type        AS ENUM ('EXAM', 'ASSIGNMENT', 'GOAL');
CREATE TYPE task_status      AS ENUM ('OPEN', 'IN_PROGRESS', 'DONE');
CREATE TYPE reminder_channel AS ENUM ('IN_APP', 'EMAIL', 'BOTH');
```

Im Java-Backend werden diese als `@Enumerated(EnumType.STRING)` persistiert (lesbar, migrationsrobust).
