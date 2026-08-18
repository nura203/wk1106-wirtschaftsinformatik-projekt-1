# 11 — Risiken und technische Schulden

Dieses Kapitel beschreibt technische und organisatorische Risiken des Study Planers sowie mögliche technische Schulden, die während der weiteren Entwicklung entstehen oder bereits aus dem aktuellen Projektstand hervorgehen können.

Die Risiken orientieren sich an den in der Spezifikation beschriebenen Risiken aus P1.8.

---

## 11.1 Risiken

### R-01 — Ungeeignete Ergebnisse des Lernplan-Algorithmus

**Beschreibung:**

Der Lernplan-Algorithmus kann bei ungünstigen Eingaben, beispielsweise wenn viele Aufgaben dieselbe Deadline besitzen, zu einer wenig sinnvollen Priorisierung führen.

**Wahrscheinlichkeit:** Mittel

**Auswirkung:** Hoch

**Maßnahmen:**

- Grenzfälle durch Unit-Tests überprüfen
- Lernplanberechnung mit unterschiedlichen Aufgabenverteilungen testen
- Fallback-Verhalten vorsehen
- Priorisierung anhand von Deadline, offenem Aufwand und Gewichtung nachvollziehbar halten

**Bezug:**

- AF-01
- NFR-11-03

---

### R-02 — Versehentliches Committen von Secrets

**Beschreibung:**

JWT-Secrets, Datenbankpasswörter oder SMTP-Zugangsdaten könnten versehentlich in das Git-Repository eingecheckt werden.

**Wahrscheinlichkeit:** Niedrig

**Auswirkung:** Sehr hoch

**Maßnahmen:**

- `.env` in `.gitignore` aufnehmen
- nur Platzhalter in `.env.example` verwenden
- Secrets über Umgebungsvariablen bereitstellen
- vor Commits prüfen, dass keine sensiblen Konfigurationsdateien enthalten sind
- optional Pre-Commit-Hooks verwenden

**Bezug:**

- CON-06
- NFR-12-04
- N2.5

---

### R-03 — Fehlende SMTP-Konfiguration

**Beschreibung:**

Wenn kein SMTP-Provider konfiguriert wurde, können keine E-Mail-Erinnerungen versendet werden.

**Wahrscheinlichkeit:** Mittel

**Auswirkung:** Mittel

**Maßnahmen:**

Eine fehlende SMTP-Konfiguration soll nicht zum Absturz der Anwendung führen.

Der E-Mail-Kanal wird deaktiviert und der Zustand wird entsprechend geloggt.

Die übrigen Funktionen der Anwendung bleiben verfügbar.

**Bezug:**

- AF-10
- AF-11
- NFR-13-03
- S1.2

---

### R-04 — Unvollständige Implementierung durch Zeitdruck

**Beschreibung:**

Durch den begrenzten Projektzeitraum besteht das Risiko, dass nicht alle vorgesehenen Funktionen vollständig umgesetzt werden können.

**Wahrscheinlichkeit:** Mittel

**Auswirkung:** Hoch

**Maßnahmen:**

Die Funktionen werden anhand ihrer Priorität umgesetzt.

Die in F3 definierte Priorisierung wird dabei berücksichtigt:

- Muss-Funktionen zuerst
- Soll-Funktionen anschließend
- Kann-Funktionen nur bei ausreichender Kapazität

Dadurch soll ein funktionsfähiges MVP auch bei begrenzter Entwicklungszeit sichergestellt werden.

**Bezug:**

- F3
- P1.6
- P1.8

---

### R-05 — Performance des Lernplan-Algorithmus

**Beschreibung:**

Bei einer größeren Anzahl von Aufgaben kann die Berechnung des Lernplans länger dauern als vorgesehen.

**Wahrscheinlichkeit:** Niedrig

**Auswirkung:** Mittel

**Maßnahmen:**

- effiziente Sortierung und Berechnung verwenden
- Zielkomplexität von O(n log n) berücksichtigen
- Performance mit bis zu 100 Aufgaben testen
- NFR-11-03 als messbares Ziel verwenden

**Bezug:**

- AF-01
- NFR-11-03

---

## 11.2 Sicherheitsrisiken

Neben den allgemeinen Projektrisiken bestehen sicherheitsrelevante Risiken.

### Unzureichender Schutz von Zugangsdaten

Passwörter dürfen niemals im Klartext gespeichert werden.

Die Anwendung verwendet deshalb bcrypt zur Speicherung der Passwort-Hashes.

### Fehlerhafte Ownership-Prüfung

Eine fehlerhafte Ownership-Prüfung könnte dazu führen, dass ein Benutzer auf fremde Aufgaben zugreifen kann.

Die Prüfung wird deshalb zentral in der Service-Schicht durchgeführt.

### Fehlerhafte JWT-Verarbeitung

Ein fehlerhaft validiertes oder unzureichend geschütztes JWT könnte unautorisierten Zugriff ermöglichen.

Die JWT-Prüfung wird zentral über Spring Security durchgeführt.

---

## 11.3 Technische Schulden

Technische Schulden entstehen, wenn eine zunächst vereinfachte oder vorläufige technische Lösung später zusätzlichen Änderungs- oder Wartungsaufwand verursacht.

Da sich das Projekt noch in der Entwicklung befindet, können einzelne Punkte bewusst zunächst vereinfacht umgesetzt werden.

---

### TD-01 — Architektur und Implementierung müssen synchron gehalten werden

**Beschreibung:**

Die Architektur wird parallel zur Entwicklung dokumentiert. Dadurch besteht das Risiko, dass sich die tatsächliche Implementierung und die Dokumentation voneinander entfernen.

**Auswirkung:**

Inkonsistenzen zwischen Spec, Architektur und Code erschweren den Code-Walkthrough und die Wartung.

**Maßnahme:**

Die Architekturkapitel werden regelmäßig gegen den aktuellen Code geprüft.

Insbesondere werden Bausteinnamen, Schnittstellen und technische Entscheidungen mit der tatsächlichen Implementierung abgeglichen.

---

### TD-02 — MVP-Fokus bei optionalen Funktionen

**Beschreibung:**

Funktionen mit der Priorität Soll oder Kann können zunächst nur teilweise oder vereinfacht umgesetzt werden.

Dazu gehören insbesondere optionale E-Mail-Erinnerungen und der iCal-Export.

**Auswirkung:**

Ein Teil der vorgesehenen Funktionalität könnte zunächst fehlen oder vereinfacht sein.

**Maßnahme:**

Die Muss-Funktionen aus F3 werden zuerst vollständig umgesetzt.

Optionale Funktionen werden erst anschließend implementiert, sofern die verfügbare Entwicklungszeit dies zulässt.

---

### TD-03 — JWT-Speicherung im Frontend

**Beschreibung:**

Für das MVP ist die Speicherung des JWT im `localStorage` vorgesehen.

Diese Lösung ist einfach umzusetzen, bietet jedoch einen geringeren Schutz gegen bestimmte XSS-Szenarien als eine geeignete `httpOnly`-Cookie-Lösung.

**Auswirkung:**

Bei einer späteren produktiven Nutzung könnte eine sicherere Session- beziehungsweise Token-Verwaltung erforderlich werden.

**Maßnahme:**

Für das MVP bleibt die in N2.1 beschriebene Lösung bestehen.

Für einen produktiven Einsatz sollte eine sichere Cookie-basierte Lösung geprüft werden.

---

### TD-04 — Begrenzte Testabdeckung während der Entwicklung

**Beschreibung:**

Die Spezifikation sieht eine Testabdeckung der Service-Schicht von mindestens 60 % vor. Während der laufenden Entwicklung kann die tatsächliche Testabdeckung zunächst darunter liegen.

**Auswirkung:**

Fehler in der Geschäftslogik können möglicherweise erst bei späteren Integrationstests oder manuellen Tests erkannt werden.

**Maßnahme:**

Unit-Tests werden parallel zur Implementierung ergänzt.

Besondere Priorität haben dabei:

- Lernplanberechnung
- Dringlichkeitsberechnung
- TaskStatus-Ableitung
- Ownership-Prüfung

---

## 11.4 Priorisierung

Die Risiken und technischen Schulden werden nach ihrer Bedeutung für die Abgabe priorisiert.

| ID | Thema | Priorität | Maßnahme |
|----|-------|-----------|----------|
| R-01 | Lernplan-Algorithmus | Hoch | Tests und Grenzfälle |
| R-02 | Secrets im Repository | Sehr hoch | `.gitignore`, Umgebungsvariablen |
| R-03 | SMTP-Konfiguration | Mittel | Graceful Degradation |
| R-04 | Zeitdruck | Hoch | Muss/Soll/Kann-Priorisierung |
| R-05 | Performance | Mittel | Performance-Tests |
| TD-01 | Spec-Code-Konsistenz | Hoch | Regelmäßiger Abgleich |
| TD-02 | Optionale Funktionen | Mittel | MVP-Priorisierung |
| TD-03 | JWT-Speicherung | Mittel | später prüfen |
| TD-04 | Testabdeckung | Hoch | Unit- und Integrationstests |

---

## 11.5 Umgang mit Risiken und technischen Schulden

Risiken und technische Schulden werden während der weiteren Entwicklung regelmäßig überprüft.

Neue Risiken sollen dokumentiert werden, sobald sie einen relevanten Einfluss auf die Architektur, Qualität oder Abgabefähigkeit des Projekts haben.

Technische Schulden sollen insbesondere dann behoben werden, wenn sie:

- die Sicherheit beeinträchtigen
- die Wartbarkeit deutlich reduzieren
- die Konsistenz zwischen Architektur und Code gefährden
- die Erfüllung einer Muss-Anforderung verhindern

Damit dient dieses Kapitel als laufende Übersicht über technische Unsicherheiten und notwendige Verbesserungen während der Projektentwicklung.