# E2 — Glossar

Begriffe, die in der Spezifikation, der Architektur oder im Code verwendet werden. Die Einträge sind alphabetisch sortiert. Fachliche IDs und Typnamen aus D2 werden einheitlich verwendet.

---

| Begriff | Definition | Synonym / ID |
|---------|-----------|--------------|
| **ActualHours** | Tatsächlich erfasster Arbeitsaufwand einer Task. | — |
| **Abgabe** | Aufgabe vom Typ `ASSIGNMENT`. | ASSIGNMENT |
| **Ampelfarbe** | Visuelle Darstellung der berechneten Dringlichkeitsstufe einer Aufgabe. | UrgencyLevel |
| **Aufgabe** | Oberbegriff für Prüfung, Abgabe oder Lernziel im System. | Task |
| **BCrypt** | Passwort-Hashing-Verfahren zur sicheren Speicherung von Passwörtern. Im aktuellen System wird ein Kostenfaktor von 12 verwendet. | — |
| **Conventional Commits** | Konvention für Commit-Nachrichten, beispielsweise `feat`, `fix`, `docs` oder `refactor`. | — |
| **Deadline** | Fälligkeitsdatum einer Aufgabe. Im Backend wird hierfür `LocalDate` verwendet; in der API wird das Datum als ISO-8601-Datum übertragen. | — |
| **DTO** | Data Transfer Object; Datenstruktur für den Austausch von Daten an der API-Grenze. | Data Transfer Object |
| **Flyway** | Werkzeug zur versionierten Verwaltung und Durchführung von Datenbankmigrationen. | — |
| **Fortschritt** | Prozentualer Bearbeitungsstand einer Task zwischen 0 und 100. | progressPercent |
| **Gewichtung** | Ganzzahlige Gewichtung einer Task im vorgesehenen Bereich von 1 bis 10. | weight |
| **iCal** | Standardformat für Kalenderdaten nach RFC 5545. Kalenderdaten werden als `.ics`-Datei exportiert. | iCalendar, `.ics` |
| **JWT** | JSON Web Token zur Authentifizierung. Im aktuellen System wird das Token mit HS256 signiert und besitzt eine Gültigkeit von 24 Stunden. | JSON Web Token |
| **LearningSession** | Protokollierte Lerneinheit zu einer Task mit Dauer, optionaler Notiz und Erfassungszeitpunkt. | — |
| **LearningPlanDTO** | API-Datenstruktur für die Darstellung des berechneten Lernplans. | — |
| **Lernplan** | Automatisch berechnete Übersicht über geplante Lernaktivitäten. | LearningPlanDTO |
| **Lernziel** | Aufgabe vom Typ `GOAL`. | GOAL |
| **MVP** | Minimum Viable Product; definierter Mindestumfang der Anwendung für die Abgabe. | — |
| **NFR** | Non-Functional Requirement beziehungsweise nichtfunktionale Anforderung. | — |
| **Ownership** | Zuordnung einer geschützten Ressource zu dem Benutzer, dem sie gehört. | — |
| **PostgreSQL** | Relationale Datenbank zur persistenten Speicherung der Anwendungsdaten. | — |
| **Prüfung** | Aufgabe vom Typ `EXAM`. | EXAM |
| **Reminder** | Konfigurierte Erinnerung zu einer Aufgabe. | — |
| **REST** | Architekturstil für Webschnittstellen, der im Study Planner für die Kommunikation zwischen Frontend und Backend verwendet wird. | — |
| **SMTP** | Simple Mail Transfer Protocol zur optionalen Übertragung von E-Mails. | — |
| **SPA** | Single Page Application; Webanwendung, deren Benutzeroberfläche clientseitig dynamisch aktualisiert wird. | Single Page Application |
| **TaskDTO** | API-Datenstruktur für die Darstellung einer Task einschließlich relevanter berechneter Informationen. | — |
| **TaskStatus** | Bearbeitungsstatus einer Task: `OPEN`, `IN_PROGRESS` oder `DONE`. | — |
| **TaskType** | Aufgabentyp: `EXAM`, `ASSIGNMENT` oder `GOAL`. | — |
| **UC** | Use Case beziehungsweise Anwendungsfall. | Anwendungsfall |
| **UrgencyLevel** | Berechnete Dringlichkeitsstufe einer Task: `RED`, `YELLOW` oder `GREEN`. | Ampelfarbe |
| **UUID** | Universally Unique Identifier zur eindeutigen Identifikation von Entitäten. | — |

---

## E2.1 Verwendung der Fachbegriffe

Die Begriffe aus diesem Glossar sollen in Spezifikation, Architektur, Backend und Frontend möglichst einheitlich verwendet werden.

Insbesondere betrifft dies die zentralen fachlichen Typen:

- `TaskType`
- `TaskStatus`
- `ReminderChannel`
- `UrgencyLevel`

sowie die zentralen Datenstrukturen:

- `TaskDTO`
- `CreateTaskRequest`
- `UpdateProgressRequest`
- `LearningPlanDTO`

Die Bezeichnungen sollen mit D1 und D2 sowie den entsprechenden Implementierungen konsistent bleiben.