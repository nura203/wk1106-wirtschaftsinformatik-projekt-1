# S3 — Inbetriebnahme

S3 beschreibt wie die Anwendung lokal gestartet und ausprobiert werden kann. Voraussetzung ist lediglich eine funktionierende Docker-Installation — kein lokales Java oder Node.js erforderlich.

---

## S3.1 Voraussetzungen

| Komponente | Mindestversion | Zweck |
|------------|---------------|-------|
| Docker Engine | 24.x | Container-Runtime |
| Docker Compose | 2.x | Multi-Container-Orchestrierung |
| Git | 2.x | Repository-Clone |


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


Das Datenbankschema wird beim ersten Start automatisch angelegt.

---

## S3.3 Stoppen und Datenbankzurücksetzen

```bash
# Stoppen (Daten bleiben erhalten)
docker compose down

# Stoppen und Datenbank löschen
docker compose down -v
```

