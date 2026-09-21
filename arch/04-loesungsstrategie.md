# 4 — Lösungsstrategie

Dieses Kapitel beschreibt die grundlegenden technischen Lösungsansätze des Study Planners. Die hier beschriebene Struktur bildet die Grundlage für die detaillierte Baustein-, Laufzeit- und Verteilungssicht.

Konkrete Technologieentscheidungen und ihre Alternativen werden in Kapitel 9 als Architecture Decision Records (ADRs) dokumentiert.

---

## 4.1 Grundlegender Architekturansatz

Der Study Planner wird als webbasierte Anwendung mit einer Trennung zwischen Präsentation, Geschäftslogik und Persistenz realisiert.

Die React-basierte Single-Page-Application übernimmt die Darstellung und die Interaktion mit dem Studierenden. Das Spring-Boot-Backend stellt die REST-API bereit und enthält die fachliche Geschäftslogik. Die dauerhafte Speicherung der Daten erfolgt über eine relationale PostgreSQL-Datenbank.

Der grundlegende Aufbau ist:

```text
┌──────────────────────────────┐
│          React SPA           │
│          TypeScript          │
│                              │
│     Benutzeroberfläche       │
└──────────────┬───────────────┘
               │
          REST / JSON
               │
┌──────────────▼───────────────┐
│         Spring Boot          │
│          Java 21             │
│                              │
│          REST API            │
│       Geschäftslogik         │
│          Security            │
└──────────────┬───────────────┘
               │
          JPA / Hibernate
               │
┌──────────────▼───────────────┐
│         PostgreSQL           │
│                              │
│     Persistente Daten        │
└──────────────────────────────┘