# 9 — Architekturentscheidungen

Dieses Kapitel dokumentiert wesentliche Architekturentscheidungen des Study Planers.

Die Entscheidungen werden als Architecture Decision Records (ADRs) festgehalten. Ein ADR beschreibt eine wichtige technische Entscheidung, den zugrunde liegenden Kontext sowie die daraus entstehenden Konsequenzen.

Die ADRs sollen nachvollziehbar machen, warum bestimmte Technologien und Architekturansätze verwendet werden.

---

## ADR-001 — Backend-Technologie mit Java und Spring Boot

**Status:** Accepted

### Kontext

Der Study Planer benötigt ein Backend zur Verarbeitung der Geschäftslogik, zur Bereitstellung der REST-API, zur Authentifizierung und zur Kommunikation mit der PostgreSQL-Datenbank.

Das Backend soll eine klare Schichtenstruktur ermöglichen und insbesondere Controller-, Service- und Repository-Schicht voneinander trennen.

### Entscheidung

Das Backend wird mit **Java 21** und **Spring Boot** umgesetzt.

Für die verschiedenen Aufgabenbereiche werden insbesondere folgende Spring-Komponenten eingesetzt:

- Spring Web für die REST-API
- Spring Security für Authentifizierung und Autorisierung
- Spring Data JPA für den Datenbankzugriff
- Spring Scheduling für den Reminder-Scheduler

Die fachliche Verarbeitung erfolgt überwiegend in der Service-Schicht.

### Konsequenzen

**Vorteile:**

- klare Trennung der Verantwortlichkeiten
- gute Unterstützung für REST-APIs
- integrierte Sicherheitsmechanismen
- gute Unterstützung für Datenbankzugriffe über JPA
- Unterstützung für automatisierte Tests
- geeignet für eine modulare Backend-Struktur

**Nachteile:**

- relativ hoher initialer Konfigurationsaufwand
- Spring bringt eine größere Anzahl an Abhängigkeiten und Konzepten mit
- für ein kleines Projekt entsteht teilweise mehr Struktur als unbedingt notwendig

---

## ADR-002 — PostgreSQL als relationale Datenbank

**Status:** Accepted

### Kontext

Der Study Planer muss Benutzer, Aufgaben, Unteraufgaben, Lernsessions und Reminder dauerhaft speichern.

Zwischen diesen Entitäten bestehen klare Beziehungen, beispielsweise zwischen einem Benutzer und seinen Aufgaben sowie zwischen Aufgaben und Lernsessions.

### Entscheidung

Als Datenbank wird **PostgreSQL** verwendet.

Der Zugriff erfolgt über **Spring Data JPA** und **Hibernate**.

Die Datenbankstruktur wird über **Flyway-Migrationen** verwaltet.

Die Beziehungen zwischen den Entitäten werden relational über Primär- und Fremdschlüssel abgebildet.

### Konsequenzen

**Vorteile:**

- relationale Datenstruktur passt zum Datenmodell
- Unterstützung von Primär- und Fremdschlüsseln
- Transaktionen werden unterstützt
- gute Integration mit Spring Data JPA
- Datenbank kann als Docker-Container betrieben werden
- Flyway ermöglicht nachvollziehbare Schemaänderungen

**Nachteile:**

- zusätzlicher Aufwand für Datenbankschema und Migrationen
- ORM-Abstraktion durch JPA kann bei komplexen Abfragen zusätzlichen Aufwand verursachen
- Datenbank muss für den Betrieb bereitgestellt werden

---

## ADR-003 — JWT-basierte stateless Authentifizierung

**Status:** Accepted

### Kontext

Der Study Planer benötigt eine Authentifizierung, damit Benutzer sich registrieren und anmelden können und anschließend ausschließlich auf ihre eigenen Aufgaben zugreifen können.

Da das Backend als REST-API umgesetzt wird, soll die Authentifizierung möglichst zustandslos erfolgen.

### Entscheidung

Die Anwendung verwendet **JSON Web Tokens (JWT)** zur Authentifizierung.

Nach erfolgreichem Login stellt das Backend ein signiertes JWT aus.

Das Frontend übermittelt das Token bei geschützten API-Anfragen über:

```text
Authorization: Bearer <JWT>
```

Das Backend validiert das Token über Spring Security.

Zusätzlich wird bei Zugriffen auf Ressourcen eine Ownership-Prüfung durchgeführt.

### Konsequenzen

**Vorteile:**

- stateless Authentifizierung
- Backend muss keine klassische Session verwalten
- gut für REST-APIs geeignet
- Benutzeridentität kann aus dem Token ermittelt werden
- horizontale Erweiterung des Backends wird erleichtert

**Nachteile:**

- Token müssen sicher gespeichert werden
- ein gestohlenes Token kann bis zum Ablauf verwendet werden
- Logout und Token-Widerruf sind bei stateless JWTs komplexer als bei serverseitigen Sessions
- zusätzlicher Sicherheitsaufwand bei der Token-Verwaltung

Für das MVP wird eine Token-Gültigkeit von 24 Stunden vorgesehen.

---

## ADR-004 — React und TypeScript für das Frontend

**Status:** Accepted

### Kontext

Der Study Planer benötigt eine webbasierte Benutzeroberfläche zur Verwaltung von Aufgaben, zur Anzeige des Lernplans und zur Darstellung des Lernfortschritts.

Die Anwendung soll sowohl auf Desktop-Geräten als auch auf mobilen Geräten nutzbar sein.

### Entscheidung

Das Frontend wird als **React Single Page Application (SPA)** mit **TypeScript** umgesetzt.

Die Kommunikation mit dem Backend erfolgt über die definierte REST-API.

Die im Datenmodell definierten DTOs werden im Frontend durch entsprechende TypeScript-Typen abgebildet.

### Konsequenzen

**Vorteile:**

- komponentenbasierte Benutzeroberfläche
- gute Wiederverwendbarkeit von UI-Komponenten
- TypeScript ermöglicht statische Typprüfung
- geeignet für interaktive Anwendungen
- REST-API kann klar vom Frontend getrennt werden
- responsive Umsetzung möglich

**Nachteile:**

- zusätzlicher Build- und Tooling-Aufwand
- React und TypeScript benötigen Einarbeitung
- Frontend und Backend müssen bei API-Änderungen konsistent gehalten werden

---

## ADR-005 — Containerisierung mit Docker Compose

**Status:** Accepted

### Kontext

Die Anwendung besteht aus mehreren technischen Komponenten, insbesondere Frontend, Backend und PostgreSQL-Datenbank.

Für die Abgabe soll die Anwendung möglichst einfach gestartet werden können, ohne dass Java, Node.js oder PostgreSQL manuell auf dem Rechner installiert werden müssen.

### Entscheidung

Die Anwendung wird mit **Docker Compose** containerisiert.

Die einzelnen Komponenten werden als separate Services betrieben.

Der Start der Anwendung erfolgt über:

```bash
docker compose up --build
```

Die Konfiguration erfolgt über Umgebungsvariablen und eine `.env`-Datei.

Ein PostgreSQL-Volume wird verwendet, damit die Datenbankdaten bei einem normalen Neustart der Container erhalten bleiben.

### Konsequenzen

**Vorteile:**

- einheitliche Entwicklungs- und Laufzeitumgebung
- keine manuelle Installation von Java, Node.js oder PostgreSQL notwendig
- einfacher Start der Gesamtanwendung
- klare Trennung der Services
- reproduzierbarer Betrieb
- Datenpersistenz über Docker Volume

**Nachteile:**

- Docker ist als zusätzliche Voraussetzung erforderlich
- Containerisierung erhöht die Komplexität der Entwicklungsumgebung
- Fehler in Docker-Netzwerken oder Volumes können die Fehlersuche erschweren
- Docker benötigt zusätzliche Systemressourcen

---

## 9.1 Übersicht der Architekturentscheidungen

| ADR | Entscheidung | Status |
|-----|-------------|--------|
| ADR-001 | Java 21 und Spring Boot als Backend | Accepted |
| ADR-002 | PostgreSQL mit Spring Data JPA | Accepted |
| ADR-003 | JWT für stateless Authentifizierung | Accepted |
| ADR-004 | React und TypeScript für das Frontend | Accepted |
| ADR-005 | Docker Compose für Containerisierung | Accepted |

---

## 9.2 Beziehung zu anderen Architekturkapiteln

Die Architekturentscheidungen stehen in direktem Zusammenhang mit den übrigen Architekturbausteinen.

| Entscheidung | Relevante Kapitel |
|-------------|-------------------|
| Java 21 / Spring Boot | Kapitel 5, Kapitel 6 |
| PostgreSQL / JPA | Kapitel 5, Kapitel 6 |
| JWT / Spring Security | Kapitel 5, Kapitel 6, Kapitel 8 |
| React / TypeScript | Kapitel 5, Kapitel 6, Kapitel 7 |
| Docker Compose | Kapitel 7, Kapitel 8 |

Die Entscheidungen werden außerdem durch die Anforderungen und Randbedingungen der Spezifikation begründet.

---

## 9.3 Umgang mit zukünftigen Architekturentscheidungen

Weitere wesentliche Architekturentscheidungen werden ebenfalls als ADR dokumentiert.

Eine neue Entscheidung sollte mindestens folgende Informationen enthalten:

1. Kontext und Problemstellung
2. getroffene Entscheidung
3. Konsequenzen
4. Status der Entscheidung

Dadurch bleiben wichtige Änderungen der Architektur auch während der Weiterentwicklung des Study Planers nachvollziehbar.