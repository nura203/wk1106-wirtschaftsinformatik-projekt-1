# N1 — Nichtfunktionale Anforderungen

Nichtfunktionale Anforderungen des Study Planners. Jede Anforderung trägt eine stabile ID (NFR-xx-yy), die aus der Architektur und dem Code referenziert werden kann.

---

## N1.1 Performance und Effizienz

| ID | Anforderung | Messkriterium |
|----|-------------|---------------|
| NFR-11-01 | Das Dashboard soll unter den definierten Testbedingungen in weniger als 2 Sekunden geladen und dargestellt werden. | Messung über die Browser-Entwicklertools beziehungsweise das Netzwerk- und Performance-Panel. |
| NFR-11-02 | Die REST-API soll bei Standard-Requests unter den definierten Testbedingungen eine Antwortzeit von weniger als 500 ms erreichen. | Messung definierter API-Aufrufe, beispielsweise über `curl` oder ein vergleichbares Testwerkzeug. Externe Dienste werden bei der Messung nicht berücksichtigt. |
| NFR-11-03 | Die Lernplan-Berechnung für bis zu 50 Aufgaben eines Benutzers soll unter den definierten Testbedingungen weniger als 1 Sekunde benötigen. | Messung beziehungsweise automatisierter Test mit einer definierten Anzahl von Testaufgaben. |

---

## N1.2 Sicherheit

| ID | Anforderung | Maßnahme |
|----|-------------|----------|
| NFR-12-01 | Passwörter dürfen niemals im Klartext gespeichert werden. | Speicherung ausschließlich als BCrypt-Hash mit dem im System konfigurierten Kostenfaktor. |
| NFR-12-02 | Geschützte API-Endpunkte müssen einen gültigen JWT voraussetzen. | Spring Security und `JwtAuthenticationFilter`; Registrierungs- und Login-Endpunkte sind öffentlich erreichbar. |
| NFR-12-03 | Ein Benutzer darf ausschließlich auf seine eigenen geschützten Ressourcen zugreifen. | Ownership-Prüfung in der Service-Schicht; bei unberechtigtem Zugriff wird der Zugriff verweigert. |
| NFR-12-04 | API-Keys, Passwörter und sonstige Secrets dürfen nicht in das Repository gelangen. | `.env` wird über `.gitignore` ausgeschlossen; `.env.example` enthält keine echten Secrets; sensible Konfiguration wird über Umgebungsvariablen bereitgestellt. |
| NFR-12-05 | Fehlgeschlagene Login-Vorgänge sollen keine unnötigen Informationen über vorhandene Benutzerkonten preisgeben. | Generische Fehlermeldung bei fehlgeschlagener Anmeldung. |

---

## N1.3 Zuverlässigkeit

| ID | Anforderung | Maßnahme |
|----|-------------|----------|
| NFR-13-01 | Ein Neustart des PostgreSQL-Containers soll nicht zum Verlust der persistent gespeicherten Daten führen. | PostgreSQL verwendet ein persistentes Docker Volume. |
| NFR-13-02 | Fehlgeschlagene API-Requests sollen zu verständlichen Fehlermeldungen führen und nicht zu unverständlichen oder leeren Zuständen. | Zentraler `@RestControllerAdvice`-Handler im Backend sowie Fehlerbehandlung im Frontend. |

---

## N1.4 Wartbarkeit

| ID | Anforderung | Maßnahme |
|----|-------------|----------|
| NFR-14-01 | Die Codebasis soll einheitlich formatiert und automatisiert überprüfbar sein. | Verwendung der im Projekt eingerichteten Formatierungs- und Prüfwerkzeuge für Backend und Frontend. |
| NFR-14-02 | Die wesentliche Service-Schicht des Backends soll durch automatisierte Tests abgedeckt werden. | JUnit 5 und Mockito; die vorhandenen automatisierten Tests prüfen insbesondere die fachliche Logik der Service-Schicht. |
| NFR-14-03 | Wesentliche Architekturentscheidungen sollen als ADRs dokumentiert werden. | ADRs in `arch/09-architekturentscheidungen.md`. |
| NFR-14-04 | Die in D2 definierten fachlichen Typen sollen in Backend und Frontend konsistent verwendet werden. | Einheitliche Benennung und fachliche Bedeutung der definierten Typen und DTOs. |

---

## N1.5 Benutzbarkeit

| ID | Anforderung | Maßnahme |
|----|-------------|----------|
| NFR-15-01 | Die Anwendung soll auf Desktop-Geräten ab 1024 px und mobilen Geräten ab 375 px nutzbar sein. | Responsive Design mit CSS; manuelle Prüfung auf unterschiedlichen Bildschirmgrößen. |
| NFR-15-02 | Fehleingaben sollen möglichst direkt am jeweiligen Formularfeld angezeigt werden. | Client-seitige Validierung und Inline-Fehlermeldungen insbesondere bei der Aufgabenverwaltung. |
| NFR-15-03 | Die Farbgebung soll die Anforderungen an WCAG AA hinsichtlich des Kontrasts unterstützen. | Kontrastprüfung der verwendeten Farbpalette; insbesondere Prüfung der Dringlichkeitsdarstellung. |

---

## N1.6 Portierbarkeit und Betrieb

| ID | Anforderung | Maßnahme |
|----|-------------|----------|
| NFR-16-01 | Die Anwendung soll mit Docker Compose reproduzierbar gestartet werden können. | Docker Compose; Frontend, Backend und PostgreSQL werden als Container bereitgestellt. |
| NFR-16-02 | Für den Betrieb sollen Java, Node.js und PostgreSQL nicht direkt auf dem Host installiert werden müssen. | Die benötigten Komponenten werden durch die Docker-Container bereitgestellt. |
| NFR-16-03 | Der Start der Anwendung soll mit einem dokumentierten Vorgehen möglich sein. | Die Installations- und Startanleitung befindet sich in `INSTALL.md`. |

Der vorgesehene Start erfolgt aus dem Verzeichnis `backend`:
# N1 — Nichtfunktionale Anforderungen

Nichtfunktionale Anforderungen des Study Planners. Jede Anforderung trägt eine stabile ID (NFR-xx-yy), die aus der Architektur und dem Code referenziert werden kann.

---

## N1.1 Performance und Effizienz

| ID | Anforderung | Messkriterium |
|----|-------------|---------------|
| NFR-11-01 | Das Dashboard soll unter den definierten Testbedingungen in weniger als 2 Sekunden geladen und dargestellt werden. | Messung über die Browser-Entwicklertools beziehungsweise das Netzwerk- und Performance-Panel. |
| NFR-11-02 | Die REST-API soll bei Standard-Requests unter den definierten Testbedingungen eine Antwortzeit von weniger als 500 ms erreichen. | Messung definierter API-Aufrufe, beispielsweise über `curl` oder ein vergleichbares Testwerkzeug. Externe Dienste werden bei der Messung nicht berücksichtigt. |
| NFR-11-03 | Die Lernplan-Berechnung für bis zu 50 Aufgaben eines Benutzers soll unter den definierten Testbedingungen weniger als 1 Sekunde benötigen. | Messung beziehungsweise automatisierter Test mit einer definierten Anzahl von Testaufgaben. |

---

## N1.2 Sicherheit

| ID | Anforderung | Maßnahme |
|----|-------------|----------|
| NFR-12-01 | Passwörter dürfen niemals im Klartext gespeichert werden. | Speicherung ausschließlich als BCrypt-Hash mit dem im System konfigurierten Kostenfaktor. |
| NFR-12-02 | Geschützte API-Endpunkte müssen einen gültigen JWT voraussetzen. | Spring Security und `JwtAuthenticationFilter`; Registrierungs- und Login-Endpunkte sind öffentlich erreichbar. |
| NFR-12-03 | Ein Benutzer darf ausschließlich auf seine eigenen geschützten Ressourcen zugreifen. | Ownership-Prüfung in der Service-Schicht; bei unberechtigtem Zugriff wird der Zugriff verweigert. |
| NFR-12-04 | API-Keys, Passwörter und sonstige Secrets dürfen nicht in das Repository gelangen. | `.env` wird über `.gitignore` ausgeschlossen; `.env.example` enthält keine echten Secrets; sensible Konfiguration wird über Umgebungsvariablen bereitgestellt. |
| NFR-12-05 | Fehlgeschlagene Login-Vorgänge sollen keine unnötigen Informationen über vorhandene Benutzerkonten preisgeben. | Generische Fehlermeldung bei fehlgeschlagener Anmeldung. |

---

## N1.3 Zuverlässigkeit

| ID | Anforderung | Maßnahme |
|----|-------------|----------|
| NFR-13-01 | Ein Neustart des PostgreSQL-Containers soll nicht zum Verlust der persistent gespeicherten Daten führen. | PostgreSQL verwendet ein persistentes Docker Volume. |
| NFR-13-02 | Fehlgeschlagene API-Requests sollen zu verständlichen Fehlermeldungen führen und nicht zu unverständlichen oder leeren Zuständen. | Zentraler `@RestControllerAdvice`-Handler im Backend sowie Fehlerbehandlung im Frontend. |

---

## N1.4 Wartbarkeit

| ID | Anforderung | Maßnahme |
|----|-------------|----------|
| NFR-14-01 | Die Codebasis soll einheitlich formatiert und automatisiert überprüfbar sein. | Verwendung der im Projekt eingerichteten Formatierungs- und Prüfwerkzeuge für Backend und Frontend. |
| NFR-14-02 | Die wesentliche Service-Schicht des Backends soll durch automatisierte Tests abgedeckt werden. | JUnit 5 und Mockito; die vorhandenen automatisierten Tests prüfen insbesondere die fachliche Logik der Service-Schicht. |
| NFR-14-03 | Wesentliche Architekturentscheidungen sollen als ADRs dokumentiert werden. | ADRs in `arch/09-architekturentscheidungen.md`. |
| NFR-14-04 | Die in D2 definierten fachlichen Typen sollen in Backend und Frontend konsistent verwendet werden. | Einheitliche Benennung und fachliche Bedeutung der definierten Typen und DTOs. |

---

## N1.5 Benutzbarkeit

| ID | Anforderung | Maßnahme |
|----|-------------|----------|
| NFR-15-01 | Die Anwendung soll auf Desktop-Geräten ab 1024 px und mobilen Geräten ab 375 px nutzbar sein. | Responsive Design mit CSS; manuelle Prüfung auf unterschiedlichen Bildschirmgrößen. |
| NFR-15-02 | Fehleingaben sollen möglichst direkt am jeweiligen Formularfeld angezeigt werden. | Client-seitige Validierung und Inline-Fehlermeldungen insbesondere bei der Aufgabenverwaltung. |
| NFR-15-03 | Die Farbgebung soll die Anforderungen an WCAG AA hinsichtlich des Kontrasts unterstützen. | Kontrastprüfung der verwendeten Farbpalette; insbesondere Prüfung der Dringlichkeitsdarstellung. |

---

## N1.6 Portierbarkeit und Betrieb

| ID | Anforderung | Maßnahme |
|----|-------------|----------|
| NFR-16-01 | Die Anwendung soll mit Docker Compose reproduzierbar gestartet werden können. | Docker Compose; Frontend, Backend und PostgreSQL werden als Container bereitgestellt. |
| NFR-16-02 | Für den Betrieb sollen Java, Node.js und PostgreSQL nicht direkt auf dem Host installiert werden müssen. | Die benötigten Komponenten werden durch die Docker-Container bereitgestellt. |
| NFR-16-03 | Der Start der Anwendung soll mit einem dokumentierten Vorgehen möglich sein. | Die Installations- und Startanleitung befindet sich in `INSTALL.md`. |

Der vorgesehene Start erfolgt aus dem Projektstamm:

```bash
docker compose up --build
```
```bash
cd backend
docker compose up --build