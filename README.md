# StudyPlanner

StudyPlanner ist eine Webanwendung zur Planung und Verwaltung von Lernaufgaben.

## Voraussetzungen

- Git
- Docker Desktop mit Docker Compose

## Projekt starten

Repository klonen und in den Projektordner wechseln:

```powershell
git clone <REPOSITORY-URL>
cd wk1106-wirtschaftsinformatik-projekt-1
```

Anwendung starten:

```powershell
docker compose up --build
```

Danach:

- Frontend: http://localhost:5173
- Backend: http://localhost:8080

## Anwendung beenden

```powershell
docker compose down
```

## Tests

Backend-Tests:

```powershell
cd backend
mvn test
```

## Dokumentation

- `INSTALL.md` – Installation und Betrieb
- `spec/` – Spezifikation
- `arch/` – Architekturdokumentation