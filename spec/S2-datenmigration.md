# S2 — Datenmigration

## Anwendbarkeit

**Nicht anwendbar.**

Der Study Planner ist eine Neuentwicklung ohne Vorgängersystem und ohne zu übernehmende Altdaten. Für das initiale Deployment wird eine leere Datenbank verwendet.

Das Datenbankschema wird beim Start des Backends durch Flyway-Migrationen versioniert und aufgebaut.

Es gibt keine zu migrierenden Daten aus externen Quellen, keine Import-Schnittstelle für Fremddaten und keinen Nutzer-Import aus einem bestehenden System.

## Migrationen für zukünftige Versionen

Strukturelle Änderungen des Datenbankschemas werden über versionierte Flyway-Migrationsskripte verwaltet.

Die Migrationen befinden sich im Backend unter:

```text
backend/src/main/resources/db/migration/