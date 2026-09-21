# S1 — Nachbarsysteme

Ein Nachbarsystem ist ein externes System außerhalb der eigenen Anwendung.

PostgreSQL ist kein Nachbarsystem, sondern eine interne Komponente der Anwendung.

Die für den Study Planner relevanten externen Systeme beziehungsweise Schnittstellen sind:

- ein optionaler E-Mail-Provider über SMTP
- externe Kalenderanwendungen über den `.ics`-Export

---

## S1.1 REST-API (Frontend ↔ Backend)

**Basis-URL:** `http://localhost:8080/api/v1` (Entwicklungsumgebung)

**Authentifizierung:** `Authorization: Bearer <JWT>` bei geschützten Endpunkten.

**Content-Type:** `application/json` für JSON-Anfragen und -Antworten.

**Versionierung:** Die API verwendet die URL-basierte Versionierung `/api/v1`.

### S1.1.1 Authentifizierung

| Methode | Pfad | UC | Auth |
|---------|------|----|------|
| `POST` | `/auth/register` | UC-01 | Nein |
| `POST` | `/auth/login` | UC-02 | Nein |

#### POST `/auth/register` — Request

```json
{
  "username": "max.mustermann",
  "email": "max@example.com",
  "password": "geheimesPasswort123"
}