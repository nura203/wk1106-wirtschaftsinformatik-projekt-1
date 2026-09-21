# 8 — Querschnittliche Konzepte

Dieses Kapitel beschreibt die zentralen technischen Konzepte, die von allen Entwicklern im Projekt einheitlich umgesetzt werden. Die Beschreibungen orientieren sich am tatsächlich implementierten Stand der Anwendung und definieren die gemeinsamen Regeln für Authentifizierung, Autorisierung, Fehlerbehandlung, Logging sowie Datenbankzugriff und Persistenz.

---

## 8.1 Authentifizierung und Autorisierung

### 8.1.1 Verwendete Technologie

Für die Authentifizierung und Autorisierung werden folgende Technologien und Mechanismen verwendet:

- **Backend:** Spring Security
- **JWT:** `jjwt`
- **Token-Algorithmus:** HS256 (HMAC mit SHA-256)
- **Token-Ablaufzeit:** 24 Stunden
- **Token-Übertragung:** HTTP-Header `Authorization: Bearer <token>`
- **Benutzerkennung im Token:** UUID des Benutzers
- **Passwort-Hashing:** BCrypt
- **Session-Management:** stateless

Die Authentifizierung erfolgt nicht über serverseitige Sessions. Nach einem erfolgreichen Login erhält das Frontend ein JWT, das bei geschützten Requests als Bearer-Token übertragen wird.

### 8.1.2 Token-Ausstellung beim Login

Die JWT-Erzeugung ist im `JwtService` zentralisiert.

Das Token enthält die UUID des Benutzers als `subject`, den Ausstellungszeitpunkt sowie den Ablaufzeitpunkt. Anschließend wird das Token mit dem konfigurierten Secret und dem Algorithmus HS256 signiert.

Der relevante Ablauf der Implementierung ist:

```java
public String generateToken(UUID userId) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

    return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
}