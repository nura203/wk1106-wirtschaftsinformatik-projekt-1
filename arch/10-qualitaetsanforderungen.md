# 10 — Qualitätsanforderungen

Dieses Kapitel beschreibt die wesentlichen Qualitätsanforderungen des Study Planers.

Die Anforderungen orientieren sich an den in N1 definierten nichtfunktionalen Anforderungen. Sie werden nach den für das Projekt relevanten Qualitätsmerkmalen strukturiert.

Die Qualitätsanforderungen werden anhand messbarer Kriterien überprüfbar gemacht.

---

## 10.1 Qualitätsziele

Für den Study Planer stehen insbesondere folgende Qualitätsziele im Vordergrund:

| Qualitätsziel | Bedeutung |
|---------------|-----------|
| Performance | Die Anwendung soll auf typische Benutzeraktionen schnell reagieren. |
| Sicherheit | Benutzer- und Zugangsdaten sollen geschützt und Ressourcen voneinander getrennt werden. |
| Zuverlässigkeit | Fehler einzelner Funktionen sollen den Normalbetrieb nicht beeinträchtigen. |
| Wartbarkeit | Die Anwendung soll strukturiert, testbar und nachvollziehbar aufgebaut sein. |
| Benutzbarkeit | Die Anwendung soll auf Desktop und mobilen Geräten verständlich und nutzbar sein. |
| Portierbarkeit | Die Anwendung soll reproduzierbar über Docker Compose betrieben werden können. |

---

## 10.2 Performance und Effizienz

### NFR-11-01 — Dashboard-Ladezeit

Das Dashboard soll unter den in N1 definierten Bedingungen in weniger als 2 Sekunden vollständig geladen werden.

**Messung:**

- Browser-Netzwerkpanel
- LAN-Verbindung
- Messung vom ersten Byte bis zum vollständigen Rendern

---

### NFR-11-02 — REST-API-Antwortzeit

Standard-Requests der REST-API sollen innerhalb von weniger als 500 ms beantwortet werden.

**Messkriterium:**

- 95. Perzentil
- Messung beispielsweise über `curl`
- ohne Berücksichtigung externer API-Aufrufe

---

### NFR-11-03 — Lernplanberechnung

Die serverseitige Berechnung des Lernplans soll für bis zu 50 Aufgaben eines Benutzers weniger als 1 Sekunde benötigen.

Die Anforderung betrifft insbesondere die in AF-01 beschriebene Lernplanberechnung.

---

## 10.3 Sicherheit

### NFR-12-01 — Passwortschutz

Passwörter dürfen niemals im Klartext gespeichert werden.

Die Speicherung erfolgt ausschließlich als bcrypt-Hash mit Kostenfaktor 12.

---

### NFR-12-02 — Geschützte API-Endpunkte

Alle API-Endpunkte außer den Authentifizierungs-Endpunkten unter `/auth/*` benötigen einen gültigen JWT.

Die technische Absicherung erfolgt über Spring Security und den vorgesehenen JWT-Mechanismus.

---

### NFR-12-03 — Ownership

Ein Benutzer darf ausschließlich seine eigenen Ressourcen lesen und verändern.

Die Ownership-Prüfung erfolgt in der Service-Schicht.

Bei einem Zugriff auf eine fremde Ressource wird HTTP 403 zurückgegeben.

---

### NFR-12-04 — Schutz sensibler Konfiguration

API-Keys, Passwörter und Secrets dürfen nicht in das Repository gelangen.

Dazu wird unter anderem:

- `.env` in `.gitignore` aufgenommen
- `.env.example` als Vorlage ohne echte Secrets verwendet
- sensible Konfiguration über Umgebungsvariablen bereitgestellt

---

### NFR-12-05 — Keine Informationsweitergabe beim Login

Fehlgeschlagene Logins sollen nicht unterscheiden, ob die E-Mail-Adresse oder das Passwort falsch ist.

Dadurch soll verhindert werden, dass Informationen über vorhandene Benutzerkonten preisgegeben werden.

---

## 10.4 Zuverlässigkeit

### NFR-13-01 — Persistenz bei Neustart

Die Anwendung soll einen Neustart der Datenbank ohne Datenverlust überstehen.

Die PostgreSQL-Daten werden deshalb über ein Docker Volume persistent gespeichert.

---

### NFR-13-02 — Fehlerbehandlung

Fehlgeschlagene API-Anfragen dürfen nicht zu leeren Bildschirmen führen.

Bei Client- beziehungsweise Validierungsfehlern werden verständliche Fehlermeldungen angezeigt.

Bei Serverfehlern wird eine generische Fehlermeldung ohne interne technische Details angezeigt.

Die Umsetzung erfolgt über einen globalen Fehlerhandler im Backend und eine zentrale Fehlerbehandlung im Frontend.

---

### NFR-13-03 — Scheduler-Zuverlässigkeit

Fehler bei der Verarbeitung des Reminder-Schedulers dürfen den normalen Betrieb der Anwendung nicht blockieren.

Ausnahmen des Schedulers werden geloggt und behandelt, ohne die gesamte Anwendung zu beenden.

---

## 10.5 Wartbarkeit

### NFR-14-01 — Einheitliche Formatierung

Die Codebasis soll einheitlich formatiert und überprüfbar sein.

Für das Backend sind Checkstyle-Regeln vorgesehen.

Für das Frontend werden ESLint und Prettier verwendet.

---

### NFR-14-02 — Testabdeckung der Service-Schicht

Die Service-Schicht des Backends soll durch Unit-Tests abgedeckt werden.

Verwendet werden:

- JUnit 5
- Mockito

Als Ziel ist eine Line Coverage von mindestens 60 % für die Service-Klassen definiert.

---

### NFR-14-03 — Dokumentierte Architekturentscheidungen

Wesentliche Architekturentscheidungen sollen als ADRs dokumentiert werden.

Dafür ist ein eigener Bereich unter `docs/arch/` vorgesehen.

Die Architekturentscheidungen werden in Kapitel 9 dokumentiert.

---

### NFR-14-04 — Typenkonsistenz

Die in D2 definierten Typen sollen in Backend und Frontend mit identischer fachlicher Bedeutung verwendet werden.

Beispielsweise:

- `TaskType`
- `TaskStatus`
- `ReminderChannel`
- `UrgencyLevel`
- `TaskDTO`
- `CreateTaskRequest`
- `LearningPlanDTO`

Dadurch wird die Konsistenz zwischen Spezifikation, Architektur und Code unterstützt.

---

## 10.6 Benutzbarkeit

### NFR-15-01 — Responsive Nutzung

Die Anwendung soll sowohl auf Desktop-Geräten als auch auf mobilen Geräten nutzbar sein.

Die definierten Mindestbreiten sind:

- Desktop: 1024 px
- Mobilgerät: 375 px

Die Umsetzung erfolgt über Responsive Design mit CSS Flexbox und Grid.

---

### NFR-15-02 — Direkte Formularvalidierung

Fehleingaben sollen direkt am jeweiligen Formularfeld angezeigt werden.

Die Validierung soll möglichst vor dem Absenden des API-Requests erfolgen.

Dies betrifft insbesondere die Eingabe und Bearbeitung von Aufgaben.

---

### NFR-15-03 — Farbkontrast

Die Farbgebung der Anwendung soll die Anforderungen an WCAG AA erfüllen.

Für normale Texte wird ein Kontrastverhältnis von mindestens 4,5:1 vorgesehen.

Dies ist insbesondere für die Ampeldarstellung der Dringlichkeit relevant.

---

## 10.7 Portierbarkeit und Betrieb

### NFR-16-01 — Plattformunabhängiger Start

Die Anwendung soll über Docker Compose auf folgenden Plattformen gestartet werden können:

- Linux
- macOS
- Windows

Der vorgesehene Start erfolgt mit:

```bash
docker compose up --build
```

---

### NFR-16-02 — Minimale Konfiguration

Für den Start der Anwendung soll kein manueller Installationsschritt außer dem Anlegen der lokalen Konfigurationsdatei erforderlich sein.

Vorgesehen ist:

```bash
cp .env.example .env
```

Das Datenbankschema wird beim Start automatisch über Flyway angelegt.

---

## 10.8 Qualitätsziel-Matrix

Die wichtigsten Qualitätsanforderungen lassen sich den Architekturmaßnahmen wie folgt zuordnen:

| Qualitätsbereich | Anforderung | Wesentliche Architekturmaßnahme |
|------------------|-------------|----------------------------------|
| Performance | NFR-11-01 bis NFR-11-03 | effiziente REST- und Service-Verarbeitung |
| Sicherheit | NFR-12-01 bis NFR-12-05 | JWT, Spring Security, bcrypt, Ownership |
| Zuverlässigkeit | NFR-13-01 bis NFR-13-03 | Docker Volume, zentrale Fehlerbehandlung, Scheduler-Handling |
| Wartbarkeit | NFR-14-01 bis NFR-14-04 | Code-Standards, Tests, ADRs, Typenkonsistenz |
| Benutzbarkeit | NFR-15-01 bis NFR-15-03 | Responsive UI, Validierung, ausreichender Kontrast |
| Portierbarkeit | NFR-16-01 bis NFR-16-02 | Docker Compose, `.env.example`, Flyway |

---

## 10.9 Überprüfung

Die Qualitätsanforderungen werden während der Entwicklung durch unterschiedliche Verfahren überprüft.

Dazu gehören:

- manuelle Tests im Browser
- Messungen der REST-Antwortzeiten
- Messung der Dashboard-Ladezeit
- Unit-Tests
- Integrationstests
- Prüfung der Docker-Compose-Inbetriebnahme
- Prüfung der responsiven Darstellung
- Überprüfung der Farbkontraste
- Code- und Architektur-Review

Die konkrete Umsetzung und der aktuelle Erfüllungsgrad werden im weiteren Projektverlauf gegen den tatsächlichen Implementierungsstand geprüft.