# S3 — Inbetriebnahme

S3 beschreibt, wie die Anwendung lokal gestartet und ausprobiert werden kann.

Voraussetzung ist eine funktionierende Docker-Installation. Für den Betrieb der containerisierten Anwendung sind kein lokal installiertes Java, Node.js oder PostgreSQL erforderlich.

---

## S3.1 Voraussetzungen

| Komponente | Zweck |
|------------|-------|
| Docker Engine / Docker Desktop | Container-Runtime |
| Docker Compose 2.x | Multi-Container-Orchestrierung |
| Git | Repository-Clone |

---

## S3.2 Schnellstart

### 1. Repository klonen

Das Repository wird zunächst geklont:

```bash
git clone https://github.com/nura203/wk1106-wirtschaftsinformatik-projekt-1.git
cd wk1106-wirtschaftsinformatik-projekt-1