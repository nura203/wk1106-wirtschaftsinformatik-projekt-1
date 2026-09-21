# E1 — Leseanleitung

## Zielgruppe

**Studierende (Nutzer)** — um zu verstehen, was der Study Planner macht und warum.

**Zukünftige Entwickler** — um die Codebasis über eine stabile konzeptuelle Karte zu navigieren.

**Betreuer / Prüfer** — um Scope, Rahmenbedingungen und Entscheidungen unabhängig vom Quellcode beurteilen zu können.

---

## Lesereihenfolge

1. Mit **P1** beginnen — Ziele, Scope, Rahmenbedingungen und Erfolgskriterien.

2. Weiter mit **P2** — das strukturelle Skelett, das die weiteren Inhalte einrahmt.

3. **F1–F3** für die funktionale Sicht (Prozesse, Use Cases und Funktionen).

4. **D1–D2** als Referenz für Datenmodell und Datentypen verwenden.

5. **B1–B3** für UI-Details, Batch-Verarbeitung und Druckausgaben lesen.

6. **S1–S3** für Nachbarsysteme, Datenmigration und Inbetriebnahme lesen.

7. **N1–N2** für nichtfunktionale Anforderungen und Querschnittskonzepte verwenden.

8. **E2** als Glossar bei Bedarf zum Nachschlagen verwenden.

---

## Konventionen

- Bausteine werden durch Siedersleben-Codes identifiziert (P1, F2, …).

- Eine Datei pro Baustein, benannt nach dem Schema `<Code>-<Thema>.md`.

- Die Spezifikation beschreibt insbesondere die fachlichen Anforderungen und den vorgesehenen Systemumfang. Implementierungs- und Architekturdetails werden ergänzend in `arch/` dokumentiert.

- IDs wie UC-xx, NFR-xx, AF-xx, CON-xx und weitere definierte Kennungen werden innerhalb der Spezifikation einheitlich verwendet.

- Diagramme werden als Mermaid-Codeblöcke direkt in den Markdown-Dateien eingebettet.

- Nicht anwendbare Bausteine werden nicht einfach weggelassen, sondern in dieser Datei mit einer Begründung dokumentiert.

---

## Status-Legende

| Symbol | Bedeutung |
|--------|-----------|
| ✅ | Baustein existiert in diesem Verzeichnis. |
| 🛠 | Baustein ist geplant, aber noch nicht fertig. |
| ⛔ | Baustein ist für den Study Planner nicht anwendbar. |

---

## Bausteinindex

### 1. Projektgrundlagen

| Baustein | Titel | Status | Datei |
|----------|-------|--------|-------|
| P1 | Ziele und Rahmenbedingungen | ✅ | [P1-ziele-rahmenbedingungen.md](P1-ziele-rahmenbedingungen.md) |
| P2 | Architekturüberblick | ✅ | [P2-architekturueberblick.md](P2-architekturueberblick.md) |

### 2. Abläufe und Funktionen

| Baustein | Titel | Status | Datei |
|----------|-------|--------|-------|
| F1 | Geschäftsprozesse | ✅ | [F1-geschaeftsprozesse.md](F1-geschaeftsprozesse.md) |
| F2 | Anwendungsfälle | ✅ | [F2-anwendungsfaelle.md](F2-anwendungsfaelle.md) |
| F3 | Anwendungsfunktionen | ✅ | [F3-anwendungsfunktionen.md](F3-anwendungsfunktionen.md) |

### 3. Daten

| Baustein | Titel | Status | Datei |
|----------|-------|--------|-------|
| D1 | Datenmodell | ✅ | [D1-datenmodell.md](D1-datenmodell.md) |
| D2 | Datentypenverzeichnis | ✅ | [D2-datentypenverzeichnis.md](D2-datentypenverzeichnis.md) |

### 4. Benutzerschnittstelle

| Baustein | Titel | Status | Datei |
|----------|-------|--------|-------|
| B1 | Dialogspezifikation | ✅ | [B1-dialogspezifikation.md](B1-dialogspezifikation.md) |
| B2 | Batch | ⛔ | [B2-batch.md](B2-batch.md) |
| B3 | Druckausgaben | ⛔ | [B3-druckausgaben.md](B3-druckausgaben.md) |

### 5. Schnittstellen

| Baustein | Titel | Status | Datei |
|----------|-------|--------|-------|
| S1 | Nachbarsysteme | ✅ | [S1-nachbarsysteme.md](S1-nachbarsysteme.md) |
| S2 | Datenmigration | ⛔ | [S2-datenmigration.md](S2-datenmigration.md) |
| S3 | Inbetriebnahme | ✅ | [S3-inbetriebnahme.md](S3-inbetriebnahme.md) |

### 6. Übergreifendes

| Baustein | Titel | Status | Datei |
|----------|-------|--------|-------|
| N1 | Nichtfunktionale Anforderungen | ✅ | [N1-nichtfunktionale-anforderungen.md](N1-nichtfunktionale-anforderungen.md) |
| N2 | Querschnittskonzepte | ✅ | [N2-querschnittskonzepte.md](N2-querschnittskonzepte.md) |

### 7. Ergänzendes

| Baustein | Titel | Status | Datei |
|----------|-------|--------|-------|
| E1 | Leseanleitung | ✅ | dieses Dokument |
| E2 | Glossar | ✅ | [E2-glossar.md](E2-glossar.md) |

---

## Nicht anwendbare Bausteine

Die folgenden Bausteine des Siedersleben-Modells werden für den Study Planner bewusst nicht ausgearbeitet. Die Begründungen dokumentieren, warum diese Bausteine für den aktuellen Projektumfang nicht erforderlich sind.

### B2 — Batch

Der Study Planner hat keine eigenständige Batch-Verarbeitung im Sinne von Siedersleben.

Im aktuellen Implementierungsstand gibt es keinen separaten Batch-Prozess mit einem definierten Massenverarbeitungs-, Eingabe- und Ausgabeverfahren.

Die vorhandenen Funktionen werden im Rahmen normaler Benutzerinteraktionen beziehungsweise einzelner API-Aufrufe verarbeitet.

### B3 — Druckausgaben

Der Study Planner erzeugt keine Druckausgaben, PDF-Berichte oder vergleichbare Artefakte für den Druck.

Der vorgesehene Export ist eine `.ics`-Datei. Diese dient dem Austausch von Kalenderdaten mit externen Kalenderanwendungen. Der Import in eine Kalenderanwendung erfolgt anschließend manuell durch den Benutzer.

### S2 — Datenmigration

Der Study Planner ist eine Neuentwicklung ohne zu übernehmendes Vorgängersystem und ohne zu migrierende Altdaten.

Eine Datenmigration ist daher im aktuellen Projektumfang nicht erforderlich.

Für zukünftige strukturelle Änderungen am Datenbankschema wird Flyway als Werkzeug zur versionierten Verwaltung von Datenbankmigrationen verwendet.

Die Migrationen befinden sich im Backend unter:

`backend/src/main/resources/db/migration/`