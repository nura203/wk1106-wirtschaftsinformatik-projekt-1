# 6 — Laufzeitsicht

Die Laufzeitsicht beschreibt das Verhalten der wesentlichen Architekturbausteine zur Laufzeit. Die dargestellten Szenarien orientieren sich an den in Kapitel 5 beschriebenen Bausteinen und zeigen die wesentlichen Kommunikationsabläufe zwischen Frontend, Backend, Geschäftslogik und Persistenz.

Es werden drei wesentliche Szenarien betrachtet:

1. **Authentifizierung** (UC-01, UC-02) – mit Registrierung beziehungsweise Anmeldung und JWT-Erzeugung.
2. **Fachlicher Request-Lebenszyklus** (UC-04 bis UC-09, UC-11) – als verallgemeinertes Muster für authentifizierte REST-Anfragen.
3. **Kalenderexport** (UC-11) – als Beispiel für die Erzeugung einer `.ics`-Datei.

Die verwendeten Bausteine entsprechen den in Kapitel 5 definierten Blackboxen und Whiteboxen.

---

## 6.1 Authentifizierung — UC-01 / UC-02

Bei Registrierung und Anmeldung übermittelt das Frontend die erforderlichen Benutzerdaten an den Auth Controller.

Der Auth Controller delegiert die fachliche Verarbeitung an den Auth Service. Der Auth Service verwendet das User Repository für den Datenzugriff und verarbeitet Passwörter mit BCrypt. Bei einer erfolgreichen Anmeldung wird über den JwtService ein JWT erzeugt.

```mermaid
sequenceDiagram
    actor Nutzer as Studierender

    participant Frontend as Frontend (React SPA)
    participant AuthC as AuthController
    participant AuthS as AuthService
    participant UserRepo as UserRepository
    participant DB as PostgreSQL
    participant JWT as JwtService

    Nutzer->>Frontend: E-Mail + Passwort eingeben
    Frontend->>AuthC: POST /api/v1/auth/login
    AuthC->>AuthS: Login-Daten übergeben
    AuthS->>UserRepo: Benutzer anhand E-Mail suchen
    UserRepo->>DB: Benutzer abfragen
    DB-->>UserRepo: Benutzer + Passwort-Hash
    UserRepo-->>AuthS: Benutzer + Passwort-Hash

    AuthS->>AuthS: Passwort mit BCrypt prüfen

    alt Zugangsdaten gültig
        AuthS->>JWT: JWT für Benutzer erzeugen
        JWT-->>AuthS: JWT
        AuthS-->>AuthC: Authentifizierungsdaten
        AuthC-->>Frontend: HTTP 200 + JWT + Benutzerdaten
        Frontend-->>Nutzer: Anwendung anzeigen
    else Zugangsdaten ungültig
        AuthS-->>AuthC: Authentifizierung fehlgeschlagen
        AuthC-->>Frontend: HTTP 401
        Frontend-->>Nutzer: Allgemeine Fehlermeldung
    end