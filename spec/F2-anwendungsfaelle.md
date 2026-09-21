# F2 — Anwendungsfälle

Use Cases im Sinne von Siedersleben (Kap. 4.4): konkrete Interaktionsszenarien zwischen einem Studierenden und dem System, die jeweils ein einzelnes, für den Nutzer bedeutsames Ziel verfolgen und in einem stabilen Zustand enden.

F2 beschreibt die systemunterstützte Teilmenge der in F1 beschriebenen Geschäftsprozesse. Jeder Use Case folgt dem Spezifikationstemplate nach Pohl & Rupp (2021), *Basiswissen Requirements Engineering*, 5. Aufl.

---

## F2.1 Use-Case-Index

| ID | Use Case | Gruppe | Bezug zu F1 | Status |
|----|----------|--------|-------------|--------|
| [UC-01](#uc-01--registrieren) | Registrieren | Zugang | — (einmalige Einrichtung) | ✅ |
| [UC-02](#uc-02--anmelden) | Anmelden | Zugang | GP-01 Vorbedingung | ✅ |
| [UC-03](#uc-03--abmelden) | Abmelden | Zugang | — | ✅ |
| [UC-04](#uc-04--aufgabe-anlegen) | Aufgabe anlegen | Aufgabenverwaltung | GP-01 A2–A4 | ✅ |
| [UC-05](#uc-05--aufgabe-bearbeiten) | Aufgabe bearbeiten | Aufgabenverwaltung | GP-02 A5 | ✅ |
| [UC-06](#uc-06--aufgabe-löschen) | Aufgabe löschen | Aufgabenverwaltung | GP-02 | ✅ |
| [UC-07](#uc-07--dashboard-anzeigen) | Dashboard anzeigen | Überblick | GP-02 A1–A2 | ✅ |
| [UC-08](#uc-08--lernplan-anzeigen) | Lernplan anzeigen | Planung | GP-01 A5, GP-02 A3, GP-03 A2 | ✅ |
| [UC-09](#uc-09--lernfortschritt-eintragen) | Lernfortschritt eintragen | Tracking | GP-02 A4/A6, GP-03 A4/A5 | ✅ |
| [UC-10](#uc-10--erinnerung-konfigurieren) | Erinnerung konfigurieren | Einstellungen | GP-01 A6 | ✅ |
| [UC-11](#uc-11--kalenderexport) | Kalenderexport | Export | — | ✅ |

**Status-Legende:** ✅ fertig · 🚧 in Arbeit · ⬜ offen

---

## F2.2 Zugang

### UC-01 — Registrieren

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-01 |
| **Name** | Registrieren |
| **Beschreibung** | Ein neuer Studierender legt ein Nutzerkonto an und wird anschließend authentifiziert. |
| **Auslöser** | Nutzer öffnet die Anwendung ohne gültige Authentifizierung und wählt „Registrieren“. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Die E-Mail-Adresse ist noch nicht im System registriert. |
| **Nachbedingung (Erfolg)** | Nutzerkonto angelegt; JWT ausgestellt; Nutzer auf Dashboard weitergeleitet. |
| **Nachbedingung (Misserfolg)** | Kein Konto angelegt; Fehlermeldung angezeigt. |
| **Hauptszenario** | 1. Nutzer öffnet das Registrierungsformular.<br>2. Nutzer gibt Benutzername, E-Mail-Adresse und Passwort ein.<br>3. System validiert die Eingaben.<br>4. System prüft, ob die E-Mail-Adresse bereits existiert.<br>5. System speichert das Konto mit einem BCrypt-Hash des Passworts.<br>6. System stellt ein JWT aus und leitet den Nutzer auf das Dashboard weiter. |
| **Alternative Szenarien** | — |
| **Ausnahmeszenarien** | **E-Mail bereits vergeben:** System zeigt eine Fehlermeldung; kein Konto wird angelegt.<br><br>**Validierungsfehler:** Betroffene Felder werden markiert; die Eingabe wird nicht erfolgreich verarbeitet. |
| **Qualitäten** | NFR-12-01 (Passwortschutz); NFR-15-02 (Formularvalidierung). |

---

### UC-02 — Anmelden

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-02 |
| **Name** | Anmelden |
| **Beschreibung** | Ein registrierter Studierender authentifiziert sich und erhält ein JWT für weitere geschützte Anfragen. |
| **Auslöser** | Nutzer öffnet die Anwendung ohne gültige Authentifizierung. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Nutzerkonto existiert. |
| **Nachbedingung (Erfolg)** | Gültiges JWT wurde ausgestellt; Nutzer wird auf das Dashboard weitergeleitet. |
| **Nachbedingung (Misserfolg)** | Kein gültiges JWT; generische Fehlermeldung wird angezeigt. |
| **Hauptszenario** | 1. System zeigt das Login-Formular.<br>2. Nutzer gibt E-Mail und Passwort ein.<br>3. System prüft die Zugangsdaten gegen die gespeicherten Benutzerdaten.<br>4. System stellt bei erfolgreicher Prüfung ein JWT mit einer Gültigkeit von 24 Stunden aus.<br>5. Client speichert das JWT.<br>6. Nutzer wird auf das Dashboard weitergeleitet. |
| **Alternative Szenarien** | — |
| **Ausnahmeszenarien** | **Falsche Zugangsdaten:** Generische Fehlermeldung ohne Unterscheidung zwischen unbekannter E-Mail-Adresse und falschem Passwort. |
| **Qualitäten** | NFR-12-02 (geschützte API-Endpunkte); NFR-12-05 (keine unnötige Informationsweitergabe beim Login). |

---

### UC-03 — Abmelden

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-03 |
| **Name** | Abmelden |
| **Beschreibung** | Nutzer beendet die lokale Authentifizierung der Anwendung. |
| **Auslöser** | Nutzer klickt „Abmelden“. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Nutzer ist authentifiziert. |
| **Nachbedingung** | JWT wird aus dem Client entfernt; Nutzer wird auf die Login-Seite weitergeleitet. |
| **Hauptszenario** | 1. Nutzer klickt „Abmelden“.<br>2. Client entfernt das gespeicherte JWT.<br>3. Nutzer wird auf die Login-Seite weitergeleitet. |
| **Alternative Szenarien** | — |
| **Ausnahmeszenarien** | — |
| **Qualitäten** | — |

---

## F2.3 Aufgabenverwaltung

### UC-04 — Aufgabe anlegen

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-04 |
| **Name** | Aufgabe anlegen |
| **Beschreibung** | Studierender erfasst eine neue Aufgabe vom Typ Prüfung, Abgabe oder Lernziel. |
| **Auslöser** | Nutzer öffnet die Funktion zum Anlegen einer neuen Aufgabe. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Nutzer ist authentifiziert. |
| **Nachbedingung (Erfolg)** | Aufgabe wurde gespeichert und dem authentifizierten Benutzer zugeordnet. |
| **Nachbedingung (Misserfolg)** | Keine Aufgabe angelegt; Validierungs- oder Fehlermeldung angezeigt. |
| **Hauptszenario** | 1. Nutzer öffnet das Formular zum Anlegen einer Aufgabe.<br>2. Nutzer wählt den Aufgabentyp (`EXAM`, `ASSIGNMENT` oder `GOAL`).<br>3. Nutzer gibt die erforderlichen Angaben ein.<br>4. Nutzer speichert die Aufgabe.<br>5. System validiert die Eingaben.<br>6. System speichert die Aufgabe mit der Benutzer-ID des authentifizierten Benutzers.<br>7. Nutzer wird zur Aufgabenübersicht beziehungsweise Detailansicht weitergeleitet. |
| **Alternative Szenarien** | **Nutzer bricht ab:** Keine Daten werden gespeichert. |
| **Ausnahmeszenarien** | **Ungültige Eingabe:** System weist die Eingabe zurück und zeigt eine entsprechende Fehlermeldung. |
| **Qualitäten** | NFR-15-02 (Formularvalidierung). |

---

### UC-05 — Aufgabe bearbeiten

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-05 |
| **Name** | Aufgabe bearbeiten |
| **Beschreibung** | Studierender ändert die Daten einer bestehenden eigenen Aufgabe. |
| **Auslöser** | Nutzer öffnet eine Aufgabe und wählt die Bearbeitungsfunktion. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Nutzer ist authentifiziert; Aufgabe existiert und gehört dem Nutzer. |
| **Nachbedingung (Erfolg)** | Geänderte Aufgabendaten wurden persistiert. |
| **Nachbedingung (Misserfolg)** | Ursprüngliche Daten bleiben unverändert; Fehlermeldung wird angezeigt. |
| **Hauptszenario** | 1. Nutzer öffnet die Aufgabendetailansicht.<br>2. Nutzer öffnet die Bearbeitungsfunktion.<br>3. System lädt die bestehenden Werte.<br>4. Nutzer ändert die gewünschten Felder.<br>5. Nutzer speichert.<br>6. System validiert die Eingaben.<br>7. System persistiert die Änderungen. |
| **Alternative Szenarien** | **Nutzer verlässt das Formular ohne Speichern:** Änderungen werden nicht persistiert. |
| **Ausnahmeszenarien** | **Zugriff auf fremde Aufgabe:** System verweigert den Zugriff.<br><br>**Validierungsfehler:** Änderungen werden nicht gespeichert. |
| **Qualitäten** | NFR-12-03 (Ownership). |

---

### UC-06 — Aufgabe löschen

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-06 |
| **Name** | Aufgabe löschen |
| **Beschreibung** | Studierender entfernt eine eigene Aufgabe aus dem System. |
| **Auslöser** | Nutzer wählt „Löschen“ bei einer Aufgabe. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Nutzer ist authentifiziert; Aufgabe existiert und gehört dem Nutzer. |
| **Nachbedingung** | Aufgabe wurde gelöscht. Zugehörige abhängige Daten werden entsprechend der Persistenzregeln behandelt. |
| **Hauptszenario** | 1. Nutzer wählt „Löschen“.<br>2. System fordert eine Bestätigung an.<br>3. Nutzer bestätigt.<br>4. System prüft die Ownership.<br>5. System löscht die Aufgabe. |
| **Alternative Szenarien** | **Nutzer bricht die Bestätigung ab:** Keine Daten werden verändert. |
| **Ausnahmeszenarien** | **Zugriff auf fremde Aufgabe:** System verweigert den Zugriff. |
| **Qualitäten** | Die Löschung ist im aktuellen MVP dauerhaft. |

---

## F2.4 Überblick und Planung

### UC-07 — Dashboard anzeigen

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-07 |
| **Name** | Dashboard anzeigen |
| **Beschreibung** | Studierender erhält einen Überblick über seine Aufgaben, Fortschritte und anstehende Deadlines. |
| **Auslöser** | Nutzer öffnet das Dashboard. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Nutzer ist authentifiziert. |
| **Nachbedingung** | Kein dauerhafter Zustandswechsel. |
| **Hauptszenario** | 1. Nutzer öffnet das Dashboard.<br>2. System lädt die Aufgaben des authentifizierten Benutzers.<br>3. System stellt die relevanten Aufgabeninformationen bereit.<br>4. System zeigt Aufgabenkarten mit Fortschritt und Dringlichkeitsdarstellung.<br>5. Nutzer kann von dort zu weiteren Bereichen navigieren. |
| **Alternative Szenarien** | **Keine Aufgaben vorhanden:** Das Dashboard zeigt einen geeigneten Leerzustand beziehungsweise eine Möglichkeit zum Anlegen einer neuen Aufgabe.<br><br>**Nutzer wählt eine Aufgabe:** Navigation zur Detailansicht. |
| **Ausnahmeszenarien** | Fehler beim Laden der Daten werden dem Nutzer angezeigt. |
| **Qualitäten** | NFR-11-01 (Dashboard-Ladezeit); NFR-15-01 (Responsive Nutzung). |

---

### UC-08 — Lernplan anzeigen

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-08 |
| **Name** | Lernplan anzeigen |
| **Beschreibung** | Studierender sieht den automatisch berechneten Lernplan mit geplanten Aufgaben und empfohlenen Lernzeiten. |
| **Auslöser** | Nutzer navigiert zum Lernplan. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Nutzer ist authentifiziert. |
| **Nachbedingung** | Kein dauerhafter Zustandswechsel durch die Anzeige. |
| **Hauptszenario** | 1. Nutzer öffnet die Lernplanansicht.<br>2. System berechnet beziehungsweise lädt die für den Benutzer relevanten Planinformationen.<br>3. System zeigt die Planinformationen nach Tagen.<br>4. Nutzer kann zwischen den dargestellten Wochen navigieren. |
| **Alternative Szenarien** | **Keine offenen Aufgaben:** System zeigt einen geeigneten Leerzustand. |
| **Ausnahmeszenarien** | Fehler bei der Planberechnung oder beim Laden der Planinformationen werden dem Nutzer angezeigt. |
| **Qualitäten** | NFR-11-03 (Berechnungszeit); F3 (Lernplanberechnung). |

---

## F2.5 Tracking

### UC-09 — Lernfortschritt eintragen

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-09 |
| **Name** | Lernfortschritt eintragen |
| **Beschreibung** | Studierender aktualisiert den Fortschritt einer eigenen Aufgabe und kann zusätzlich eine Lernsession erfassen. |
| **Auslöser** | Nutzer öffnet eine Aufgabe und ändert den Fortschritt. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Nutzer ist authentifiziert; Aufgabe existiert und gehört dem Nutzer. |
| **Nachbedingung (Erfolg)** | Fortschritt wurde gespeichert; der Status der Aufgabe wird entsprechend des Fortschritts aktualisiert. Eine optional erfasste Lernsession wird ebenfalls gespeichert. |
| **Nachbedingung (Misserfolg)** | Ursprünglicher Fortschritt bleibt unverändert. |
| **Hauptszenario** | 1. Nutzer öffnet die Aufgabendetailansicht.<br>2. Nutzer gibt einen Fortschritt zwischen 0 und 100 Prozent ein.<br>3. Optional gibt der Nutzer eine Lerndauer und Notiz ein.<br>4. Nutzer speichert.<br>5. System validiert die Eingaben.<br>6. System speichert den Fortschritt und gegebenenfalls die Lernsession.<br>7. Der Aufgabenstatus wird entsprechend des Fortschritts bestimmt. |
| **Alternative Szenarien** | **Fortschritt wird verändert:** Der neue gültige Fortschritt wird gespeichert und der Status entsprechend angepasst. |
| **Ausnahmeszenarien** | **Ungültige Lerndauer:** Die Eingabe wird zurückgewiesen und keine ungültige Lernsession gespeichert.<br><br>**Zugriff auf fremde Aufgabe:** System verweigert den Zugriff. |
| **Qualitäten** | NFR-12-03 (Ownership); Validierung der Fortschritts- und Sessiondaten. |

---

## F2.6 Einstellungen

### UC-10 — Erinnerung konfigurieren

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-10 |
| **Name** | Erinnerung konfigurieren |
| **Beschreibung** | Studierender konfiguriert eine Erinnerung zu einer eigenen Aufgabe. |
| **Auslöser** | Nutzer verwendet die vorgesehene Funktion zur Konfiguration einer Erinnerung. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Nutzer ist authentifiziert; die erforderliche Aufgabe existiert und gehört dem Nutzer. |
| **Nachbedingung (Erfolg)** | Erinnerungsdaten wurden gespeichert. |
| **Nachbedingung (Misserfolg)** | Keine Änderung an den bisherigen Erinnerungsdaten. |
| **Hauptszenario** | 1. Nutzer öffnet die Erinnerungskonfiguration.<br>2. Nutzer gibt die erforderlichen Erinnerungsparameter ein.<br>3. Nutzer wählt einen verfügbaren Kanal aus `ReminderChannel`.<br>4. Nutzer speichert die Konfiguration.<br>5. System validiert und speichert die Erinnerungsdaten. |
| **Alternative Szenarien** | **Erinnerung wird deaktiviert:** Die Erinnerung wird entsprechend der Konfiguration deaktiviert. |
| **Ausnahmeszenarien** | **Ungültige Konfiguration:** System weist die Eingabe zurück und zeigt eine Fehlermeldung an. |
| **Qualitäten** | Validierung der Erinnerungsdaten; Ownership der zugehörigen Aufgabe. |

---

## F2.7 Export

### UC-11 — Kalenderexport

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-11 |
| **Name** | Kalenderexport |
| **Beschreibung** | Studierender exportiert seine Aufgaben als `.ics`-Datei zum anschließenden Import in eine externe Kalenderanwendung. |
| **Auslöser** | Nutzer startet den Kalenderexport. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Nutzer ist authentifiziert. |
| **Nachbedingung** | Eine `.ics`-Datei wird zum Download bereitgestellt; im StudyPlanner wird dadurch kein dauerhafter Zustand verändert. |
| **Hauptszenario** | 1. Nutzer startet den Export.<br>2. System erzeugt eine iCalendar-Datei aus den Aufgaben des authentifizierten Benutzers.<br>3. System stellt die Datei zum Download bereit.<br>4. Nutzer kann die Datei anschließend manuell in eine Kalenderanwendung importieren. |
| **Alternative Szenarien** | **Keine exportierbaren Aufgaben:** System erzeugt beziehungsweise liefert eine entsprechende gültige Kalenderdatei. |
| **Ausnahmeszenarien** | Fehler bei der Erstellung oder Bereitstellung der Datei werden dem Nutzer angezeigt. |
| **Qualitäten** | Verwendung des iCalendar-Formats (`.ics`). |

---

## F2.8 Außerhalb des Scope von F2

Folgende Funktionen beziehungsweise technische Konzepte sind keine eigenständigen Use Cases, da sie keinen eigenen Nutzer-Zielzustand mit einem separaten Interaktionsszenario darstellen:

- **JWT-Validierung und Ownership-Prüfung:** Querschnittliche Sicherheitskonzepte, siehe N2 und Architektur Kapitel 8.
- **Lernplan-Berechnung:** Serverseitige Geschäftslogik, die im Zusammenhang mit UC-08 verwendet wird.
- **Datenbankmigrationen:** Technischer Betriebs- und Entwicklungsprozess, kein Benutzer-Use-Case.
- **Passwort zurücksetzen:** Nicht Bestandteil des aktuellen MVP-Umfangs.

Ein eigenständiger zeitgesteuerter Reminder-Batch beziehungsweise ein verpflichtender täglicher Hintergrundjob ist im aktuellen Implementierungsstand kein eigenständiger Use Case.

---

## F2.9 Querverweise

| Baustein | Relevanz für F2 |
|----------|-----------------|
| F1 | Die in F1 beschriebenen Geschäftsprozesse werden durch die Use Cases der Aufgabenverwaltung, Planung und des Trackings unterstützt. |
| F3 | Serverseitige Anwendungsfunktionen wie Lernplanberechnung und weitere Geschäftslogik unterstützen mehrere Use Cases. |
| D1 | Die Entitäten `USER`, `TASK`, `SUBTASK`, `LEARNING_SESSION` und `REMINDER` werden durch die entsprechenden Use Cases verwendet. |
| D2 | `TaskType`, `TaskStatus`, `UrgencyLevel` und `ReminderChannel` werden für die fachliche Verarbeitung und Darstellung verwendet. |
| B1 | Die Screens und Dialoge konkretisieren die Benutzerinteraktion der Use Cases. |
| S1 | Die REST-API-Endpunkte realisieren die serverseitigen Operationen der Use Cases. |
| N1 | Die nichtfunktionalen Anforderungen definieren Qualitätsanforderungen unter anderem für Authentifizierung, Ownership, Validierung und Responsive Design. |
| N2 | Authentifizierung und Autorisierung bilden querschnittliche Voraussetzungen für die geschützten Use Cases. |