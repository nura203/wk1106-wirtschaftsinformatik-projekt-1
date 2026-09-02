# 9 — Architekturentscheidungen

Dieses Kapitel dokumentiert wesentliche Architekturentscheidungen des Study Planers.

Die Entscheidungen werden als Architecture Decision Records (ADRs) festgehalten. Ein ADR beschreibt eine wichtige technische Entscheidung, den zugrunde liegenden Kontext sowie die daraus entstehenden Konsequenzen.

Die ADRs sollen nachvollziehbar machen, warum bestimmte Technologien und Architekturansätze verwendet werden.

---

## ADR-001 — Backend-Technologie mit Java und Spring Boot

**Status:** Accepted

### Kontext

Der Study Planer benötigt ein Backend zur Verarbeitung der Geschäftslogik, zur Bereitstellung der REST-API, zur Authentifizierung und zur Kommunikation mit der PostgreSQL-Datenbank.

Java 21 ist gemäß TECH-01 als technologische Randbedingung für das Backend vorgegeben. Die Architekturentscheidung beschränkt sich daher auf die Wahl eines Frameworks innerhalb des Java-Ökosystems, das REST, Security, JPA und eine Schichtenarchitektur (Controller / Service / Repository) unterstützt.

### Betrachtete Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|--------|-------------|----------|-----------|
| **A: Spring Boot** | Weit verbreitetes Java-Framework mit umfangreichem Ökosystem (Web, Security, Data, Scheduling). | • Größte Community und umfassendste Dokumentation im Java-Bereich<br>• Nahtlose Integration mit Spring Security, Spring Data JPA und Spring Scheduling | • Höherer initialer Konfigurationsaufwand<br>• Größere Anzahl an Abhängigkeiten |
| **B: Quarkus** | Kubernetes-natives Java-Framework mit Fokus auf schnelle Startup-Zeiten. | • Sehr schneller Start, geringerer Speicherbedarf<br> | • Kleinere Community, weniger Tutorials<br>• Anderes Programmiermodell, für das Team komplett neu |

### Entscheidung

Das Backend wird mit **Java 21** und **Spring Boot** umgesetzt.

Für die verschiedenen Aufgabenbereiche werden insbesondere folgende Spring-Komponenten eingesetzt:

- Spring Web für die REST-API
- Spring Security für Authentifizierung und Autorisierung
- Spring Data JPA für den Datenbankzugriff
- Spring Scheduling für den Reminder-Scheduler

Die fachliche Verarbeitung erfolgt überwiegend in der Service-Schicht.

### Konsequenzen

**Positiv:**

- klare Trennung der Verantwortlichkeiten
- gute Unterstützung für REST-APIs
- integrierte Sicherheitsmechanismen
- gute Unterstützung für Datenbankzugriffe über JPA
- Unterstützung für automatisierte Tests
- geeignet für eine modulare Backend-Struktur

**Negativ:**

- relativ hoher initialer Konfigurationsaufwand
- Spring bringt eine größere Anzahl an Abhängigkeiten und Konzepten mit
- für ein kleines Projekt entsteht teilweise mehr Struktur als unbedingt notwendig

---

## ADR-002 — PostgreSQL als relationale Datenbank

**Status:** Accepted

### Kontext

Der Study Planer muss Benutzer, Aufgaben, Unteraufgaben, Lernsessions und Reminder dauerhaft speichern.

Zwischen diesen Entitäten bestehen klare Beziehungen, beispielsweise zwischen einem Benutzer und seinen Aufgaben sowie zwischen Aufgaben und Lernsessions.

### Betrachtete Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|--------|-------------|----------|-----------|
| **A: PostgreSQL** | Relationale Open-Source-Datenbank mit starkem SQL-Standard und guter JPA-Unterstützung. | • Hervorragende Integration mit Hibernate / Spring Data JPA<br>• Gute Unterstützung für UUID und Docker/Flyway<br>• Open Source, keine Lizenzkosten | • Für sehr einfache Datenmodelle potenziell überdimensioniert • ORM-Abstraktion kann bei komplexen Abfragen zusätzlichen Aufwand verursachen |
| **B: MySQL / MariaDB** | Weit verbreitete relationale Open-Source-Datenbank. | • Sehr große Verbreitung, viele Hosting-Angebote<br>• Gute JPA-Unterstützung | • MySQL unterliegt der Oracle-Lizenzpolitik<br>• UUID- und JSON-Unterstützung weniger ausgereift als bei PostgreSQL |

### Entscheidung

Als Datenbank wird **PostgreSQL** verwendet. Die Entscheidung basiert auf folgenden Faktoren:

- Das Datenmodell (siehe [D1](../spec/D1-datenmodell.md)) ist stark relational mit klaren 1:n-Beziehungen und Fremdschlüsseln. Eine relationale Datenbank ist daher die natürliche Wahl.
- PostgreSQL bietet die beste und stabilste Integration mit Spring Data JPA und Hibernate, was die Entwicklung der Repository-Schicht erheblich vereinfacht. MySQL wäre hier ebenfalls möglich, aber PostgreSQL ist SQL-standardkonformer und bietet bessere Unterstützung für UUIDs (Primärschlüsseltyp in D1).

### Konsequenzen

**Positiv:**

- relationale Datenstruktur passt zum Datenmodell
- Unterstützung von Primär- und Fremdschlüsseln
- Transaktionen werden unterstützt
- gute Integration mit Spring Data JPA
- Datenbank kann als Docker-Container betrieben werden
- Flyway ermöglicht nachvollziehbare Schemaänderungen

**Negativ:**

- zusätzlicher Aufwand für Datenbankschema und Migrationen
- ORM-Abstraktion durch JPA kann bei komplexen Abfragen zusätzlichen Aufwand verursachen
- Datenbank muss für den Betrieb bereitgestellt werden

---

## ADR-003 — JWT-basierte stateless Authentifizierung

**Status:** Accepted

### Kontext

Der Study Planer benötigt eine Authentifizierung, damit Benutzer sich registrieren und anmelden können und anschließend ausschließlich auf ihre eigenen Aufgaben zugreifen können.

Da das Backend als REST-API umgesetzt wird, soll die Authentifizierung möglichst zustandslos erfolgen.

### Betrachtete Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|--------|-------------|----------|-----------|
| **A: JWT (stateless)** | Signierte Tokens, die Benutzeridentität und Gültigkeitsdauer enthalten. | • Vollständig stateless, keine Session-Verwaltung nötig<br>• Passt direkt zu REST/SPA (`Authorization: Bearer`) | • Token müssen sicher gespeichert werden<br>• Widerruf vor Ablauf ist komplexer |
| **B: Session-Cookies** | Serverseitige Sessions, referenziert über ein Cookie. | • Einfache Invalidation beim Logout<br>• Geringeres Risiko bei Token-Diebstahl | • Zustandsbehaftet, Backend muss Session-Status speichern<br>• Skaliert schlechter für entkoppelte REST-APIs |
| **C: OAuth2 / OpenID Connect** | Delegation der Authentifizierung an einen externen Provider (z. B. Google, Keycloak). | • Passwörter werden nicht selbst gespeichert<br>• Benutzer können bestehende Accounts wiederverwenden | • Externe Abhängigkeit (Internet, Provider-Verfügbarkeit)<br>• Deutlich komplexere Konfiguration, für ein MVP überdimensioniert |

### Entscheidung

Die Anwendung verwendet **JSON Web Tokens (JWT)** zur Authentifizierung.

JWT ist stateless und damit die natürliche Wahl für eine REST-API mit SPA-Frontend. Die Ownership-Prüfung (**[NFR-12-03](../spec/N1-nichtfunktionale-anforderungen.md)**) wird durch die direkte Auslesbarkeit der `userId` aus dem Token vereinfacht. Session-Cookies erfordern einen serverseitigen Session-Store; OAuth2 ist für ein Einzelnutzer-MVP überdimensioniert.

### Konsequenzen

**Positiv:**

- stateless Authentifizierung
- Backend muss keine klassische Session verwalten
- gut für REST-APIs geeignet
- Benutzeridentität kann aus dem Token ermittelt werden
- horizontale Erweiterung des Backends wird erleichtert

**Negativ:**

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

### Betrachtete Optionen

| Option | Beschreibung | Vorteile | Nachteile |
|--------|-------------|----------|-----------|
| **A: React + TypeScript** | Komponentenbasierte SPA-Bibliothek mit statischer Typisierung. | • Größtes Ökosystem und beste Community-Unterstützung<br>• Hervorragende TypeScript-Integration<br> | • Erfordert Build-Tooling (Vite, Webpack) |
| **B: Vue.js + TypeScript** | Progressives Framework mit komponentenbasierter Architektur. | • Geringere Lernkurve als React | • Kleinere Community als React<br> |
| **C: Angular** | Full-Framework mit allen benötigten Werkzeugen out-of-the-box. | • Komplettes Framework, einheitliche Projektstruktur | • Sehr steile Lernkurve<br>• Hoher Overhead für ein kleines MVP |

### Entscheidung

Das Frontend wird als **React Single Page Application (SPA)** mit **TypeScript** umgesetzt.

TypeScript bildet die Backend-DTOs (z. B. `TaskDTO`, `LearningPlanDTO`) exakt ab und stellt so die Typenkonsistenz sicher. React ist die am weitesten verbreitete Frontend-Technologie; die größte Community minimiert das Risiko bei fehlenden Vorkenntnissen im Team. Vue und Angular wurden aufgrund der geringeren Verbreitung und Dokumentationslage verworfen.

### Konsequenzen

**Positiv:**

- komponentenbasierte Benutzeroberfläche
- gute Wiederverwendbarkeit von UI-Komponenten
- TypeScript ermöglicht statische Typprüfung
- geeignet für interaktive Anwendungen
- REST-API kann klar vom Frontend getrennt werden
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
| **A: Docker Compose** | Multi-Container-Orchestrierung für lokale Entwicklung und Betrieb. | • Kein manuelles Installieren von Java, Node.js, PostgreSQL nötig | • Docker als zusätzliche Voraussetzung erforderlich |
| **B: Manuelle lokale Installation** | Jedes Teammitglied installiert Java, Node.js und PostgreSQL lokal. | • Keine Docker-Abhängigkeit | • "Works on my machine"-Probleme duch unterschiedliche Versionen<br>• Aufwändigere Einrichtung für den Betreuer |
| **C: Kubernetes** | Container-Orchestrierungsplattform für automatisiertes Deployment. | • Industriestandard für Produktivsysteme | • Viel zu komplex für ein Uni-Projekt mit drei Containern<br>• Steile Lernkurve, Team kennt es nicht |

### Entscheidung

Die Anwendung wird mit **Docker Compose** containerisiert. 

Die Randbedingungen **TECH-02** (`docker compose up --build` als Startvorschrift) und **TECH-04** (kein lokales Java oder Node.js für den Betrieb) schließen eine manuelle Installation aus. Kubernetes ist für drei statische Container überdimensioniert. Docker Compose ist daher die einzige praktikable Option, die alle Randbedingungen erfüllt und gleichzeitig den geringsten Betriebsaufwand verursacht.

Die einzelnen Komponenten werden als separate Services betrieben.

Der Start der Anwendung erfolgt über:

```bash
docker compose up --build
```

### Konsequenzen

**Positiv:**

- einheitliche Entwicklungs- und Laufzeitumgebung
- keine manuelle Installation von Java, Node.js oder PostgreSQL notwendig
- einfacher Start der Gesamtanwendung
- klare Trennung der Services
- reproduzierbarer Betrieb
- Datenpersistenz über Docker Volume

**Negativ:**

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
