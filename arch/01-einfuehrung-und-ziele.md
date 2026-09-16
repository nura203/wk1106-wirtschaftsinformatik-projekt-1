# 1 — Einführung und Ziele

## 1.1 Aufgabenstellung

Der Study Planer ist eine webbasierte Einzelnutzer-Anwendung zur strukturierten
Planung von Prüfungen, Lernzielen und akademischen Abgaben.

Der Studierende erfasst seine Aufgaben einmalig. Das System berechnet daraus
automatisch einen priorisierten Lernplan, hebt dringende Aufgaben visuell hervor
und unterstützt optionale Erinnerungen sowie den Export der offenen Aufgaben
in einen Kalender.

Ziel des Systems ist es, die Selbstorganisation von Studierenden zu vereinfachen.
Prüfungen, Abgaben und Lernziele werden zentral erfasst und anhand von
Deadlines, geschätztem Aufwand und Fortschritt priorisiert.

## 1.2 Architekturziele

Die Architektur soll insbesondere folgende Ziele unterstützen:

| ID | Architekturziel | Bezug zur Spezifikation |
|----|-----------------|-------------------------|
| AZ-01 | Klare Trennung von Frontend, Geschäftslogik und Persistenz | P1, P2 |
| AZ-02 | Sichere Authentifizierung und Zugriffskontrolle | N1, N2 |
| AZ-03 | Performante Berechnung und Bereitstellung des Lernplans | N1, F3 |
| AZ-04 | Wartbare und testbare Geschäftslogik | N1, N2 |
| AZ-05 | Zuverlässige persistente Datenspeicherung | N1, D1 |
| AZ-06 | Reproduzierbarer Betrieb über Docker Compose | P1, S3 |
| AZ-07 | Klare Integration externer Schnittstellen | P2, S1 |

## 1.3 Qualitätsziele

Für die Architektur sind insbesondere folgende Qualitätsziele relevant:

| ID | Qualitätsziel | Anforderung / Kriterium |
|----|---------------|-------------------------|
| QZ-01 | Performance | Das Dashboard soll in unter 2 Sekunden laden. Standard-REST-Requests sollen im 95. Perzentil unter 500 ms beantwortet werden. |
| QZ-02 | Sicherheit | Passwörter werden ausschließlich als bcrypt-Hash gespeichert. Geschützte API-Endpunkte erfordern einen gültigen JWT. Nutzer dürfen nur auf eigene Ressourcen zugreifen. |
| QZ-03 | Zuverlässigkeit | Persistierte Daten sollen einen Neustart der Datenbank überstehen. Fehler einzelner Funktionen dürfen den Normalbetrieb nicht blockieren. |
| QZ-04 | Wartbarkeit | Die Geschäftslogik soll strukturiert und durch Unit-Tests abgesichert werden. Wesentliche Architekturentscheidungen werden als ADRs dokumentiert. |
| QZ-05 | Benutzbarkeit | Die Anwendung soll auf Desktop- und Mobilgeräten nutzbar sein und verständliche Fehlermeldungen bereitstellen. |
| QZ-06 | Portierbarkeit | Die Anwendung soll über Docker Compose auf Linux, macOS und Windows gestartet werden können. |