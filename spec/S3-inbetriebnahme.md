# S3 — Inbetriebnahme

S3 beschreibt die Rollout-Prozedur für Entwicklungs- und Testbetrieb. Die vollständige Anweisung inkl. Troubleshooting befindet sich in `INSTALL.md` im Repository-Wurzelverzeichnis. S3 ist eine Zusammenfassung der wesentlichen Schritte.

---

## S3.1 Voraussetzungen

| Komponente | Mindestversion | Zweck |
|------------|---------------|-------|
| Docker Engine | 24.x | Container-Runtime |
| Docker Compose | 2.x | Multi-Container-Orchestrierung |
| Git | 2.x | Repository-Clone |

Kein lokales Java, Maven oder Node.js erforderlich — alles läuft in Containern (CON-02, NFR-16-01).

---

## S3.2 Schnellstart

```bash
# 1. Repository klonen
git clone https://github.com/<gruppe>/study-planer.git
cd study-planer

# 2. Konfiguration anlegen
cp .env.example .env
# Optional: .env anpassen (JWT_SECRET, SMTP etc.)

# 3. Starten
docker compose up --build
```

Nach erfolgreichem Start erreichbar unter:

| Service | URL |
|---------|-----|
| Frontend (React SPA) | http://localhost:5173 |
| Backend-API | http://localhost:8080/api/v1 |
| Adminer (DB-UI, optional) | http://localhost:8888 |

Das Datenbankschema wird beim ersten Start automatisch durch Flyway angelegt (NFR-16-02). Ein manuelles `migrate` ist nicht erforderlich.

---

## S3.3 Konfiguration via Umgebungsvariablen

Alle sensitiven Werte werden über `.env` konfiguriert — niemals direkt in `docker-compose.yml` oder `application.properties` (CON-06, NFR-12-04).

| Variable | Default in `.env.example` | Beschreibung |
|----------|--------------------------|--------------|
| `JWT_SECRET` | `changeme-in-production` | JWT-Signing-Key; in Produktion ≥ 32 zufällige Zeichen |
| `POSTGRES_DB` | `studyplaner` | Datenbankname |
| `POSTGRES_USER` | `studyplaner` | Datenbanknutzer |
| `POSTGRES_PASSWORD` | `studyplaner` | Datenbankpasswort |
| `SMTP_HOST` | (leer) | SMTP-Server; leer = E-Mail-Kanal deaktiviert |
| `SMTP_PORT` | `587` | SMTP-Port |
| `SMTP_USER` | (leer) | SMTP-Nutzername |
| `SMTP_PASS` | (leer) | SMTP-Passwort |

---

## S3.4 Stoppen und Datenbankzurücksetzen

```bash
# Stoppen (Daten bleiben erhalten)
docker compose down

# Stoppen und Datenbank löschen
docker compose down -v
```

---

## S3.5 Querverweise

| Baustein | Relevanz |
|----------|---------|
| P1 SC-02 | Erfolgskriterium: Anwendung startet mit `docker compose up --build` ohne manuelle Schritte. |
| N2.5 | Konfigurationsmanagement-Konzept. |
| CON-06 | Keine Secrets im Repo. |
| NFR-16-01 | Plattformunabhängigkeit (Linux, macOS, Windows). |
