# 6 — Laufzeitsicht

Die Laufzeitsicht beschreibt das Verhalten der wesentlichen Architekturbausteine
zur Laufzeit. Dazu werden ausgewählte Abläufe aus den in der Spezifikation
beschriebenen Use Cases betrachtet.

Die dargestellten Abläufe zeigen insbesondere die Kommunikation zwischen
Frontend, REST-API, Security, Service-Schicht und Persistenz.

---

## 6.1 Anmeldung — UC-02

Bei der Anmeldung übermittelt der Studierende seine E-Mail-Adresse und sein
Passwort an das Backend.

Das Backend prüft die Zugangsdaten und stellt bei erfolgreicher
Authentifizierung ein JWT aus.

```mermaid
sequenceDiagram
    actor Nutzer as Studierender
    participant Frontend as React SPA
    participant Controller as Auth Controller
    participant Service as Auth Service
    participant Security as JWT/Security
    participant UserRepo as User Repository
    participant DB as PostgreSQL

    Nutzer->>Frontend: E-Mail + Passwort eingeben
    Frontend->>Controller: POST /api/v1/auth/login
    Controller->>Service: Login-Daten übergeben
    Service->>UserRepo: Benutzer anhand E-Mail suchen
    UserRepo-->>DB: Benutzer anhand E-Mail abfragen
    DB-->>UserRepo: Benutzer + Passwort-Hash
    UserRepo-->>Service: Benutzer + Passwort-Hash
    Service->>Security: Passwort prüfen
alt Zugangsdaten gültig
    Security-->>Service: Prüfung erfolgreich
    Service->>Security: JWT erzeugen
    Security-->>Service: JWT
    Service-->>Controller: Authentifizierungsdaten
    Controller-->>Frontend: JWT + Userdaten
    Frontend-->>Nutzer: Dashboard anzeigen
else Zugangsdaten ungültig
    Security-->>Service: Prüfung fehlgeschlagen
    Service-->>Controller: Authentifizierung fehlgeschlagen
    Controller-->>Frontend: Allgemeine Fehlermeldung
    Frontend-->>Nutzer: Anmeldung fehlgeschlagen
end
```

Bei ungültigen Zugangsdaten wird keine Information darüber preisgegeben, ob
die E-Mail-Adresse oder das Passwort falsch war. Dies entspricht NFR-12-05.

---

## 6.2 Aufgabe anlegen — UC-04

Beim Anlegen einer Aufgabe sendet das Frontend eine
`CreateTaskRequest` an den geschützten REST-Endpunkt.

Das Backend authentifiziert den Benutzer und übergibt die Anfrage an die
fachliche Aufgabenverwaltung.

```mermaid
sequenceDiagram
    actor Nutzer as Studierender
    participant Frontend as React SPA
    participant Security as JWT/Security
    participant Controller as Task Controller
    participant Service as Task Service
    participant Repo as Task Repository
    participant DB as PostgreSQL

    Nutzer->>Frontend: Aufgabendaten eingeben
    Frontend->>Controller: POST /api/v1/tasks
    Controller->>Security: JWT prüfen
    alt JWT gültig
    Security-->>Controller: Benutzer authentifiziert
    Controller->>Service: CreateTaskRequest
    Service->>Service: Eingaben validieren
    Service->>Service: TaskStatus aus Fortschritt ableiten
    Service->>Repo: Aufgabe mit Eigentümer-ID speichern
    Repo->>DB: Aufgabe mit Eigentümer-ID speichern
    DB-->>Repo: Gespeicherte Aufgabe
    Repo-->>Service: TASK
    Service->>Service: UrgencyLevel berechnen
    Service-->>Controller: TaskDTO
    Controller-->>Frontend: HTTP 201 + TaskDTO
    Frontend-->>Nutzer: Neue Aufgabe anzeigen
else JWT ungültig
    Security-->>Controller: Authentifizierung fehlgeschlagen
    Controller-->>Frontend: HTTP 401
    Frontend-->>Nutzer: Anmeldung erforderlich
end
```

Die Ownership-Prüfung ist beim Anlegen einer Aufgabe insbesondere dadurch
gewährleistet, dass die Eigentümer-ID aus der authentifizierten Identität
verwendet wird.

---

## 6.3 Lernplan berechnen — UC-08

Der Lernplan wird serverseitig aus den offenen Aufgaben des authentifizierten
Benutzers berechnet.

Die Berechnung berücksichtigt Deadline, offenen Aufwand, Fortschritt und
Prioritätsgewichtung.

```mermaid
sequenceDiagram
    actor Nutzer as Studierender
    participant Frontend as React SPA
    participant Security as JWT/Security
    participant Controller as Learning Plan Controller
    participant Service as Learning Plan Service
    participant Repo as Task Repository
    participant DB as PostgreSQL

    Nutzer->>Frontend: Lernplan öffnen
    Frontend->>Controller: GET /api/v1/plan
    Controller->>Security: JWT prüfen
    Security-->>Controller: Benutzer authentifiziert
    Controller->>Service: Lernplan für Benutzer anfordern
    Service->>Repo: Offene Aufgaben des Benutzers laden
    Repo->>DB: SELECT offene TASKs des Benutzers
    DB-->>Repo: Aufgaben
    Repo-->>Service: Offene Aufgaben
    Service->>Service: AF-01 Lernplan aus offenen Aufgaben berechnen
    Service->>Service: AF-02 Dringlichkeit je Aufgabe berechnen
    Service->>Service: Deadline, Aufwand und Fortschritt berücksichtigen
    Service-->>Controller: LearningPlanDTO
    Controller-->>Frontend: HTTP 200 + LearningPlanDTO
    Frontend-->>Nutzer: Priorisierten Lernplan anzeigen
```

Die eigentliche Berechnung erfolgt ausschließlich im Backend. Dadurch bleibt
die fachliche Logik zentral und kann unabhängig vom Frontend getestet werden.

---

## 6.4 Lernfortschritt aktualisieren — UC-09

Beim Aktualisieren des Lernfortschritts übermittelt das Frontend die neuen
Fortschrittsdaten und optional die Dauer der Lernsitzung an das Backend.

Das Backend authentifiziert den Benutzer, validiert die Eingaben und aktualisiert
den Lernfortschritt. Anschließend wird daraus der neue TaskStatus abgeleitet.

```mermaid
sequenceDiagram
    actor Nutzer as Studierender
    participant Frontend as React SPA
    participant Security as JWT/Security
    participant Controller as Task Controller
    participant Service as Progress Service
    participant TaskRepo as Task Repository
    participant SessionRepo as Learning Session Repository
    participant DB as PostgreSQL

    Nutzer->>Frontend: Fortschritt aktualisieren
    Frontend->>Controller: PATCH /api/v1/tasks/{id}/progress
    Controller->>Security: JWT prüfen
    Security-->>Controller: Benutzer authentifiziert
    Controller->>Service: UpdateProgressRequest
    Service->>TaskRepo: Aufgabe laden
    TaskRepo->>DB: SELECT TASK
    DB-->>TaskRepo: TASK
    TaskRepo-->>Service: TASK
    Service->>Service: Ownership prüfen
    Service->>Service: progressPercent aktualisieren
Service->>Service: AF-03 TaskStatus ableiten

alt Lernsession wurde angegeben
    Service->>SessionRepo: LearningSession speichern
    SessionRepo->>DB: INSERT LEARNING_SESSION
    DB-->>SessionRepo: Gespeicherte Session
    Service->>Service: actualHours aktualisieren
end

Service->>TaskRepo: TASK speichern
TaskRepo->>DB: UPDATE TASK
DB-->>TaskRepo: Aktualisierte Aufgabe
TaskRepo-->>Service: TASK
    Service-->>Controller: TaskDTO
    Controller-->>Frontend: HTTP 200 + TaskDTO
    Frontend-->>Nutzer: Aktualisierten Fortschritt anzeigen
```

---

## 6.5 Aufgabe löschen — UC-06

Beim Löschen einer Aufgabe prüft das Backend zunächst die Authentifizierung
und anschließend die Ownership der Aufgabe.

Wird die Aufgabe gelöscht, werden die zugehörigen abhängigen Datensätze
gemäß den in D1 definierten Kaskadenregeln ebenfalls gelöscht.

```mermaid
sequenceDiagram
    actor Nutzer as Studierender
    participant Frontend as React SPA
    participant Security as JWT/Security
    participant Controller as Task Controller
    participant Service as Task Service
    participant Repo as Task Repository
    participant DB as PostgreSQL

    Nutzer->>Frontend: Aufgabe löschen
    Frontend->>Controller: DELETE /api/v1/tasks/{id}
    Controller->>Security: JWT prüfen
    Security-->>Controller: Benutzer authentifiziert
    Controller->>Service: Aufgabe löschen
    Service->>Repo: Aufgabe laden
    Repo->>DB: SELECT TASK
    DB-->>Repo: TASK
    Repo-->>Service: TASK
    Service->>Service: Ownership prüfen
    Service->>Repo: Aufgabe löschen
    Repo->>DB: DELETE TASK
    DB-->>Repo: Löschung erfolgreich
    Repo-->>Service: Erfolgreich gelöscht
    Service-->>Controller: Erfolgreich
    Controller-->>Frontend: HTTP 204
    Frontend-->>Nutzer: Aufgabe aus Liste entfernen
```

---

## 6.6 Fehlerfall

Fehler werden entsprechend N2.2 zentral behandelt.

Bei einem ungültigen oder fehlenden JWT wird der Zugriff mit HTTP 401
abgelehnt.

Wird versucht, auf eine fremde Ressource zuzugreifen, wird die
Ownership-Prüfung ausgelöst und das Backend antwortet mit HTTP 403.

Bei nicht vorhandenen Ressourcen wird HTTP 404 zurückgegeben.

Unbehandelte interne Fehler werden durch den globalen
`@ControllerAdvice`-Handler abgefangen und als HTTP 500 zurückgegeben.


```text
Frontend
   |
   v
REST Controller
   |
   v
Service
   |
   +-- 401 -> nicht authentifiziert
   +-- 403 -> fremde Ressource
   +-- 404 -> Ressource nicht vorhanden
   +-- 500 -> interner Fehler
```

Das Frontend verarbeitet diese Fehler zentral über den in N2.2 beschriebenen
Axios-Interceptor und den zentralen Error-State.

---

## 6.7 Zusammenfassung

Die Laufzeitsicht zeigt, dass die wesentliche Kommunikation des Systems
entlang der folgenden Struktur erfolgt:

```text
Studierender
     │
     ▼
React SPA
     │
     ▼
REST Controller
     │
     ▼
Security / Authentifizierung
     │
     ▼
Service-Schicht
     │
     ▼
Repository-Schicht
     │
     ▼
PostgreSQL
```

Die fachliche Verarbeitung findet dabei zentral in der Service-Schicht statt.
Die Controller dienen als API-Schnittstelle und die Repository-Schicht
kapselt den Datenbankzugriff.

Die dargestellten Laufzeitszenarien stellen die Verbindung zwischen den
Use Cases aus der Spezifikation und den Architekturbausteinen her.
