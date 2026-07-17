# E2 — Glossar

Begriffe, die in der Spezifikation, der Architektur oder im Code verwendet werden. Einträge sind alphabetisch sortiert. IDs und Typnamen aus D2 sind **fett** hervorgehoben.

---

| Begriff | Definition | Synonym / ID |
|---------|-----------|-------------|
| **ActualHours** | Tatsächlich gelernte Stunden; Summe aller `LearningSession.duration_minutes / 60`; nur serverseitig gesetzt (AF-09). | — |
| **Aufgabe** | Oberbegriff für Prüfung, Abgabe oder Lernziel im System. | Task |
| **Abgabe** | Aufgabe vom Typ `ASSIGNMENT`; institutionelle oder selbst gesetzte Frist mit harter Deadline. | ASSIGNMENT |
| **Ampelfarbe** | Visuelles Dringlichkeitssignal: 🔴 RED (≤ 3 Tage), 🟡 YELLOW (≤ 7 Tage), 🟢 GREEN (> 7 Tage). Berechnet durch AF-02. | UrgencyLevel |
| **bcrypt** | Passwort-Hash-Algorithmus mit konfigurierbarem Kostenfaktor (hier: 12). Einzige erlaubte Methode zur Passwortspeicherung (NFR-12-01). | — |
| **Conventional Commits** | Commit-Message-Konvention (`feat`, `fix`, `docs`, `refactor` …); verbindlich für dieses Projekt (CON-04). | — |
| **Deadline** | Fälligkeitsdatum einer Aufgabe; Typ `LocalDate` im Backend, ISO 8601 `YYYY-MM-DD` in der API. | — |
| **DTO** | Data Transfer Object; Datenstruktur an der API-Grenze; enthält keine Geschäftslogik. Definiert in D2.3. | Data Transfer Object |
| **Flyway** | Schema-Migrations-Tool; führt SQL-Skripte unter `db/migration/` beim Container-Start automatisch aus. | — |
| **Fortschritt** | Prozentwert 0–100 (`progressPercent`); steuert `TaskStatus` via AF-03; einziger Weg zum Statuswechsel. | progressPercent |
| **Gewichtung** | Ganzzahl 1–10 (`weight`); beeinflusst Priorisierungsalgorithmus in AF-01. | weight |
| **iCal** | Dateiformat für Kalenderdaten (RFC 5545); Dateiendung `.ics`; erzeugt durch AF-12 (UC-11). | iCalendar, .ics |
| **JWT** | JSON Web Token; HS256-signiert; Ablaufzeit 24 h; einziger Authentifizierungsmechanismus (AF-05, AF-06). | JSON Web Token |
| **LearningSession** | Protokolleintrag einer Lernsession: Dauer, Notiz, Zeitpunkt; persistiert durch AF-09. | — |
| **LearningPlanDTO** | API-Antwort für den Lernplan; enthält `WeekEntryDTO[]` mit `PlanTaskDTO[]` je Tag (D2.3). | — |
| **Lernplan** | Automatisch berechnete Wochenübersicht (AF-01); nicht persistiert — bei jeder Anfrage neu berechnet. | LearningPlanDTO |
| **Lernziel** | Aufgabe vom Typ `GOAL`; selbst gesetztes Ziel ohne institutionelle Deadline. | GOAL |
| **MVP** | Minimum Viable Product; Funktionsumfang, der für die Abgabe M3 implementiert wird (alle Muss-Anforderungen aus F3). | — |
| **NFR** | Nichtfunktionale Anforderung; stabile ID-Form `NFR-xx-yy`; definiert in N1. | — |
| **Ownership** | Eigentumsbeziehung zwischen USER und TASK; geprüft durch AF-07; Verletzung → HTTP 403. | — |
| **PostgreSQL** | Relationale Datenbank; einzige Persistenzschicht; läuft als Docker-Container. | — |
| **Prüfung** | Aufgabe vom Typ `EXAM`; institutionell vorgegebene Klausur oder mündliche Prüfung. | EXAM |
| **Reminder** | Erinnerungsregel je Aufgabe; konfiguriert X Tage vor Deadline eine Benachrichtigung (UC-10, AF-10). | — |
| **REST** | Representational State Transfer; Architekturstil der HTTP-Schnittstelle zwischen Frontend und Backend (S1). | — |
| **SMTP** | Simple Mail Transfer Protocol; für optionalen E-Mail-Versand (AF-11, S1.4); deaktiviert wenn `SMTP_HOST` leer. | — |
| **SPA** | Single Page Application; clientseitiges Routing ohne Seitenreload; technische Grundlage des Frontends. | Single Page Application |
| **TaskDTO** | API-Antwort für eine einzelne Aufgabe; enthält berechnetes `urgency`-Feld (D2.3). | — |
| **TaskStatus** | Enum: `OPEN`, `IN_PROGRESS`, `DONE`; abgeleitet aus `progressPercent` durch AF-03; niemals direkt gesetzt. | — |
| **TaskType** | Enum: `EXAM`, `ASSIGNMENT`, `GOAL`; steuert Pflichtfelder im Formular und Darstellung (D2.2). | — |
| **UC** | Use Case; Anwendungsfall aus F2; stabile ID-Form `UC-xx`. | Anwendungsfall |
| **UrgencyLevel** | Berechnetes Dringlichkeitslevel: `RED`, `YELLOW`, `GREEN`; definiert in D2.2; berechnet durch AF-02. | Ampelfarbe |
| **UUID** | Universally Unique Identifier v4; primärer Schlüsseltyp für alle Entitäten (D1). | — |
