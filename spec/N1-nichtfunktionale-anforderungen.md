# N1 — Nichtfunktionale Anforderungen

Nichtfunktionale Anforderungen nach ISO 25010. Jede Anforderung trägt eine stabile ID (NFR-xx-yy), die aus der Architektur und dem Code referenziert werden kann.

---

## N1.1 Performance und Effizienz

| ID | Anforderung | Messkriterium |
|----|-------------|--------------|
| NFR-11-01 | Dashboard lädt in unter 2 Sekunden. | Browser-Netzwerkpanel; gemessen ab erstem Byte bis vollständigem Render; LAN-Verbindung. |
| NFR-11-02 | REST-API antwortet auf Standard-Requests in < 500 ms. | `curl`-Messung; 95. Perzentil; ohne externe API-Calls. |
| NFR-11-03 | Lernplan-Berechnung (AF-01) dauert serverseitig < 1 Sekunde für bis zu 50 Aufgaben je Nutzer. | Unit-Test mit Zeitmessung. |

---

## N1.2 Sicherheit

| ID | Anforderung | Maßnahme |
|----|-------------|---------|
| NFR-12-01 | Passwörter dürfen niemals im Klartext gespeichert werden. | bcrypt mit Kostenfaktor 12 (AF-04). |
| NFR-12-02 | Alle API-Endpunkte außer `/auth/*` erfordern einen gültigen JWT. | Spring Security `JwtAuthenticationFilter` (AF-06). |
| NFR-12-03 | Nutzer darf ausschließlich eigene Ressourcen lesen und verändern. | Ownership-Prüfung in der Service-Schicht (AF-07); HTTP 403 bei Verletzung. |
| NFR-12-04 | API-Keys, Passwörter und Secrets dürfen nicht ins Repository. | `.env` in `.gitignore`; `.env.example` enthält nur Platzhalter (CON-06). |
| NFR-12-05 | Fehlermeldung bei Login unterscheidet nicht zwischen falscher E-Mail und falschem Passwort. | Generische Fehlermeldung in UC-02 (kein Information-Leakage). |

---

## N1.3 Zuverlässigkeit

| ID | Anforderung | Maßnahme |
|----|-------------|---------|
| NFR-13-01 | Anwendung überlebt Neustart der Datenbank ohne Datenverlust. | PostgreSQL-Daten in Docker Volume; Spring DataSource-Reconnect. |
| NFR-13-02 | Fehlgeschlagene API-Requests führen niemals zu leeren Bildschirmen. Validierungs- und Client-Fehler (4xx) zeigen dem Nutzer eine verständliche Meldung. Server-Fehler (5xx) zeigen eine generische Meldung ohne interne Details — aus Sicherheitsgründen bewusst nicht differenziert. | Globaler `@ControllerAdvice`-Handler im Backend (N2 Fehlerbehandlung); Frontend Axios-Interceptor. |
| NFR-13-03 | Fehler in AF-10 (Scheduler) blockieren nicht den Normalbetrieb. | Exceptions im Scheduler werden geloggt und verschluckt; kein Absturz. |

---

## N1.4 Wartbarkeit

| ID | Anforderung | Maßnahme |
|----|-------------|---------|
| NFR-14-01 | Codebase folgt einheitlicher Formatierung. | Checkstyle (Backend); ESLint + Prettier (Frontend). |
| NFR-14-02 | Service-Schicht im Backend ist durch Unit-Tests abgedeckt. | JUnit 5 + Mockito; Ziel: ≥ 60 % Line Coverage der Service-Klassen. |
| NFR-14-03 | Wesentliche Architekturentscheidungen sind als ADRs dokumentiert. | Mindestens 3–5 ADRs in `docs/arch/` (Programmiersprache, Persistenz, Auth, Frontend, Deployment). |
| NFR-14-04 | Typen aus D2 sind im Code identisch benannt. | Klassen/Interfaces im Backend und TypeScript-Interfaces im Frontend tragen dieselben Namen wie in D2. |

---

## N1.5 Benutzbarkeit

| ID | Anforderung | Maßnahme |
|----|-------------|---------|
| NFR-15-01 | Anwendung ist auf Desktop (≥ 1024 px) und Mobilgerät (≥ 375 px) nutzbar. | Responsive Design; CSS Flexbox/Grid; manuelle Tests auf beiden Viewports. |
| NFR-15-02 | Fehleingaben werden direkt am Formularfeld gemeldet — kein generischer Fehler nach dem Absenden. | Client-seitige Validierung vor dem API-Call (UC-04, UC-05); Inline-Fehlermeldungen. |
| NFR-15-03 | Farbgebung erfüllt WCAG AA (Kontrastverhältnis ≥ 4,5:1). | Farbpalette mit Kontrast-Check; insbesondere Ampelfarben (Rot, Gelb, Grün) auf weißem Hintergrund. |

---

## N1.6 Portierbarkeit und Betrieb

| ID | Anforderung | Maßnahme |
|----|-------------|---------|
| NFR-16-01 | Anwendung startet mit `docker compose up --build` auf Linux, macOS und Windows ohne zusätzliche Schritte. | Docker Compose; alle Services containerisiert; getestet auf allen drei Plattformen. |
| NFR-16-02 | Kein manueller Konfigurationsschritt außer `cp .env.example .env`. | Sinnvolle Defaults in `.env.example`; Datenbankschema per Flyway automatisch angelegt. |
