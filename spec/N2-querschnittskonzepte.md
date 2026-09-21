# N2 — Querschnittskonzepte

Querschnittskonzepte sind technische oder organisatorische Entscheidungen, die mehrere Bausteine gleichzeitig betreffen. Sie werden hier einmal definiert und von F2, S1 und N1 referenziert, statt in jedem Baustein wiederholt zu werden.

---

## N2.1 Authentifizierung und Session

**Konzept:** JWT-basierte, stateless Authentifizierung.

- Beim Login stellt das Backend ein signiertes JWT mit HS256 aus.
- Die Gültigkeit des JWT beträgt im aktuellen Implementierungsstand 24 Stunden.
- Das Frontend überträgt das Token bei geschützten API-Anfragen über den HTTP-Header `Authorization: Bearer <token>`.
- Spring Security prüft eingehende Requests über den `JwtAuthenticationFilter`.
- Die Benutzer-ID wird aus dem JWT als UUID ausgelesen.
- Die Service-Schicht prüft bei geschützten Ressourcen zusätzlich die Ownership anhand der authentifizierten Benutzer-ID.
- Das Session-Management des Backends ist auf `STATELESS` konfiguriert.

Registrierung und Login sind öffentlich erreichbar. Die übrigen geschützten API-Endpunkte erfordern eine erfolgreiche Authentifizierung.

**Querverweise:** NFR-12-01 bis NFR-12-05; UC-01, UC-02, UC-03.

---

## N2.2 Fehlerbehandlung

**Backend:** Die zentrale Fehlerbehandlung erfolgt über einen globalen `@RestControllerAdvice`.

Der `GlobalExceptionHandler` verarbeitet unter anderem:

- `ResponseStatusException`
- `IllegalArgumentException`
- `DateTimeException`
- sonstige nicht speziell behandelte Exceptions

Fehler werden als strukturierte JSON-Antwort zurückgegeben:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Fehlerbeschreibung",
  "timestamp": "2026-01-01T12:00:00"
}