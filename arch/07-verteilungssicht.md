# 7 — Verteilungssicht

Die Verteilungssicht beschreibt, auf welchen technischen Knoten (Hosts) die Komponenten des Study Planners ausgeführt werden und wie diese über Netzwerkgrenzen hinweg kommunizieren.

---

## 7.1 Übersicht

Der Study Planner wird als containerisierte Webanwendung betrieben. Für den lokalen Betrieb werden Frontend, Backend und PostgreSQL über die zentrale Docker-Compose-Konfiguration im Projektstamm gestartet.

Der Start erfolgt aus dem Projektstamm mit:

```powershell
docker compose up --build