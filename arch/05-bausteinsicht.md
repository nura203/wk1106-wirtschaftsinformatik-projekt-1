# 5 — Bausteinsicht

Dieses Kapitel beschreibt die statische Struktur des Study Planners nach dem Prinzip der schrittweisen Verfeinerung (top-down). Jede Ebene öffnet eine Whitebox der darüber liegenden Ebene und zeigt die enthaltenen Bausteine als Blackboxen.

Die Struktur orientiert sich an den Use Cases aus [F2](../spec/F2-anwendungsfaelle.md), den Anwendungsfunktionen aus [F3](../spec/F3-anwendungsfunktionen.md) sowie dem Datenmodell aus [D1](../spec/D1-datenmodell.md).

---

## 5.1 Whitebox „Study Planner System“

Die in Kapitel 3 beschriebene Blackbox des Study Planners wird auf dieser Ebene als Whitebox geöffnet.

Sie besteht aus drei wesentlichen Bausteinen:

- Frontend
- Backend
- Persistenz

```mermaid
graph TD
    User["Studierender"]

    subgraph System["Study Planner (System)"]
        Frontend["Frontend<br/>React / TypeScript"]
        Backend["Backend<br/>Spring Boot / Java 21"]
        DB["PostgreSQL"]
    end

    User --> Frontend
    Frontend -->|"REST / JSON / JWT"| Backend
    Backend -->|"JPA / Hibernate"| DB