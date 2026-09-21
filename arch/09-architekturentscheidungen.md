# 9 — Architekturentscheidungen

Dieses Kapitel dokumentiert wesentliche Architekturentscheidungen des Study Planners.

Die Entscheidungen werden als Architecture Decision Records (ADRs) festgehalten. Ein ADR beschreibt eine wichtige technische Entscheidung, den zugrunde liegenden Kontext, betrachtete Alternativen sowie die daraus entstehenden Konsequenzen.

Die ADRs sollen nachvollziehbar machen, warum bestimmte Technologien und Architekturansätze verwendet werden.

---

## ADR-001 — Backend-Technologie mit Java und Spring Boot

**Status:** Accepted

### Kontext

Der Study Planner benötigt ein Backend zur Verarbeitung der Geschäftslogik, zur Bereitstellung der REST-API, zur Authentifizierung und zur Kommunikation mit der PostgreSQL-Datenbank.

Java 21 ist gemäß TECH-01 als technologische Randbedingung für das Backend vorgegeben. Die eigentliche Architekturentscheidung besteht daher in der Wahl eines geeigneten Frameworks innerhalb des Java-Ökosystems.

Das Framework soll insbesondere REST, Security, JPA und eine Schichtenarchitektur mit Controller-, Service- und Repository-Schicht unterstützen.

### Betrachtete Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|--------|-------------|----------|-----------|
| **A: Spring Boot** | Weit verbreitetes Java-Framework mit umfangreichem Ökosystem. | Gute Integration mit Spring Web, Spring Security und Spring Data JPA; umfangreiche Dokumentation | Größere Anzahl an Abhängigkeiten und Konzepten |
| **B: Quarkus** | Java-Framework mit Fokus auf schnelle Startzeiten und Cloud-/Containerbetrieb. | Schnelle Startzeiten, geringer Ressourcenverbrauch | Für das Team neues Programmiermodell und weniger Erfahrung im Projekt |

### Entscheidung

Das Backend wird mit **Java 21** und **Spring Boot** umgesetzt.

Java 21 folgt dabei der vorgegebenen technischen Randbedingung TECH-01. Spring Boot wurde als Framework für die Umsetzung der REST-API, der Sicherheitsmechanismen und des Datenzugriffs gewählt.

Für die verschiedenen Aufgabenbereiche werden insbesondere folgende Spring-Komponenten eingesetzt:

- Spring Web für die REST-API
- Spring Security für Authentifizierung und Autorisierung
- Spring Data JPA für den Datenbankzugriff

Die fachliche Verarbeitung erfolgt überwiegend in der Service-Schicht.

### Konsequenzen

**Positiv:**

- klare Trennung der Verantwortlichkeiten
- gute Unterstützung für REST-APIs
- integrierte Sicherheitsmechanismen
- gute Unterstützung für Datenbankzugriffe über JPA
- Unterstützung für automatisierte Tests
- geeignete Grundlage für eine modulare Backend-Struktur

**Negativ:**

- zusätzlicher initialer Konfigurationsaufwand
- Spring bringt eine größere Anzahl an Abhängigkeiten und Konzepten mit
- für ein kleines Projekt entsteht teilweise mehr Struktur als unbedingt notwendig

---

## ADR-002 — PostgreSQL als relationale Datenbank

**Status:** Accepted

### Kontext

Der Study Planner muss Benutzer, Aufgaben, Unteraufgaben, Lernsessions und Reminder dauerhaft speichern.

Zwischen diesen Entitäten bestehen klare Beziehungen, beispielsweise zwischen einem Benutzer und seinen Aufgaben sowie zwischen Aufgaben und Lernsessions.

### Betrachtete Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|--------|-------------|----------|-----------|
| **A: PostgreSQL** | Relationale Open-Source-Datenbank mit umfangreicher SQL-Unterstützung. | Gute Integration mit Hibernate und Spring Data JPA; Unterstützung für UUID; gut mit Docker und Flyway kombinierbar | Für sehr einfache Datenmodelle potenziell überdimensioniert |
| **B: MySQL / MariaDB** | Weit verbreitete relationale Open-Source-Datenbanken. | Große Verbreitung und gute JPA-Unterstützung | Für das Projekt wäre ein Wechsel der Datenbanktechnologie ohne konkreten Vorteil verbunden |

### Entscheidung

Als relationale Datenbank wird **PostgreSQL** verwendet.

Das Datenmodell ist stark relational und enthält klare Beziehungen und Fremdschlüssel. Eine relationale Datenbank passt daher zur Struktur des Datenmodells.

PostgreSQL bietet außerdem eine gute Integration mit Spring Data JPA und Hibernate und lässt sich als Docker-Container betreiben.

### Konsequenzen

**Positiv:**

- relationale Datenstruktur passt zum Datenmodell
- Unterstützung von Primär- und Fremdschlüsseln
- Unterstützung von Transaktionen
- gute Integration mit Spring Data JPA
- Betrieb als Docker-Container möglich
- Flyway ermöglicht nachvollziehbare Schemaänderungen

**Negativ:**

- zusätzlicher Aufwand für Datenbankschema und Migrationen
- ORM-Abstraktion durch JPA kann bei komplexen Abfragen zusätzlichen Aufwand verursachen
- Datenbank muss für den Betrieb bereitgestellt werden

---

## ADR-003 — JWT-basierte stateless Authentifizierung

**Status:** Accepted

### Kontext

Der Study Planner benötigt eine Authentifizierung, damit Benutzer sich registrieren und anmelden können und anschließend ausschließlich auf ihre eigenen geschützten Ressourcen zugreifen können.

Da das Backend als REST-API mit einem SPA-Frontend umgesetzt wird, soll die Authentifizierung zustandslos erfolgen.

### Betrachtete Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|--------|-------------|----------|-----------|
| **A: JWT (stateless)** | Signierte Tokens enthalten die Benutzeridentität und Gültigkeitsinformationen. | Keine serverseitige Session-Verwaltung erforderlich; gut für REST/SPA geeignet | Token müssen sicher gespeichert werden; Widerruf vor Ablauf ist komplexer |
| **B: Session-Cookies** | Serverseitige Sessions werden über Cookies referenziert. | Einfache serverseitige Verwaltung und Invalidation | Server muss Session-Zustand verwalten |
| **C: OAuth2 / OpenID Connect** | Authentifizierung über einen externen Identity Provider. | Externe Authentifizierung und weniger eigene Passwortverwaltung | Zusätzliche externe Abhängigkeit und höherer Konfigurationsaufwand |

### Entscheidung

Die Anwendung verwendet **JSON Web Tokens (JWT)** zur Authentifizierung.

JWT ist stateless und eignet sich damit für die REST-API mit SPA-Frontend.

Die Benutzer-ID wird als UUID im JWT gespeichert. Die Ownership-Prüfung verwendet die authentifizierte Benutzer-ID, um den Zugriff auf benutzerspezifische Ressourcen zu kontrollieren.

### Konsequenzen

**Positiv:**

- stateless Authentifizierung
- keine klassische serverseitige Session-Verwaltung
- gut für REST-APIs geeignet
- Benutzeridentität kann aus dem Token ermittelt werden
- Backend kann grundsätzlich horizontal erweitert werden, ohne Session-Zustände zwischen Instanzen synchronisieren zu müssen

**Negativ:**

- Token müssen sicher gespeichert werden
- ein gestohlenes Token kann bis zum Ablauf verwendet werden
- Logout und Token-Widerruf sind bei stateless JWTs aufwändiger als bei serverseitigen Sessions
- zusätzlicher Sicherheitsaufwand bei der Token-Verwaltung

Im aktuellen Implementierungsstand beträgt die Token-Gültigkeit **24 Stunden**.

Das JWT verwendet **HS256**. Die Benutzer-ID wird als UUID im `subject` des Tokens gespeichert.

---

## ADR-004 — React und TypeScript für das Frontend

**Status:** Accepted

### Kontext

Der Study Planner benötigt eine webbasierte Benutzeroberfläche zur Verwaltung von Aufgaben, zur Anzeige des Lernplans und zur Darstellung des Lernfortschritts.

Die Anwendung soll auf unterschiedlichen Bildschirmgrößen nutzbar sein.

### Betrachtete Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|--------|-------------|----------|-----------|
| **A: React + TypeScript** | Komponentenbasierte SPA mit statischer Typisierung. | Gute TypeScript-Integration, komponentenbasierte Entwicklung und großes Ökosystem | Zusätzlicher Build- und Tooling-Aufwand |
| **B: Vue.js + TypeScript** | Progressives Framework mit komponentenbasierter Architektur. | Komponentenbasierte Entwicklung und vergleichsweise einfacher Einstieg | Für das Team weniger Erfahrung |
| **C: Angular** | Vollständiges Framework mit umfangreicher Projektstruktur. | Viele Funktionen und Werkzeuge integriert | Höherer Umfang und zusätzlicher Lernaufwand für ein kleines Projekt |

### Entscheidung

Das Frontend wird als **React Single Page Application (SPA)** mit **TypeScript** umgesetzt.

TypeScript bildet die fachlichen Datenstrukturen des Backends im Frontend ab und unterstützt dadurch die Typenkonsistenz.

React ermöglicht eine komponentenbasierte und interaktive Benutzeroberfläche.

### Konsequenzen

**Positiv:**

- komponentenbasierte Benutzeroberfläche
- gute Wiederverwendbarkeit von UI-Komponenten
- TypeScript ermöglicht statische Typprüfung
- geeignet für interaktive Anwendungen
- klare Trennung zwischen Frontend und REST-API
- responsive Umsetzung möglich

**Negativ:**

- zusätzlicher Build- und Tooling-Aufwand
- React und TypeScript benötigen Einarbeitung
- Frontend und Backend müssen bei API-Änderungen konsistent gehalten werden

---

## ADR-005 — Containerisierung mit Docker Compose

**Status:** Accepted

### Kontext

Die Anwendung besteht aus mehreren technischen Komponenten, insbesondere Frontend, Backend und PostgreSQL-Datenbank.

Für die Abgabe soll die Anwendung möglichst einfach gestartet werden können, ohne dass Java, Node.js oder PostgreSQL manuell auf dem Rechner installiert werden müssen.

### Betrachtete Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|--------|-------------|----------|-----------|
| **A: Docker Compose** | Verwaltung der benötigten Container als gemeinsamer Anwendungsverbund. | Einheitlicher Start der Komponenten; keine manuelle Installation von Java, Node.js und PostgreSQL erforderlich | Docker ist als zusätzliche Voraussetzung erforderlich |
| **B: Manuelle lokale Installation** | Java, Node.js und PostgreSQL werden direkt auf dem Rechner installiert. | Kein Docker erforderlich | Unterschiedliche lokale Versionen können zu Umgebungsproblemen führen; höherer Einrichtungsaufwand |
| **C: Kubernetes** | Container-Orchestrierungsplattform für umfangreichere Deployments. | Geeignet für komplexe Containerlandschaften | Für den Umfang des Study Planners unnötig komplex |

### Entscheidung

Die Anwendung wird mit **Docker Compose** containerisiert.

Die Randbedingungen **TECH-02** und **TECH-04** unterstützen diese Entscheidung:

- **TECH-02:** Die Anwendung muss aus dem Verzeichnis `backend` mit `docker compose up --build` gestartet werden können.
- **TECH-04:** Für den Betrieb der Anwendung sollen kein lokal installiertes Java und Node.js erforderlich sein.

Die drei zentralen Komponenten werden als separate Docker-Compose-Services betrieben:

- Frontend
- Backend
- PostgreSQL

Die Compose-Konfiguration befindet sich im Verzeichnis `backend`.

Der Start der Anwendung erfolgt aus diesem Verzeichnis mit:

```bash
cd backend
docker compose up --build