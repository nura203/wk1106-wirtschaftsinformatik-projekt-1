# P1 — Ziele und Rahmenbedingungen

Grundlagenbaustein der Study-Planer-Spezifikation nach Siedersleben. Beantwortet: Warum wird das System gebaut, für wen, und welche Rahmenbedingungen begrenzen den Lösungsraum?

---

## P1.1 Mission

Der Study Planer ist eine webbasierte Einzelnutzer-Anwendung zur strukturierten Planung von Prüfungen, Lernzielen und akademischen Abgaben. Der Studierende erfasst seine Aufgaben einmalig; das System berechnet daraus automatisch einen priorisierten Lernplan, hebt dringende Aufgaben visuell hervor und erinnert ihn rechtzeitig an bevorstehende Deadlines.

Der Study Planer löst das Problem unübersichtlicher Selbstorganisation: Studierende verwalten Prüfungstermine, Abgaben und Lernziele aus verschiedenen Quellen — der Study Planer zentralisiert diese Information und reduziert den kognitiven Aufwand der Priorisierung.

---

## P1.2 Projektziele

| ID | Ziel |
|----|------|
| G-01 | Prüfungen, Abgaben und Lernziele zentral erfassen und verwalten. |
| G-02 | Automatisch einen priorisierten Lernplan auf Basis von Deadlines, Aufwandsschätzung und Fortschritt berechnen. |
| G-03 | Dringende Aufgaben visuell hervorheben (Ampelsystem: Rot / Gelb / Grün). |
| G-04 | Lernfortschritt tracken und Lernsessions protokollieren. |
| G-05 | Optionale Erinnerungen (in-app, E-Mail) konfigurierbar machen. |
| G-06 | Kalenderexport der offenen Aufgaben als `.ics`-Datei ermöglichen. |

---

## P1.3 Stakeholder und Nutzer

| Rolle | Beschreibung | Interaktion mit dem System |
|-------|-------------|---------------------------|
| **Studierender** | Primäre Nutzergruppe; plant und verfolgt seinen Lernfortschritt. | Erfasst Aufgaben, liest Lernplan, trägt Fortschritt ein. |
| **Projektteam** | Entwickler und Dokumentationsautoren dieser Studienarbeit. | Baut, dokumentiert und präsentiert das System. |
| **Betreuender Dozent** | Bewertet die Abgabe gemäß Modulanforderungen. | Liest Dokumentation, führt Code-Walkthrough durch. |

Mehrnutzer-Unterstützung (z. B. Gruppen, geteilte Pläne) ist explizit außerhalb des Scopes (→ NG-01).

---

## P1.4 Scope

### Im Scope

- Browser-UI für Aufgabenverwaltung, Lernplan-Ansicht und Fortschrittstracking.
- Automatische Lernplan-Berechnung serverseitig (Priorisierungsalgorithmus).
- JWT-basierte Authentifizierung (Registrierung, Login, Logout).
- Optionale E-Mail-Erinnerungen via SMTP (konfigurierbar, nicht Pflicht).
- Kalenderexport als `.ics`-Datei.
- Containerisiertes Deployment via Docker Compose.

### Nicht im Scope

| ID | Nicht-Ziel | Begründung |
|----|-----------|-----------|
| NG-01 | Mehrnutzer-Konten, Rollen, geteilte Pläne | Persönliches Planungswerkzeug für Einzelnutzer. |
| NG-02 | LMS-Funktionen (Kursinhalte, Materialien, Noten) | Kein Ersatz für Moodle o.ä.; reines Planungstool. |
| NG-03 | Kommunikation zwischen Studierenden | Kein soziales Netzwerk. |
| NG-04 | Prüfungsanmeldung oder Notenberechnung | Institutionell geregelt; außerhalb des Problemraums. |
| NG-05 | Native mobile Apps (iOS, Android) | Responsive Web-UI deckt mobile Nutzung ab. |
| NG-06 | Druckausgaben / PDF-Export des Lernplans | Kein identifizierter Bedarf; `.ics`-Export reicht. |
| NG-07 | Migration von Altdaten | Neuentwicklung ohne Vorgängersystem. |
| NG-08 | Offline-Modus / PWA | Konnektivität wird vorausgesetzt; Komplexität nicht gerechtfertigt. |

---

## P1.5 Rahmenbedingungen

Technologieentscheidungen (Sprache, Framework, Datenbankprodukt, Build-Toolchain) sind *Designentscheidungen* und werden als ADRs in `docs/arch/` dokumentiert. Hier stehen nur die Randbedingungen, die den Lösungsraum von außen einschränken.

| ID | Rahmenbedingung |
|----|----------------|
| CON-01 | Implementierungssprachen: Java 21 (Backend), TypeScript 5 (Frontend) — vorgegeben durch Teamkompetenz und Modulkontext. |
| CON-02 | Abgabe als lauffähige Anwendung mit `docker compose up` — keine manuelle Installationsschritte außer `cp .env.example .env`. |
| CON-03 | Repository öffentlich auf GitHub; Betreuer (carstenlucke) muss ab Projektbeginn Lesezugriff haben. |
| CON-04 | Alle Commit-Messages folgen Conventional Commits. |
| CON-05 | Dokumentation in Markdown (kein PDF); Diagramme als Mermaid oder PlantUML mit Quelltext im Repo. |
| CON-06 | Keine API-Keys, Passwörter oder `.env`-Dateien mit Geheimnissen im Repository. |
| CON-07 | Passwörter werden ausschließlich als bcrypt-Hash (Kostenfaktor 12) gespeichert — niemals im Klartext. |
| CON-08 | DSGVO-konforme Handhabung von Nutzerdaten; keine Matrikelnummern oder private Kontaktdaten im Repo. |

---

## P1.6 Erfolgskriterien

| ID | Kriterium |
|----|-----------|
| SC-01 | Alle in F2 spezifizierten Use Cases sind vollständig implementiert und im Code-Walkthrough erklärbar. |
| SC-02 | `docker compose up --build` startet die Anwendung ohne manuelle Eingriffe; Frontend unter Port 5173, Backend unter Port 8080 erreichbar. |
| SC-03 | Der Lernplan priorisiert Aufgaben korrekt: Aufgabe mit Deadline morgen erscheint vor Aufgabe mit Deadline in 14 Tagen. |
| SC-04 | Ampelfarben entsprechen der Spezifikation: Rot = deadline − heute ≤ 3 Tage, Gelb = 3 < deadline − heute ≤ 7 Tage, Grün = deadline − heute > 7 Tage. Grenzwerte sind überschneidungsfrei; exakt 7 Tage ist Gelb, exakt 3 Tage ist Rot. |
| SC-05 | Alle drei Artefakte (Spec, Architektur, Code) sind konsistent: UC-Bezeichner aus F2 sind in der Architektur und im Code wiederzufinden. |

---

## P1.7 Annahmen

| ID | Annahme |
|----|---------|
| AS-01 | Der Studierende nutzt einen modernen Browser (Chrome, Firefox, Safari, Edge — jeweils aktuelle Version). |
| AS-02 | Für den Betrieb ist Docker mit Internetzugang verfügbar (für den initialen Image-Pull). |
| AS-03 | SMTP-Zugangsdaten werden vom Betreiber selbst bereitgestellt; das System versendet keine E-Mails ohne explizite Konfiguration. |
| AS-04 | Das System wird nicht unter hoher Last betrieben (< 100 gleichzeitige Nutzer; Hochschulprojekt). |
| AS-05 | Datenverlust durch Neustart des Containers ist akzeptabel, sofern das PostgreSQL-Volume korrekt eingebunden ist. |

---

## P1.8 Risiken

| ID | Risiko | Eintrittswahrscheinlichkeit | Mitigation |
|----|--------|---------------------------|-----------|
| R-01 | Lernplan-Algorithmus liefert bei ungünstigen Eingaben (z. B. alle Deadlines am selben Tag) unbrauchbare Ergebnisse. | Mittel | Grenzfälle in Unit-Tests abdecken; Fallback: Aufgaben alphabetisch sortiert anzeigen. |
| R-02 | JWT-Secret wird versehentlich ins Repository committed. | Niedrig | `.env` in `.gitignore`; Pre-Commit-Hook empfohlen; Secret in `.env.example` als Platzhalter. |
| R-03 | SMTP-Konfiguration fehlt → E-Mail-Erinnerungen schweigen still. | Mittel | Fehlende SMTP-Konfiguration deaktiviert E-Mail-Kanal explizit mit Log-Warnung; kein Absturz. |
| R-04 | Zeitdruck führt zu unvollständiger Implementierung von Soll- und Kann-Anforderungen. | Mittel | Klare Priorisierung in F3 (Muss / Soll / Kann); MVP ist mit Muss-Funktionen bestanden. |
| R-05 | Performance-Probleme beim Lernplan-Algorithmus bei >50 Aufgaben. | Niedrig | Algorithmus mit O(n log n) Komplexität designen; Lasttest mit 100 Aufgaben. |
