# E1 — Leseanleitung

## Zielgruppe

**Studierende (Nutzer)** — um zu verstehen, was der Study Planer macht und warum.

**Zukünftige Entwickler** — um die Codebasis über eine stabile konzeptuelle Karte zu navigieren.

**Betreuer / Prüfer** — um Scope, Rahmenbedingungen und Entscheidungen unabhängig vom Quellcode zu beurteilen.

---

## Lesereihenfolge

1. Mit **P1** beginnen — Ziele, Scope, Rahmenbedingungen, Erfolgskriterien.
2. Weiter mit **P2** — das strukturelle Skelett, das alles andere einrahmt.
3. **F1–F3** für die funktionale Sicht (Prozesse, Use Cases, Funktionen).
4. **D1–D2** als Dateireferenz beim Lesen aller anderen Bausteine nutzen.
5. **B1** für UI-Details, **S1/S3** für Integration und Inbetriebnahme, **N1/N2** für querschnittliche Qualitäten.
6. **E2** ist ein Glossar — Begriffe bei Bedarf nachschlagen.

---

## Konventionen

- Bausteine werden durch Siedersleben-Codes identifiziert (P1, F2, …).
- Eine Datei pro Baustein, benannt `<Code>-<Thema>.md`.
- Die Spezifikation beschreibt **was** das System ist und **warum** — keine Implementierungsdetails. Architektur- und Code-Entscheidungen liegen in `docs/arch/` (ADRs und Architekturdokument).
- IDs (UC-xx, NF-xx, G-xx, CON-xx, R-xx, SC-xx …) sind innerhalb der Spezifikation eindeutig und werden in `docs/arch/` und im Code referenziert.
- Diagramme sind als Mermaid-Codeblöcke direkt in den Markdown-Dateien eingebettet; GitHub/GitLab rendern sie automatisch.
- Nicht anwendbare Bausteine sind **nicht weggelassen**, sondern am Ende dieser Datei mit Begründung dokumentiert.

---

## Status-Legende

| Symbol | Bedeutung |
|--------|-----------|
| ✅ | Baustein existiert in diesem Verzeichnis. |
| 🛠 | Baustein ist geplant, aber noch nicht fertig. |
| ⛔ | Baustein ist für den Study Planer nicht anwendbar (Begründung unten). |

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
| B2 | Batch | ⛔ | — |
| B3 | Druckausgaben | ⛔ | — |

### 5. Schnittstellen

| Baustein | Titel | Status | Datei |
|----------|-------|--------|-------|
| S1 | Nachbarsysteme | ✅ | [S1-nachbarsysteme.md](S1-nachbarsysteme.md) |
| S2 | Datenmigration | ⛔ | — |
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

Die folgenden Bausteine des Siedersleben-Modells werden für den Study Planer bewusst nicht ausgearbeitet. Die Begründung ist hier festgehalten, damit die Abwesenheit dokumentiert und intentional ist.

### B2 — Batch

Der Study Planer hat keine Batch-Verarbeitung. Der einzige zeitgesteuerte Prozess ist ein täglicher Erinnerungs-Scheduler (Spring `@Scheduled`), der keine Massendatenverarbeitung durchführt, sondern Reminder-Regeln prüft und einzelne Benachrichtigungen auslöst. Dies gilt nicht als Batch im Sinne von Siedersleben.

### B3 — Druckausgaben

Der Study Planer erzeugt keine Druckausgaben, PDFs oder ähnliche Artefakte. Der einzige Export ist eine `.ics`-Datei (UC-11), die als Schnittstelle zu externen Kalenderanwendungen dient und in S1 beschrieben wird.

### S2 — Datenmigration

Der Study Planer ist eine Neuentwicklung ohne Vorgängersystem und ohne Altdaten. Das initiale Deployment startet mit einer leeren Datenbank. Sollten in späteren Versionen strukturelle Schema-Änderungen nötig sein, wird Flyway als Migrationstool eingesetzt (Skripte unter `backend/src/main/resources/db/migration/`).
