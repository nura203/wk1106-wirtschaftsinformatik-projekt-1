# Installations- und Inbetriebnahmeanweisung

## Voraussetzungen

- Git
- Docker Desktop
- Docker Compose

## Projekt herunterladen

Repository klonen:

```bash
git clone https://github.com/nura203/wk1106-wirtschaftsinformatik-projekt-1.git
cd wk1106-wirtschaftsinformatik-projekt-1
```

## Anwendung starten

Zum Starten der Anwendung:

```bash
docker compose up --build
```

## Anwendung aufrufen

Nach erfolgreichem Start sind die folgenden Dienste erreichbar:

- Frontend: http://localhost:5173
- Backend-API: http://localhost:8080/api/v1

Die Anwendung kann über das Frontend im Browser aufgerufen werden.

## Anwendung beenden

Zum Beenden der laufenden Container:

```bash
docker compose down
```