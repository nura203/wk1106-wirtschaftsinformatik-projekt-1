# F3 — Anwendungsfunktionen

F3 listet alle Systemfunktionen — also die systeminternen Verarbeitungsschritte, die keine eigenen Use Cases in F2 sind, sowie die vollständige Funktionsinventur. Jede Funktion ist einer Funktionsgruppe zugeordnet und trägt eine stabile ID, die im Code referenziert wird.

---

## F3.1 Funktionsindex

| ID | Funktion | Gruppe | Prio | UC-Bezug |
|----|----------|--------|------|----------|
| AF-01 | Lernplan berechnen | Planung | Muss | UC-04, UC-05, UC-06, UC-09 |
| AF-02 | Dringlichkeit berechnen (UrgencyLevel) | Planung | Muss | UC-07 |
| AF-03 | TaskStatus aus Fortschritt ableiten | Tracking | Muss | UC-09 |
| AF-07 | Ownership prüfen | Auth | Muss | UC-05, UC-06, UC-09 |
| AF-10 | Reminder-Regeln auswerten (Scheduler) | Erinnerung | Soll | — |
| AF-11 | E-Mail-Benachrichtigung versenden | Erinnerung | Kann | — |
| AF-12 | iCal-Datei generieren | Export | Kann | UC-11 |

Prioritätslegende: **Muss** — ohne diese Funktion ist die Anwendung nicht abgabefähig. **Soll** — im Normalfall implementiert. **Kann** — nach Kapazität.
Hinweis: Die IDs AF-04, AF-05, AF-06, AF-08 und AF-09 wurden gestrichen. Die verbleibenden IDs bleiben stabil und werden nicht umbenannt.
---

## F3.2 Funktionsbeschreibungen

### AF-01 — Lernplan berechnen

**Zweck:** Erzeugt aus allen offenen Aufgaben eines Nutzers eine priorisierte Wochenübersicht mit empfohlenen täglichen Lernzeiten.

**Auslöser:** Aufgabe angelegt, bearbeitet, gelöscht oder Fortschritt aktualisiert; manueller Neuberechnungsaufruf (`POST /api/v1/plan/recalculate`).

**Algorithmus:**

```
für jede offene Aufgabe des Nutzers:
    verbleibende_tage  = deadline − heute  (mind. 1)
    offener_aufwand    = estimatedHours × (1 − progressPercent / 100)
    tagesaufwand       = offener_aufwand / verbleibende_tage
    priorität          = tagesaufwand × weight

Aufgaben sortiert nach priorität (absteigend)
Ausgabe: LearningPlanDTO mit WeekEntryDTO je Tag (Mo–So)
```

**Einschränkungen:** Aufgaben im Status DONE werden ignoriert. Aufgaben ohne `estimatedHours` erhalten `estimatedHours = 1` als Fallback. Aufgaben, deren Deadline heute oder in der Vergangenheit liegt, erhalten `verbleibende_tage = 1` (werden ganz oben priorisiert).

**Querverweise:** D2 `LearningPlanDTO`; UC-08; NFR-11-03.

---

### AF-02 — Dringlichkeit berechnen (UrgencyLevel)

**Zweck:** Ordnet jeder offenen Aufgabe eine Ampelfarbe zu.

| Bedingung | UrgencyLevel |
|-----------|-------------|
| `deadline − heute ≤ 3` | `RED` |
| `3 < deadline − heute ≤ 7` | `YELLOW` |
| `deadline − heute > 7` | `GREEN` |

**Hinweis:** UrgencyLevel ist ein berechnetes, nicht persistiertes Attribut. Es wird bei jedem Read serverseitig bestimmt und im `TaskDTO` mitgeliefert.

**Querverweise:** D2 `UrgencyLevel`; UC-07; B1 Ampeldarstellung.

---

### AF-03 — TaskStatus aus Fortschritt ableiten

**Zweck:** TaskStatus wird automatisch aus `progressPercent` abgeleitet; der Nutzer setzt ihn nicht direkt.

| progressPercent | TaskStatus |
|----------------|-----------|
| 0 | `OPEN` |
| 1–99 | `IN_PROGRESS` |
| 100 | `DONE` |

**Hinweis:** Statusübergänge sind unidirektional (OPEN → IN_PROGRESS → DONE). Ein Zurücksetzen auf OPEN durch Verringern des Fortschritts unter 1 % ist technisch möglich, aber nicht als eigenständiger UC modelliert.

**Querverweise:** D2 `TaskStatus`; UC-09.

---

### AF-07 — Ownership prüfen

**Zweck:** Sicherstellen, dass ein Nutzer nur auf eigene Ressourcen zugreift.

**Implementierung:** Service-Schicht vergleicht `userId` aus JWT mit `task.userId`; bei Abweichung: HTTP 403.

**Querverweise:** NFR-12-03; UC-05, UC-06, UC-09.

---

### AF-10 — Reminder-Regeln auswerten (Scheduler)

**Zweck:** Täglich um 08:00 Uhr alle aktiven Reminder prüfen; fällige Benachrichtigungen auslösen.

**Implementierung:** Spring `@Scheduled(cron = "0 0 8 * * *")`; prüft `reminder.daysBefore` gegen `task.deadline − heute`; löst AF-11 oder In-App-Benachrichtigung aus.

**Hinweis:** Kein Batch im Siedersleben-Sinne — B2 nicht anwendbar (→ E1).

**Querverweise:** D1 Entität REMINDER; UC-10.

---

### AF-11 — E-Mail-Benachrichtigung versenden

**Zweck:** Erinnerungs-E-Mail an den Nutzer versenden, wenn SMTP konfiguriert ist.

**Vorbedingung:** `SMTP_HOST` Umgebungsvariable gesetzt; `reminder.channel` = EMAIL oder BOTH.

**Querverweise:** S1 SMTP; N2 Konfigurationsmanagement; NFR-12-04.

---

### AF-12 — iCal-Datei generieren

**Zweck:** Alle offenen Aufgaben als RFC-5545-konforme `.ics`-Datei serialisieren.

**Inhalt je VEVENT:** `DTSTART` = `task.deadline`; `SUMMARY` = `task.title`; `DESCRIPTION` = `task.description`.

**Querverweise:** UC-11; S1 Abschnitt 3.2.
