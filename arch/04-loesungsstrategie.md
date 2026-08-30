# 4 — Lösungsstrategie

Dieses Kapitel beschreibt die grundlegenden technischen Lösungsansätze des
Study Planers. Die hier beschriebene Struktur bildet die Grundlage für die
detaillierte Baustein-, Laufzeit- und Verteilungssicht.

Konkrete Technologieentscheidungen und ihre Alternativen werden in Kapitel 9
als Architecture Decision Records (ADRs) dokumentiert.

## 4.1 Grundlegender Architekturansatz

Der Study Planer wird als webbasierte Anwendung mit einer Trennung zwischen Präsentation, Geschäftslogik und Persistenz realisiert.

Das Frontend wird als React-basierte Single Page Application mit TypeScript  realisiert. Es ist für die Darstellung der Benutzeroberfläche und die Interaktion mit dem Benutzer verantwortlich.

Das Backend wird mit Java 21 und Spring Boot umgesetzt. Es stellt die REST-API bereit und enthält die zentrale Geschäftslogik der Anwendung.

Die persistenten Daten werden in einer relationalen PostgreSQL-Datenbank gespeichert. Der Zugriff auf die Datenbank erfolgt über Spring Data JPA und Hibernate.

Die Kommunikation zwischen Frontend und Backend erfolgt über eine REST-Schnittstelle. Der Datenaustausch erfolgt über JSON und die in der Architektur vorgesehenen DTOs.

Der grundlegende Aufbau ist:

```text
┌──────────────────────────────┐
│       React SPA              │
│       TypeScript             │
│                              │
│  Benutzeroberfläche          │
└──────────────┬───────────────┘
               │
          REST / JSON
               │
┌──────────────▼───────────────┐
│       Spring Boot            │
│       Java 21                │
│                              │
│  REST API                    │
│  Geschäftslogik              │
│  Security                    │
└──────────────┬───────────────┘
               │
          JPA / Hibernate
               │
┌──────────────▼───────────────┐
│       PostgreSQL             │
│                              │
│  Persistente Daten           │
└──────────────────────────────┘
```

## 4.2 Sicherheitsstrategie

Die Authentifizierung erfolgt gemäß N2.1 stateless über JWT.

Nicht öffentliche REST-Endpunkte werden durch Spring Security geschützt.
Nach erfolgreicher JWT-Validierung wird die Identität des Benutzers an die
fachliche Verarbeitung weitergegeben.

Zusätzlich wird in der Service-Schicht geprüft, ob die angeforderte Ressource
dem authentifizierten Benutzer gehört. Dadurch wird die in NFR-12-03 geforderte
Ownership-Prüfung zentral umgesetzt.

Passwörter werden nicht im Klartext gespeichert, sondern mit bcrypt gehasht.