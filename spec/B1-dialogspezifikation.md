# B1 — Dialogspezifikation

B1 beschreibt die Benutzerschnittstelle des Study Planners: Navigationsstruktur, Screens und ihre wesentlichen Elemente.

B1 konkretisiert die Use Cases aus F2 auf Ebene der Benutzeroberfläche. Jeder Use Case wird mindestens einem Screen zugeordnet.

---

## B1.1 Navigationsstruktur

```mermaid
graph TD

    Login["/login — Anmelden"]
    Register["/register — Registrieren"]

    Dashboard["/ — Dashboard"]
    Tasks["/tasks — Aufgabenliste"]
    TaskNew["/tasks/new — Aufgabe anlegen"]
    TaskDetail["/tasks/:id — Aufgabendetail"]
    TaskEdit["/tasks/:id/edit — Aufgabe bearbeiten"]
    Plan["/plan — Lernplan"]
    Settings["/settings — Einstellungen"]

    Login -->|UC-02 Erfolg| Dashboard
    Register -->|UC-01 Erfolg| Dashboard

    Dashboard -->|Aufgaben anzeigen| Tasks
    Dashboard -->|Lernplan anzeigen| Plan
    Dashboard -->|Neue Aufgabe| TaskNew
    Dashboard -->|Einstellungen| Settings

    Tasks -->|Detail anzeigen| TaskDetail
    Tasks -->|Neue Aufgabe| TaskNew

    TaskDetail -->|Bearbeiten| TaskEdit
    TaskEdit -->|Speichern / Abbrechen| TaskDetail

    Plan -->|Wochenansicht wechseln| Plan

    Settings -->|Passwort ändern| Settings
    Settings -->|Abmelden| Login