# F2 — Anwendungsfälle

Use Cases im Sinne von Siedersleben (Kap. 4.4): konkrete Interaktionsszenarien zwischen einem Studierenden und dem System, die jeweils ein einzelnes, für den Nutzer bedeutsames Ziel verfolgen und in einem stabilen Zustand enden. F2 ist die **systemunterstützte Teilmenge** der in F1 beschriebenen Geschäftsprozesse: jede Aktivität in F1, die eine Nutzerinteraktion mit dem Study Planer beinhaltet, erscheint hier als Use Case.

Jeder Use Case folgt dem Spezifikationstemplate nach Pohl & Rupp (2021), *Basiswissen Requirements Engineering*, 5. Aufl. Die Nummerierung ist stabil; einmal referenzierte UC-IDs werden nicht umbenannt.

---

## F2.1 Use-Case-Index

| ID | Use Case | Gruppe | Bezug zu F1 | Status |
|----|----------|--------|-------------|--------|
| [UC-01](#uc-01--registrieren) | Registrieren | Zugang | — (einmalige Einrichtung) | ✅ |
| [UC-02](#uc-02--anmelden) | Anmelden | Zugang | GP-01 Vorbedingung | ✅ |
| [UC-03](#uc-03--abmelden) | Abmelden | Zugang | — (Nachbedingung) | ✅ |
| [UC-04](#uc-04--aufgabe-anlegen) | Aufgabe anlegen | Aufgabenverwaltung | GP-01 A2 | ✅ |
| [UC-05](#uc-05--aufgabe-bearbeiten) | Aufgabe bearbeiten | Aufgabenverwaltung | GP-02 | ✅ |
| [UC-06](#uc-06--aufgabe-löschen) | Aufgabe löschen | Aufgabenverwaltung | GP-02 | ✅ |
| [UC-07](#uc-07--dashboard-anzeigen) | Dashboard anzeigen | Überblick | GP-02 A1 | ✅ |
| [UC-08](#uc-08--lernplan-anzeigen) | Lernplan anzeigen | Planung | GP-02 A3 | ✅ |
| [UC-09](#uc-09--lernfortschritt-eintragen) | Lernfortschritt eintragen | Tracking | GP-02 A4, GP-03 A4 | ✅ |
| [UC-10](#uc-10--erinnerung-konfigurieren) | Erinnerung konfigurieren | Einstellungen | — | ✅ |
| [UC-11](#uc-11--kalenderexport) | Kalenderexport | Export | — | ✅ |

Status-Legende: ✅ fertig · 🚧 in Arbeit · ⬜ offen.

---

## F2.2 Zugang

### UC-01 — Registrieren

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-01 |
| **Name** | Registrieren |
| **Beschreibung** | Ein neuer Studierender legt ein Nutzerkonto an und erhält direkt im Anschluss eine aktive Sitzung. |
| **Auslöser** | Nutzer öffnet die Anwendung ohne Konto und wählt „Registrieren". |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Die E-Mail-Adresse ist noch nicht im System registriert. |
| **Nachbedingung (Erfolg)** | Nutzerkonto angelegt; authentifizierte Sitzung aktiv; Nutzer auf Dashboard weitergeleitet. |
| **Nachbedingung (Misserfolg)** | Kein Konto angelegt; Fehlermeldung angezeigt. |
| **Hauptszenario** | 1. Nutzer öffnet das Registrierungsformular.<br> 2. Nutzer gibt Benutzername, E-Mail-Adresse und Passwort ein. <br>3. System validiert Eingaben (E-Mail-Format; Passwort ≥ 8 Zeichen).<br> 4. System prüft, ob die E-Mail-Adresse bereits existiert. <br>5. System speichert das Konto (Passwort als bcrypt-Hash).<br> 6. System stellt einen JWT aus und leitet den Nutzer auf das Dashboard weiter. |
| **Alternative Szenarien** | — |
| **Ausnahmeszenarien** | *E-Mail bereits vergeben:* System zeigt Fehlermeldung; kein Konto angelegt; Nutzer kann eine andere Adresse eingeben. *Validierungsfehler:* betroffene Felder werden markiert; kein Request abgesendet. |
| **Qualitäten** | NFR-12-1 (Passwort-Hash); NFR-15-2 (Inline-Validierung). |

---

### UC-02 — Anmelden

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-02 |
| **Name** | Anmelden |
| **Beschreibung** | Ein registrierter Studierender authentifiziert sich und erhält eine gültige Sitzung für alle weiteren Use Cases. |
| **Auslöser** | Nutzer öffnet die Anwendung ohne aktive Sitzung. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Nutzerkonto existiert. |
| **Nachbedingung (Erfolg)** | JWT im Client gespeichert; Nutzer auf Dashboard. |
| **Nachbedingung (Misserfolg)** | Keine Sitzung; generische Fehlermeldung angezeigt. |
| **Hauptszenario** | 1. System zeigt Login-Formular.<br> 2. Nutzer gibt E-Mail und Passwort ein.<br> 3. System prüft Credentials gegen Datenbank. <br>4. System stellt JWT aus (Gültigkeit: 24 h).<br> 5. Client speichert JWT. <br>6. Weiterleitung auf Dashboard. |
| **Alternative Szenarien** | — |
| **Ausnahmeszenarien** | *Falsche Credentials:* generische Fehlermeldung ohne Hinweis, ob E-Mail oder Passwort falsch ist. *Konto existiert nicht:* gleiche generische Meldung (kein Information-Leakage). |
| **Qualitäten** | NFR-12-02 (JWT-Validierung); NFR-12-05 (keine differenzierte Fehlermeldung bei falschen Credentials). |

---

### UC-03 — Abmelden

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-03 |
| **Name** | Abmelden |
| **Beschreibung** | Nutzer beendet die aktive Sitzung. |
| **Auslöser** | Nutzer klickt „Abmelden". |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Authentifizierte Sitzung aktiv. |
| **Nachbedingung** | JWT im Client gelöscht; Nutzer auf Login-Seite weitergeleitet. |
| **Hauptszenario** | 1. Nutzer klickt „Abmelden". <br>2. System löscht JWT im Client.<br> 3. Weiterleitung auf Login-Seite. |
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
| **Beschreibung** | Studierender erfasst eine neue Aufgabe (Prüfung, Abgabe oder Lernziel); das System berechnet den Lernplan neu. |
| **Auslöser** | Nutzer klickt „Neue Aufgabe" auf Dashboard oder Aufgabenliste. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Authentifizierte Sitzung. |
| **Nachbedingung (Erfolg)** | Aufgabe gespeichert; Lernplan neu berechnet; Aufgabe erscheint sofort im Dashboard. |
| **Nachbedingung (Misserfolg)** | Keine Aufgabe angelegt; Validierungsfehler angezeigt. |
| **Hauptszenario** | 1. Nutzer öffnet Aufgaben-Anlegen-Formular.<br> 2. Nutzer wählt Aufgabentyp (EXAM / ASSIGNMENT / GOAL). <br>3. System zeigt typ-spezifische Felder (vgl. D2).<br> 4. Nutzer füllt Pflichtfelder aus (mindestens: Titel, Deadline). <br>5. Nutzer speichert.<br> 6. System validiert Eingaben gegen das Typ-Schema.<br> 7. System persistiert Aufgabe und berechnet Lernplan neu. <br>8. Nutzer wird auf Dashboard oder Aufgabenliste weitergeleitet. |
| **Alternative Szenarien** | *Nutzer bricht ab:* nichts wird gespeichert. |
| **Ausnahmeszenarien** | *Pflichtfeld leer:* betroffenes Feld wird markiert; kein Speichern möglich. *Datum in der Vergangenheit:* Warnung wird angezeigt, Speichern aber erlaubt (weiche Regel). |
| **Qualitäten** | NFR-15-2 (Inline-Validierung); F-03-03 (Plan-Neuberechnung). |

---

### UC-05 — Aufgabe bearbeiten

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-05 |
| **Name** | Aufgabe bearbeiten |
| **Beschreibung** | Studierender ändert Felder einer bestehenden Aufgabe; das System berechnet den Lernplan neu. |
| **Auslöser** | Nutzer öffnet eine Aufgabe und klickt „Bearbeiten". |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Authentifizierte Sitzung; Aufgabe existiert und gehört dem Nutzer. |
| **Nachbedingung (Erfolg)** | Geänderte Felder persistiert; Lernplan neu berechnet. |
| **Nachbedingung (Misserfolg)** | Ursprüngliche Daten unverändert; Validierungsfehler angezeigt. |
| **Hauptszenario** | 1. Nutzer öffnet Aufgabendetailansicht. <br>2. Nutzer klickt „Bearbeiten". <br>3. Formular wird mit bestehenden Werten befüllt.<br> 4. Nutzer ändert Felder.<br> 5. Nutzer speichert.<br> 6. System validiert und persistiert Änderungen. <br>7. System berechnet Lernplan neu. |
| **Alternative Szenarien** | *Nutzer verlässt Formular ohne Speichern:* Änderungen werden verworfen. |
| **Ausnahmeszenarien** | *Zugriff auf fremde Aufgabe:* HTTP 403; Nutzer wird auf eigene Aufgabenliste weitergeleitet. *Validierungsfehler:* betroffene Felder markiert; kein Speichern. |
| **Qualitäten** | NFR-12-3 (Ownership-Prüfung); F-03-03 (Plan-Neuberechnung). |

---

### UC-06 — Aufgabe löschen

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-06 |
| **Name** | Aufgabe löschen |
| **Beschreibung** | Studierender entfernt eine Aufgabe unwiderruflich aus dem System. |
| **Auslöser** | Nutzer wählt „Löschen" auf einer Aufgabe. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Authentifizierte Sitzung; Aufgabe existiert und gehört dem Nutzer. |
| **Nachbedingung** | Aufgabe und zugehörige Lernsessions aus Datenbank entfernt; Lernplan neu berechnet. |
| **Hauptszenario** | 1. Nutzer klickt „Löschen".<br> 2. System zeigt Bestätigungsdialog (irreversible Aktion). <br>3. Nutzer bestätigt.<br> 4. System löscht Aufgabe inkl. Subtasks und Learning Sessions. <br>5. Lernplan wird neu berechnet. |
| **Alternative Szenarien** | *Nutzer bricht Bestätigung ab:* nichts verändert sich. |
| **Ausnahmeszenarien** | *Zugriff auf fremde Aufgabe:* HTTP 403. |
| **Qualitäten** | Kein Soft-Delete im MVP; Löschung ist permanent (P1, NG-07). |

---

## F2.4 Überblick und Planung

### UC-07 — Dashboard anzeigen

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-07 |
| **Name** | Dashboard anzeigen |
| **Beschreibung** | Studierender erhält einen Sofortüberblick über dringende Aufgaben, Fortschritt und anstehende Deadlines. |
| **Auslöser** | Nutzer öffnet die Anwendung nach dem Login oder navigiert zu `/`. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Authentifizierte Sitzung. |
| **Nachbedingung** | Kein Zustandswechsel. |
| **Hauptszenario** | 1. Nutzer öffnet Dashboard. <br>2. System lädt alle offenen Aufgaben des Nutzers.<br> 3. System berechnet Dringlichkeit je Aufgabe (UrgencyLevel: RED / YELLOW / GREEN).<br> 4. System zeigt Aufgabenkarten sortiert nach Deadline, mit Ampelfarbe und Fortschrittsbalken.<br> 5. System zeigt Zusammenfassung: „X Aufgaben diese Woche". |
| **Alternative Szenarien** | *Nutzer hat keine Aufgaben:* leerer Zustand mit Hinweis „Neue Aufgabe anlegen". *Nutzer klickt auf Aufgabe:* navigiert zu Aufgabendetail. |
| **Ausnahmeszenarien** | — |
| **Qualitäten** | NFR-11-1 (Ladezeit < 2 s); NFR-15-1 (Responsive Design). Ampelregel: RED ≤ 3 Tage, YELLOW ≤ 7 Tage, GREEN > 7 Tage. |

---

### UC-08 — Lernplan anzeigen

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-08 |
| **Name** | Lernplan anzeigen |
| **Beschreibung** | Studierender sieht den automatisch generierten Wochenlernplan mit Aufgaben und empfohlenen täglichen Lernzeiten. |
| **Auslöser** | Nutzer navigiert zu „Mein Lernplan". |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Authentifizierte Sitzung; mindestens eine offene Aufgabe vorhanden. |
| **Nachbedingung** | Kein Zustandswechsel. |
| **Hauptszenario** | 1. Nutzer öffnet Lernplan-Ansicht.<br> 2. System berechnet aktuellen Plan (Priorisierungsalgorithmus, vgl. D2 `LearningPlanDTO`). <br>3. System zeigt Wochenansicht: je Tag eine Spalte mit empfohlenen Aufgaben und Minutenangabe.<br> 4. Nutzer kann mit Vor-/Zurück-Navigation zwischen Kalenderwochen wechseln. |
| **Alternative Szenarien** | *Keine offenen Aufgaben:* leerer Zustand mit Hinweis. *Nutzer klickt „Plan neu berechnen":* System ruft `POST /api/v1/plan/recalculate` auf und aktualisiert die Ansicht. |
| **Ausnahmeszenarien** | — |
| **Qualitäten** | NFR-11-3 (Berechnung < 1 s); F-03-01 (Priorisierungsalgorithmus). |

---

## F2.5 Tracking

### UC-09 — Lernfortschritt eintragen

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-09 |
| **Name** | Lernfortschritt eintragen |
| **Beschreibung** | Studierender aktualisiert den Fortschritt einer Aufgabe und protokolliert optional eine Lernsession; das System berechnet den Lernplan neu. |
| **Auslöser** | Nutzer öffnet eine Aufgabe und trägt Fortschritt ein. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Authentifizierte Sitzung; Aufgabe im Status OPEN oder IN_PROGRESS. |
| **Nachbedingung (Erfolg)** | Fortschritt persistiert; Status ggf. automatisch auf IN_PROGRESS oder DONE gesetzt; Lernplan neu berechnet. |
| **Nachbedingung (Misserfolg)** | Ursprünglicher Fortschritt unverändert. |
| **Hauptszenario** | 1. Nutzer öffnet Aufgabendetailansicht. <br>2. Nutzer bewegt Fortschritts-Schieberegler (0–100 %).<br> 3. Optional: Nutzer trägt Lerndauer (Minuten) und Notiz ein.<br> 4. Nutzer speichert. <br>5. System persistiert Fortschritt und ggf. neue LearningSession.<br> 6. System aktualisiert TaskStatus automatisch aus progressPercent. <br>7. System berechnet Lernplan neu. |
| **Alternative Szenarien** | *Fortschritt = 100 %:* Aufgabe wird automatisch als DONE markiert; erscheint auf Dashboard ausgegraut. |
| **Ausnahmeszenarien** | *Ungültige Minutenangabe (≤ 0):* Validierungsfehler; keine Session gespeichert. |
| **Qualitäten** | AF-09 (LearningSession persistieren); Statusübergang folgt dynamisch dem progressPercent — Änderungen in beide Richtungen erlaubt (vgl. D2 TaskStatus). |

---

## F2.6 Einstellungen

### UC-10 — Erinnerung konfigurieren

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-10 |
| **Name** | Erinnerung konfigurieren |
| **Beschreibung** | Studierender legt fest, wann und auf welchem Kanal er an eine bevorstehende Deadline erinnert werden möchte. |
| **Auslöser** | Nutzer öffnet Einstellungen oder Aufgabendetail und wählt „Erinnerung". |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Authentifizierte Sitzung; Aufgabe existiert. |
| **Nachbedingung (Erfolg)** | Erinnerungsregel gespeichert. |
| **Nachbedingung (Misserfolg)** | Keine Änderung. |
| **Hauptszenario** | 1. Nutzer öffnet Erinnerungskonfiguration. <br>2. Nutzer wählt Vorlaufzeit (Tage vor Deadline).<br> 3. Nutzer wählt Kanal (ReminderChannel: IN_APP / EMAIL / BOTH).<br> 4. Nutzer speichert.<br> 5. System persistiert Reminder-Eintrag. |
| **Alternative Szenarien** | *Nutzer deaktiviert Erinnerung:* `active = false` gesetzt; keine Benachrichtigung mehr. |
| **Ausnahmeszenarien** | *E-Mail-Kanal gewählt, aber SMTP nicht konfiguriert:* System zeigt Hinweis; speichert Regel trotzdem; E-Mail-Versand bleibt stumm bis SMTP konfiguriert ist. |
| **Qualitäten** | F-06-01 bis F-06-04; NFR-12-4 (keine Secrets im Repo). |

---

## F2.7 Export

### UC-11 — Kalenderexport

| Abschnitt | Inhalt |
|-----------|--------|
| **Bezeichner** | UC-11 |
| **Name** | Kalenderexport |
| **Beschreibung** | Studierender exportiert alle offenen Aufgaben als `.ics`-Datei zum Import in einen externen Kalender. |
| **Auslöser** | Nutzer klickt „Als iCal exportieren" in den Einstellungen. |
| **Akteure** | Studierender (primär). |
| **Vorbedingung** | Authentifizierte Sitzung; mindestens eine Aufgabe vorhanden. |
| **Nachbedingung** | `.ics`-Datei wird vom Browser heruntergeladen; kein Zustandswechsel im System. |
| **Hauptszenario** | 1. Nutzer klickt Export-Button.<br> 2. System generiert iCalendar-Datei (RFC 5545): VEVENT je Aufgabe mit DTSTART = Deadline, SUMMARY = Titel, DESCRIPTION = Beschreibung.<br> 3. Browser lädt Datei herunter. |
| **Alternative Szenarien** | *Keine Aufgaben vorhanden:* leere, aber valide `.ics`-Datei wird heruntergeladen. |
| **Ausnahmeszenarien** | — |
| **Qualitäten** | Kompatibel mit Google Calendar, Apple Calendar, Thunderbird (RFC 5545); generiert durch AF-12. |

---

## F2.8 Außerhalb des Scope von F2

- **JWT-Validierung und Ownership-Prüfung.** Querschnittskonzepte ohne Nutzer-Entscheidungspunkt — siehe N2.
- **Lernplan-Berechnung.** Serverseitige Funktion ohne eigenen UC — siehe F3, AF-03.
- **Erinnerungs-Scheduler.** Hintergrundjob ohne Nutzerinteraktion — siehe B2, F-06-02.
- **Passwort zurücksetzen.** Nicht in MVP (P1, NG-08 implizit durch Scope-Beschränkung).

---

## F2.9 Querverweise

| Baustein | Relevanz für F2 |
|----------|----------------|
| F1 | Aktivitäten in GP-01 bis GP-03 werden durch UC-04 bis UC-09 realisiert. |
| F3 | AF-01 (Lernplan-Berechnung) läuft in UC-04, UC-05, UC-06, UC-09; AF-02 (Ampel) in UC-07. |
| D1 | Entitäten Task, LearningSession, Reminder werden durch UC-04–UC-10 gelesen und geschrieben. |
| D2 | Statusmaschine TaskStatus (OPEN→IN_PROGRESS→DONE) steuert UC-09; UrgencyLevel steuert UC-07. |
| B1 | Screen-Designs und Dialogfluss für jeden UC. |
| S1 | REST-API-Endpunkte, die die UCs serverseitig realisieren. |
| N1 | NFR-11-01 (Ladezeit Dashboard, UC-07); NFR-12-02 (JWT, UC-02); NFR-12-05 (Login-Fehlermeldung, UC-02); NFR-15-02 (Validierung, UC-04/05). |
| N2 | Auth-Querschnittskonzept gilt als Vorbedingung für UC-02 bis UC-11. |
