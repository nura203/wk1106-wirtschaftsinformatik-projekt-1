# P1 — Ziele und Rahmenbedingungen

Grundlagenbaustein der Study-Planner-Spezifikation nach Siedersleben. Beantwortet: Warum wird das System gebaut, für wen, und welche Rahmenbedingungen begrenzen den Lösungsraum?

---

## P1.1 Mission

Der Study Planner ist eine webbasierte Einzelnutzer-Anwendung zur strukturierten Planung von Prüfungen, Lernzielen und akademischen Abgaben.

Der Studierende erfasst seine Aufgaben. Das System unterstützt bei der Priorisierung, stellt einen automatisch berechneten Lernplan bereit, hebt dringende Aufgaben visuell hervor und unterstützt die Konfiguration von Erinnerungen an bevorstehende Deadlines.

Der Study Planner soll das Problem unübersichtlicher Selbstorganisation reduzieren, indem Prüfungstermine, Abgaben und Lernziele zentral verwaltet und für die persönliche Lernplanung aufbereitet werden.

---

## P1.2 Projektziele

| ID | Ziel |
|----|------|
| G-01 | Prüfungen, Abgaben und Lernziele zentral erfassen und verwalten. |
| G-02 | Einen Lernplan auf Basis von Deadlines, Aufwandsschätzung, Fortschritt und weiteren Aufgabendaten berechnen. |
| G-03 | Dringende Aufgaben visuell hervorheben. |
| G-04 | Lernfortschritt erfassen und Lernsessions protokollieren. |
| G-05 | Erinnerungsoptionen für Aufgaben konfigurierbar machen. |
| G-06 | Kalenderexport der Aufgaben als `.ics`-Datei ermöglichen. |

---

## P1.3 Stakeholder und Nutzer

| Rolle | Beschreibung | Interaktion mit dem System |
|-------|-------------|---------------------------|
| **Studierender** | Primäre Nutzergruppe; plant und verfolgt den eigenen Lernfortschritt. | Erfasst Aufgaben, betrachtet den Lernplan und trägt Fortschritt sowie Lernsessions ein. |
| **Projektteam** | Entwickler und Dokumentationsautoren der Studienarbeit. | Entwickelt, dokumentiert, testet und präsentiert das System. |
| **Betreuender Dozent** | Bewertet die Abgabe gemäß den Modulanforderungen. | Prüft Dokumentation, Anwendung und Code. |

Kollaborative Funktionen wie Gruppen, geteilte Aufgaben oder gemeinsame Lernpläne sind außerhalb des aktuellen Scopes.

---

## P1.4 Scope

### Im Scope

- Browser-UI für Aufgabenverwaltung, Lernplan-Ansicht und Fortschrittserfassung.
- Automatische serverseitige Berechnung des Lernplans.
- JWT-basierte Authentifizierung mit Registrierung und Login.
- Verwaltung benutzerspezifischer Ressourcen.
- Erinnerungsoptionen einschließlich optionaler E-Mail-Konfiguration über SMTP.
- Kalenderexport als `.ics`-Datei.
- Containerisierter Betrieb über Docker Compose.

### Nicht im Scope

| ID | Nicht-Ziel | Begründung |
|----|-----------|-----------|
| NG-01 | Kollaborative Funktionen wie Gruppen, geteilte Aufgaben oder gemeinsame Lernpläne | Der aktuelle Anwendungsfall ist auf die persönliche Lernplanung ausgerichtet. |
| NG-02 | LMS-Funktionen wie Kursinhalte, Materialien oder Noten | Der Study Planner ist ein Planungstool und kein Lernmanagementsystem. |
| NG-03 | Kommunikation zwischen Studierenden | Kein Bestandteil des aktuellen Funktionsumfangs. |
| NG-04 | Prüfungsanmeldung oder Notenberechnung | Institutionelle Prozesse liegen außerhalb des Problemraums. |
| NG-05 | Native mobile Apps für iOS oder Android | Die mobile Nutzung wird über die responsive Webanwendung unterstützt. |
| NG-06 | Druckausgaben oder PDF-Export des Lernplans | Der aktuelle Funktionsumfang sieht keinen Druck- oder PDF-Export vor. |
| NG-07 | Migration von Altdaten | Der Study Planner wird ohne Vorgängersystem entwickelt. |
| NG-08 | Offline-Modus oder PWA-Funktionen | Der aktuelle Projektumfang setzt eine bestehende Netzwerkverbindung voraus. |

---

## P1.5 Rahmenbedingungen

Technologieentscheidungen werden in den Architekturentscheidungen dokumentiert. Hier werden nur Randbedingungen aufgeführt, die den Lösungsraum des Projekts einschränken.

| ID | Rahmenbedingung |
|----|----------------|
| CON-01 | Implementierungssprachen: Java 21 im Backend und TypeScript im Frontend. |
| CON-02 | Die Anwendung muss aus dem Projektstamm mit `docker compose up --build` gestartet werden können. |
| CON-03 | Das Projekt wird in einem öffentlichen GitHub-Repository entwickelt. |
| CON-04 | Commit-Messages sollen nach Conventional Commits strukturiert sein. |
| CON-05 | Die Dokumentation wird in Markdown erstellt; Diagramme werden als Mermaid beziehungsweise als Diagramm-Quelltext im Repository dokumentiert. |
| CON-06 | API-Keys, Passwörter und andere Secrets dürfen nicht in das Repository gelangen. |
| CON-07 | Passwörter werden ausschließlich als BCrypt-Hash gespeichert. |
| CON-08 | Personenbezogene Daten werden angemessen geschützt und nicht unnötig im Repository veröffentlicht. |

---

## P1.6 Erfolgskriterien

| ID | Kriterium |
|----|-----------|
| SC-01 | Die für den Projektumfang vorgesehenen Use Cases sind implementiert und im Code-Walkthrough nachvollziehbar. |
| SC-02 | `docker compose up --build` startet die Anwendung aus dem Projektstamm; Frontend und Backend sind über die dokumentierten Ports erreichbar. |
| SC-03 | Der Lernplan berücksichtigt unter anderem Deadline, Aufwand und Fortschritt bei der Planung. |
| SC-04 | Die Dringlichkeitsstufen entsprechen der definierten Berechnung: Rot bei höchstens 3 Tagen, Gelb bei mehr als 3 und höchstens 7 Tagen und Grün bei mehr als 7 Tagen. |
| SC-05 | Spezifikation, Architektur und Implementierung verwenden die definierten Begriffe und fachlichen Typen konsistent. |

---

## P1.7 Annahmen

| ID | Annahme |
|----|---------|
| AS-01 | Der Studierende verwendet einen aktuellen modernen Webbrowser. |
| AS-02 | Für den Betrieb von Docker Compose ist eine geeignete Docker-Umgebung verfügbar. Für den initialen Image-Pull wird Internetzugang benötigt. |
| AS-03 | SMTP-Zugangsdaten werden vom Betreiber selbst bereitgestellt. Ohne entsprechende Konfiguration wird kein E-Mail-Versand vorausgesetzt. |
| AS-04 | Die Anwendung wird im Rahmen des Hochschulprojekts und nicht unter hoher produktiver Last betrieben. |
| AS-05 | PostgreSQL-Daten werden über das konfigurierte Docker Volume persistent gespeichert. |

---

## P1.8 Risiken

| ID | Risiko | Eintrittswahrscheinlichkeit | Mitigation |
|----|--------|---------------------------|-----------|
| R-01 | Der Lernplan kann bei ungewöhnlichen oder stark konzentrierten Aufgabenverteilungen zu einer wenig geeigneten Planung führen. | Mittel | Berechnungslogik durch automatisierte Tests und definierte Testfälle überprüfen. |
| R-02 | Das JWT-Secret oder andere sensible Konfigurationswerte könnten versehentlich veröffentlicht werden. | Niedrig | `.env` wird nicht versioniert; `.env.example` enthält nur Platzhalter; Secrets werden über Umgebungsvariablen bereitgestellt. |
| R-03 | Eine fehlende SMTP-Konfiguration verhindert den E-Mail-Versand. | Mittel | SMTP ist optional; die Kernfunktionen der Anwendung sollen auch ohne SMTP-Konfiguration nutzbar bleiben. |
| R-04 | Zeitliche Einschränkungen können dazu führen, dass einzelne optionale Anforderungen nicht vollständig umgesetzt werden. | Mittel | Priorisierung der Anforderungen nach Projektumfang und Fokus auf die vorgesehenen Kernfunktionen. |
| R-05 | Größere Datenmengen können die Laufzeit der Lernplanberechnung erhöhen. | Niedrig | Lernplanberechnung durch automatisierte Tests überprüfen und bei Bedarf mit größeren Aufgabenzahlen testen. |