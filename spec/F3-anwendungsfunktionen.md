# F3 — Anwendungsfunktionen

F3 listet die wesentlichen Systemfunktionen auf, die innerhalb der Anwendung ausgeführt werden und keine eigenen Use Cases in F2 darstellen.

Jede Funktion ist einer Funktionsgruppe zugeordnet und besitzt eine stabile ID. Die IDs werden in der Spezifikation zur eindeutigen Referenzierung verwendet.

---

## F3.1 Funktionsindex

| ID | Funktion | Gruppe | Prio | UC-Bezug |
|----|----------|--------|------|----------|
| AF-01 | Lernplan berechnen | Planung | Muss | UC-08 |
| AF-02 | Dringlichkeit berechnen (`UrgencyLevel`) | Planung | Muss | UC-07, UC-08 |
| AF-03 | `TaskStatus` aus Fortschritt ableiten | Tracking | Muss | UC-09 |
| AF-07 | Ownership prüfen | Auth | Muss | UC-05, UC-06, UC-09 |
| AF-12 | iCal-Datei generieren | Export | Kann | UC-11 |

**Prioritätslegende:**

- **Muss** — ohne diese Funktion ist die Anwendung nicht abgabefähig
- **Soll** — im Normalfall implementiert
- **Kann** — nach Kapazität

Die IDs AF-04, AF-05, AF-06, AF-08, AF-09, AF-10 und AF-11 werden im aktuellen Funktionsindex nicht verwendet. Die verbleibenden IDs bleiben stabil und werden nicht umbenannt.

---

## F3.2 Funktionsbeschreibungen

### AF-01 — Lernplan berechnen

**Zweck:**

Erzeugt aus den offenen Aufgaben eines Benutzers eine strukturierte Übersicht über empfohlene Lernaktivitäten.

**Auslöser:**

Die Berechnung wird über die Lernplanfunktion beziehungsweise den entsprechenden API-Aufruf angefordert.

**Verarbeitung:**

Die Berechnung berücksichtigt insbesondere:

- offene Aufgaben,
- Deadline,
- geschätzten Aufwand,
- aktuellen Fortschritt,
- Gewichtung einer Aufgabe,
- verbleibende Tage bis zur Deadline.

Für eine Aufgabe wird aus dem verbleibenden Aufwand und der Anzahl der verbleibenden Tage eine empfohlene tägliche Lernzeit berechnet.

Aufgaben werden nur für Tage geplant, an denen sie noch relevant sind. Bereits erledigte oder überfällige Aufgaben werden nicht als zukünftige Lernaktivitäten eingeplant.

Das Ergebnis wird als `LearningPlanDTO` an das Frontend übertragen.

**Einschränkungen:**

Abgeschlossene Aufgaben werden bei der Planung nicht als offene Lernaufgaben berücksichtigt.

**Querverweise:**

- D1 — Datenmodell
- D2 — `LearningPlanDTO`
- UC-08
- NFR-11-03

---

### AF-02 — Dringlichkeit berechnen (`UrgencyLevel`)

**Zweck:**

Ordnet einer Aufgabe anhand ihrer Deadline eine Dringlichkeitsstufe zu.

| Bedingung | `UrgencyLevel` |
|-----------|----------------|
| `deadline − heute ≤ 3 Tage` | `RED` |
| `3 < deadline − heute ≤ 7 Tage` | `YELLOW` |
| `deadline − heute > 7 Tage` | `GREEN` |

**Hinweis:**

`UrgencyLevel` ist ein berechneter, nicht dauerhaft persistierter Wert. Er wird serverseitig bestimmt und bei der Ausgabe einer Task bereitgestellt.

**Querverweise:**

- D2 — `UrgencyLevel`
- UC-07
- UC-08
- B1 — Dashboard und Aufgabendetail

---

### AF-03 — `TaskStatus` aus Fortschritt ableiten

**Zweck:**

Der Bearbeitungsstatus einer Task wird anhand des gespeicherten Fortschritts bestimmt.

| `progressPercent` | `TaskStatus` |
|-------------------|--------------|
| `0` | `OPEN` |
| `1–99` | `IN_PROGRESS` |
| `100` | `DONE` |

Der Status wird nicht als eigenständige Benutzereingabe behandelt.

**Hinweis:**

Wird der Fortschritt verändert, kann sich dadurch der Bearbeitungsstatus der Aufgabe ändern.

**Querverweise:**

- D2 — `TaskStatus`
- UC-09

---

### AF-07 — Ownership prüfen

**Zweck:**

Stellt sicher, dass ein Benutzer nur auf seine eigenen geschützten Ressourcen zugreifen kann.

**Implementierung:**

Die Benutzer-ID des authentifizierten Benutzers wird mit der Eigentümer-ID der angeforderten Task verglichen.

Bei einer Abweichung wird der Zugriff verweigert.

Die Prüfung erfolgt insbesondere bei:

- Lesen einzelner Tasks,
- Ändern von Tasks,
- Löschen von Tasks,
- Zugriffen auf aufgabenbezogene Ressourcen.

**Querverweise:**

- D1 — Ownership
- NFR-12-03
- UC-05
- UC-06
- UC-09

---

### AF-12 — iCal-Datei generieren

**Zweck:**

Erzeugt aus Aufgaben eine `.ics`-Datei für den Export in eine externe Kalenderanwendung.

**Verarbeitung:**

Die Aufgaben werden serverseitig in das iCalendar-Format serialisiert.

Die erzeugte Datei kann anschließend vom Benutzer heruntergeladen und manuell in eine Kalenderanwendung importiert werden.

**Inhalt:**

Für die exportierten Termine werden die relevanten Aufgabeninformationen verwendet, insbesondere:

- Deadline
- Aufgabentitel
- Aufgabenbeschreibung, sofern vorhanden

**Hinweis:**

Es findet keine direkte Synchronisation mit einem externen Kalenderdienst statt.

**Querverweise:**

- UC-11
- D1 — `TASK`
- B1 — Einstellungen / Kalenderexport

---

## F3.3 Funktionsübersicht

Die aktuell dokumentierten Anwendungsfunktionen decken die wesentlichen fachlichen Querschnittsfunktionen der Anwendung ab:

```text
Aufgabe / Fortschritt
        │
        ├── AF-03 → TaskStatus
        │
        ├── AF-02 → UrgencyLevel
        │
        ├── AF-07 → Ownership
        │
        └── AF-01 → Lernplan

Aufgaben
   │
   └── AF-12 → .ics-Export