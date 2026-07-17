# S2 — Datenmigration

## Anwendbarkeit

**Nicht anwendbar.**

Der Study Planer ist eine Neuentwicklung ohne Vorgängersystem und ohne Altdaten. Das initiale Deployment startet mit einer leeren Datenbank; das Schema wird beim ersten Container-Start automatisch durch Flyway angelegt (S3, NFR-16-02).

Es gibt keine zu migrierenden Daten aus externen Quellen, keine Import-Schnittstelle für Fremddaten und keinen Nutzer-Import aus einem bestehenden System (P1, NG-07).

**Hinweis für spätere Versionen:** Strukturelle Schema-Änderungen werden über Flyway-Migrationsskripte unter `backend/src/main/resources/db/migration/V*.sql` verwaltet. Damit ist Vorsorge für zukünftige Migrationen getroffen, auch wenn für M3 keine anfällt.
