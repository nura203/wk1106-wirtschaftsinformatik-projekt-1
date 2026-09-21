# F1 — Geschäftsprozesse

F1 beschreibt die zentralen Abläufe aus Nutzerperspektive — unabhängig von der technischen Realisierung. Die Bausteine F2 und F3 verfeinern den systemunterstützten Teil dieser Prozesse.

---

## F1.1 Prozessübersicht

| ID | Prozess | Auslöser | Ergebnis |
|----|---------|---------|----------|
| GP-01 | Semester einrichten | Beginn eines neuen Semesters | Relevante Aufgaben erfasst; Lernplan ist einsehbar |
| GP-02 | Laufende Planung und Verfolgung | Regelmäßige Nutzung während des Semesters | Fortschritt aktuell; Lernplan berücksichtigt den aktuellen Stand |
| GP-03 | Prüfungsphase | Verdichtete Lernphase vor Prüfungen | Tagesbezogene Planung; Lernzeit und Fortschritt dokumentiert |

---

## F1.2 GP-01 — Semester einrichten

**Ziel:**  
Der Studierende legt alle relevanten Prüfungen, Abgaben und Lernziele für das neue Semester an und erhält einen ersten Lernplan.

**Akteur:**  
Studierender.

**Vorbedingung:**  
Ein Nutzerkonto existiert oder wird im Rahmen des Prozesses angelegt.

**Ablauf:**

| Schritt | Aktivität | Systemunterstützung |
|---------|-----------|---------------------|
| A1 | Registrieren oder Anmelden | UC-01 / UC-02 |
| A2 | Prüfungen anlegen (Titel, Datum, Fach, geschätzter Aufwand) | UC-04 |
| A3 | Abgaben anlegen (Titel, Deadline, Fach) | UC-04 |
| A4 | Lernziele anlegen (Thema, Zieldatum, Unteraufgaben) | UC-04 |
| A5 | Lernplan betrachten | UC-08 |
| A6 | Optional: Erinnerungen konfigurieren | UC-10 |

**Nachbedingung:**  
Die relevanten Aufgaben sind erfasst und der automatisch berechnete Lernplan ist einsehbar.

```mermaid
flowchart TD
    A1[Registrieren / Anmelden] --> A2[Prüfungen anlegen]
    A2 --> A3[Abgaben anlegen]
    A3 --> A4[Lernziele anlegen]
    A4 --> A5[Lernplan betrachten]
    A5 --> A6{Erinnerungen gewünscht?}
    A6 -->|Ja| A7[Erinnerungen konfigurieren]
    A6 -->|Nein| A8([Ende])
    A7 --> A8