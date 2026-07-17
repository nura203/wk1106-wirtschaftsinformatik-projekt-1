# P2 — Architekturüberblick

Dieser Baustein gibt einen kompakten Überblick für Leser, die nicht alle Architekturdokumente lesen. Er beantwortet: Wie ist das System grob aufgebaut, welche Technologien werden eingesetzt, und wie hängen die Teile zusammen? Die vollständige Beschreibung mit ADRs findet sich in `docs/arch/`.

---

## P2.1 Strukturüberblick

Der Study Planer folgt einer klassischen dreischichtigen Client-Server-Architektur:

```
┌──────────────────────────────────────────┐
│            Browser (Client)               │
│       React SPA  ·  TypeScript 5          │
│       Vite · React Router                 │
└─────────────────┬────────────────────────┘
                  │ HTTPS · REST/JSON
┌─────────────────▼────────────────────────┐
│           Backend (Server)                │
│      Spring Boot 3  ·  Java 21            │
│  auth │ task │ plan │ reminder │ export   │
└─────────────────┬────────────────────────┘
                  │ JDBC / JPA
┌─────────────────▼────────────────────────┐
│             Persistenz                    │
│          PostgreSQL 15                    │
└──────────────────────────────────────────┘
```

Externe Dienste (optional):

```
Backend ──SMTP/TLS──► E-Mail-Provider   (Erinnerungen)
Browser ──Download──► Kalender-App      (.ics-Export)
```

---

## P2.2 Technologiestack

| Schicht | Technologie | Version | Begründung (Kurzform) |
|---------|-------------|---------|----------------------|
| Backend-Sprache | Java | 21 (LTS) | Typsicherheit; Spring-Ökosystem; Teamkompetenz |
| Backend-Framework | Spring Boot | 3.x | Convention-over-Configuration; integrierte Security |
| Frontend-Sprache | TypeScript | 5 | Typsicherheit über JS hinaus |
| Frontend-Framework | React | 18 | Komponentenmodell; hohe Verbreitung; gutes Tooling |
| Build Frontend | Vite | 5 | Schneller Dev-Server; einfache React-Integration |
| Build Backend | Maven | 3.x | Standard im Java-Ökosystem |
| Datenbank | PostgreSQL | 15+ | Relationale Daten; ACID; UUID-Support nativ |
| Auth | JWT (HS256) | — | Stateless; kein Session-State im Backend; skalierbar |
| Containerisierung | Docker Compose | 2.x | Reproduzierbare Umgebung; kein lokales JDK/Node nötig |
| Schema-Migration | Flyway | — | Versionierte SQL-Skripte; automatischer Start |

Vollständige Begründungen für alle wesentlichen Entscheidungen → `docs/arch/` (ADRs).

---

## P2.3 Komponentenstruktur (Repository-Layout)

```
study-planer/
├── backend/
│   └── src/main/java/de/thm/studyplaner/
│       ├── auth/          # JWT-Filter, Login, Registrierung (UC-01, UC-02)
│       ├── task/          # Aufgaben-CRUD (UC-04–UC-06)
│       ├── plan/          # Lernplan-Berechnung (AF-01, UC-08)
│       ├── reminder/      # Erinnerungsregeln + Scheduler (AF-10, UC-10)
│       ├── export/        # iCal-Generierung (AF-12, UC-11)
│       └── user/          # Nutzerprofil
├── frontend/
│   └── src/
│       ├── pages/         # Dashboard, Aufgaben, Lernplan, Einstellungen
│       ├── components/    # Wiederverwendbare UI-Komponenten
│       ├── hooks/         # Custom React Hooks (Auth, API)
│       └── api/           # API-Client (Axios)
├── docs/
│   ├── spec/              # Diese Spezifikation (Siedersleben)
│   └── arch/              # arc42-Architekturdokument + ADRs
├── docker-compose.yml
├── .env.example
└── INSTALL.md
```

Die Paketnamen im Backend (`auth`, `task`, `plan`, `reminder`, `export`) entsprechen den Funktionsgruppen aus F3 und den Komponenten in der Architekturbeschreibung.

---

## P2.4 Externe Schnittstellen (Überblick)

| System | Protokoll | Pflicht | Beschreibung |
|--------|-----------|---------|--------------|
| PostgreSQL | JDBC | Ja | Datenpersistenz (intern) |
| E-Mail-Provider | SMTP/TLS | Nein | Erinnerungsversand (AF-11); deaktiviert wenn nicht konfiguriert |
| Kalender-App | iCal (RFC 5545) | Nein | `.ics`-Download im Browser (AF-12, UC-11) |
| Adminer | HTTP (Port 8888) | Nein | Datenbankoberfläche; nur in Entwicklung; kein Produktivbetrieb |

Detaillierte Schnittstellenbeschreibungen → S1.
