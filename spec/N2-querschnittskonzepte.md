# N2 — Querschnittskonzepte

Querschnittskonzepte sind technische oder organisatorische Entscheidungen, die mehrere Bausteine gleichzeitig betreffen. Sie werden hier einmal definiert und von F2, S1 und N1 referenziert — statt in jedem Baustein wiederholt zu werden.

---

## N2.1 Authentifizierung und Session

**Konzept:** JWT-basierte, stateless Authentifizierung (AF-05, AF-06).

- Beim Login stellt das Backend ein signiertes JWT (HS256) aus; Ablaufzeit 24 h.
- Das Frontend speichert das Token im `localStorage` (MVP-Default) oder als `httpOnly`-Cookie (erhöht Sicherheit gegen XSS; empfohlen für Produktivbetrieb).
- Jeder API-Request trägt `Authorization: Bearer <token>`.
- Spring Security prüft jeden eingehenden Request durch `JwtAuthenticationFilter`.
- Die Service-Schicht vergleicht `userId` aus dem JWT mit dem Datenbankbesitzer der angeforderten Ressource (Ownership-Prüfung, AF-07).

**Querverweise:** NFR-12-01 bis NFR-12-05; UC-01, UC-02, UC-03.

---

## N2.2 Fehlerbehandlung

**Backend:** Ein globaler `@ControllerAdvice` fängt alle unbehandelten Exceptions und gibt einheitliche JSON-Fehlerantworten zurück:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Titel darf nicht leer sein",
  "timestamp": "2025-01-21T10:00:00Z"
}
```

HTTP-Statuscodes: 400 Validierungsfehler · 401 kein/ungültiger JWT · 403 fremde Ressource · 404 nicht gefunden · 500 interner Fehler.

**Frontend:** Axios-Interceptor fängt 401-Antworten ab und leitet auf `/login` um. Alle übrigen API-Fehler werden über einen zentralen Error-State als Toast oder Inline-Meldung angezeigt.

**Querverweise:** NFR-13-02; S1 Abschnitt 2.5.

---

## N2.3 Logging

- Backend: SLF4J + Logback (Spring-Default); Log-Level `INFO` im Normalbetrieb, `DEBUG` per Spring-Profil aktivierbar.
- Keine personenbezogenen Daten in Logs: keine Passwörter, keine vollständigen E-Mail-Adressen in Stack Traces.
- Fehler im Scheduler (AF-10) werden auf Level `ERROR` geloggt; kein Absturz (NFR-13-03).

**Querverweise:** NFR-12-04.

---

## N2.4 Datenbankzugriff

- Spring Data JPA mit Hibernate als ORM; Repositories als `JpaRepository`-Interfaces.
- Native SQL-Queries nur bei Komplexität, die JPA nicht abdeckt.
- Transaktionen: `@Transactional` auf Service-Methoden, die mehrere DB-Operationen kombinieren.
- Schema-Migration: Flyway; SQL-Skripte unter `backend/src/main/resources/db/migration/V*.sql`.
- ENUM-Typen aus D2 werden als `@Enumerated(EnumType.STRING)` gespeichert (lesbar, migrationsrobust).

**Querverweise:** D1; D2; NFR-16-02.

---

## N2.5 Konfigurationsmanagement

- Alle umgebungsabhängigen Werte (Datenbankpasswort, JWT-Secret, SMTP) über Umgebungsvariablen.
- `application.properties` enthält nur nicht-sensitive Defaults.
- `.env.example` als Template im Repo; `.env` in `.gitignore`.
- Docker Compose liest `.env` automatisch ein.

- **API-URL-Präfix:** Der Präfix `/api/v1` wird global in `application.properties` über `spring.mvc.servlet.path=/api/v1` verankert. Dadurch gilt er automatisch für alle Endpunkte aus S1.2.

**Querverweise:** CON-06; NFR-12-04; NFR-16-02; S3.

---

## N2.6 Typen-Konsistenz (Code ↔ Spec)

Typen aus D2 sind im Backend und Frontend identisch benannt. Die Abbildung ist verbindlich:

| D2-Typ | Java-Klasse/-Enum | TypeScript-Interface/-Enum |
|--------|-------------------|---------------------------|
| `TaskType` | `TaskType` (Enum) | `TaskType` |
| `TaskStatus` | `TaskStatus` (Enum) | `TaskStatus` |
| `ReminderChannel` | `ReminderChannel` (Enum) | `ReminderChannel` |
| `TaskDTO` | `TaskDTO` (Record/Class) | `TaskDTO` |
| `CreateTaskRequest` | `CreateTaskRequest` | `CreateTaskRequest` |
| `LearningPlanDTO` | `LearningPlanDTO` | `LearningPlanDTO` |
| `UrgencyLevel` | `UrgencyLevel` (Enum) | `UrgencyLevel` |

> **Hinweis zu `UrgencyLevel`:** Dieser Wert wird **nicht in der Datenbank gespeichert**. Er wird bei jedem Lesen einer Aufgabe serverseitig aus `task.deadline` und dem aktuellen Datum berechnet (→ AF-02) und als Feld im `TaskDTO` mitgeliefert. Es gibt keine entsprechende Datenbankspalte.

Im Code-Walkthrough wird stichprobenartig geprüft: „Zeigen Sie mir `TaskStatus` im Code."

**Querverweise:** NFR-14-04; D2.

---

## N2.7 Testkonzept

| Schicht | Testart | Werkzeug | Ziel |
|---------|---------|---------|------|
| Service-Schicht Backend | Unit-Tests | JUnit 5 + Mockito | ≥ 60 % Line Coverage |
| Controller-Schicht Backend | Integrationstests | Spring `MockMvc` | Alle HTTP-Endpunkte aus S1 |
| Frontend-Komponenten | Komponenten-Tests (optional) | Vitest + Testing Library | Kritische UI-Logik |
| Use Cases UC-01 bis UC-11 | Manueller Test | Browser | Alle Akzeptanzkriterien aus F2 |

**Querverweise:** NFR-14-02.
