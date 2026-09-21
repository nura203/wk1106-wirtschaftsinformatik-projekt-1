# INSTALL – StudyPlanner

## 1. Voraussetzungen

Für die lokale Ausführung werden benötigt:

- Git
- Docker Desktop
- Docker Compose

Docker Desktop muss gestartet sein.

## 2. Repository klonen

Repository klonen und in das Projektverzeichnis wechseln:

```powershell
git clone <REPOSITORY-URL>
cd wk1106-wirtschaftsinformatik-projekt-1
```

## 3. Anwendung starten

Die vollständige Anwendung wird mit Docker Compose gestartet:

```powershell
docker compose up --build
```

Dabei werden folgende Komponenten gestartet:

- PostgreSQL-Datenbank
- Spring-Boot-Backend
- React-Frontend

## 4. Anwendung öffnen

Nach erfolgreichem Start ist das Frontend erreichbar unter:

http://localhost:5173

Das Backend ist erreichbar unter:

http://localhost:8080

## 5. Anwendung beenden

Die laufenden Container werden beendet mit:

```powershell
docker compose down
```

Das PostgreSQL-Docker-Volume bleibt dabei erhalten.

## 6. Datenbank zurücksetzen

Falls die lokale Datenbank vollständig zurückgesetzt werden soll:

```powershell
docker compose down -v
```

Anschließend kann die Anwendung erneut gestartet werden:

```powershell
docker compose up --build
```

**Achtung:** Durch `-v` werden die im Docker-Volume gespeicherten PostgreSQL-Daten gelöscht.

## 7. Backend-Tests

Die Backend-Tests können lokal mit Maven ausgeführt werden:

```powershell
cd backend
mvn test
```

## 8. Konfiguration

Eine Beispielkonfiguration befindet sich unter:

```text
backend/.env.example
```

Die Datei enthält keine produktiven Zugangsdaten.

## 9. Docker-Status prüfen

Der Status der Container kann mit folgendem Befehl überprüft werden:

```powershell
docker compose ps
```

Bei erfolgreichem Start sollten die Services `frontend`, `backend` und `postgres` ausgeführt werden.

## 10. Fehlerbehebung

### Docker Desktop läuft nicht

Docker Desktop starten und anschließend erneut ausführen:

```powershell
docker compose up --build
```

### Ein Port ist bereits belegt

StudyPlanner verwendet standardmäßig folgende Ports:

- `5173` – Frontend
- `8080` – Backend
- `5432` – PostgreSQL

Wenn einer dieser Ports bereits verwendet wird, muss der betreffende Prozess beendet oder die Compose-Konfiguration angepasst werden.

### Container neu erstellen

```powershell
docker compose down
docker compose up --build
```

## 11. Weitere Dokumentation

- `README.md` – Kurzübersicht und Schnellstart
- `spec/` – Spezifikation
- `arch/` – Architekturdokumentation