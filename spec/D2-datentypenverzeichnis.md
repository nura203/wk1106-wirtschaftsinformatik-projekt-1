# D2 — Datentypenverzeichnis

D2 ist das verbindliche Typenverzeichnis des Study Planers. Alle hier definierten Namen werden in D1 (Datenmodell), S1 (API), im Java-Backend und im TypeScript-Frontend identisch verwendet. Abweichungen im Code sind Fehler, keine Stilfrage (vgl. N2.6).

---

## D2.1 Enum-Typen

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

## D2.2 Transfer-Objekte (DTOs)

Ein DTO (Data Transfer Object) ist ein Datenpaket das zwischen Frontend und Backend ausgetauscht wird. Es enthält nur Daten — keine Logik. Was das Frontend sendet und was das Backend antwortet ist hier verbindlich festgelegt.

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
