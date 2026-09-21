# 3 — Kontextabgrenzung

Dieses Kapitel beschreibt die Grenze des Study Planners zu seiner Umgebung.

Das System wird als Blackbox betrachtet. Interne Komponenten wie Frontend, Backend und Datenbank werden erst in den nachfolgenden Architekturkapiteln detailliert beschrieben.

---

## 3.1 Fachlicher Kontext

Der Study Planner ist eine webbasierte Einzelnutzer-Anwendung zur Planung von Prüfungen, Abgaben und Lernzielen.

```mermaid
graph TD
    Nutzer["👤 Studierender"]
    SP["Study Planner"]
    SMTP["📧 E-Mail-Provider"]
    Cal["📅 Kalender-Anwendung"]

    Nutzer -->|"Aufgaben erfassen, Lernplan ansehen, Fortschritt eintragen"| SP
    SP -->|"Erinnerungs-E-Mails versenden"| SMTP
    SMTP -->|"E-Mail-Benachrichtigung"| Nutzer
    SP -->|".ics-Datei exportieren"| Nutzer
    Nutzer -->|"Manueller Import"| Cal