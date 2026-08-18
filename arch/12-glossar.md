# 12 — Glossar

Dieses Glossar erklärt die wichtigsten technischen und fachlichen Begriffe des Study Planers.

Die Begriffe orientieren sich an der Spezifikation und werden in Architektur, Backend und Frontend möglichst einheitlich verwendet.

---

## 12.1 Fachliche Begriffe

| Begriff | Erklärung |
|---------|-----------|
| **Study Planer** | Webbasierte Anwendung zur Planung und Organisation von Prüfungen, Abgaben und Lernzielen. |
| **Task** | Eine Aufgabe des Studierenden. Eine Task kann beispielsweise eine Prüfung, eine Abgabe oder ein persönliches Lernziel darstellen. |
| **Exam** | Eine institutionelle Prüfung, beispielsweise eine Klausur oder mündliche Prüfung. |
| **Assignment** | Eine Aufgabe mit einer festen Abgabefrist. |
| **Goal** | Ein selbst gesetztes Lernziel des Studierenden. |
| **Deadline** | Das Datum, bis zu dem eine Aufgabe abgeschlossen sein soll. |
| **Lernplan** | Vom System automatisch berechnete Übersicht über die empfohlenen Lernaktivitäten. |
| **Lernsession** | Protokollierte Lerneinheit zu einer bestimmten Aufgabe. |
| **Fortschritt** | Prozentualer Bearbeitungsstand einer Aufgabe zwischen 0 und 100 Prozent. |
| **Reminder** | Eine konfigurierte Erinnerung an eine bevorstehende Deadline. |
| **UrgencyLevel** | Vom Backend berechnete Dringlichkeitsstufe einer Aufgabe. |
| **Ownership** | Zuordnung einer Ressource zu dem Benutzer, dem diese Ressource gehört. |

---

## 12.2 Status- und Aufgabentypen

### TaskType

Beschreibt den Charakter einer Aufgabe.

| Wert | Bedeutung |
|------|-----------|
| `EXAM` | Prüfung |
| `ASSIGNMENT` | Abgabe |
| `GOAL` | Lernziel |

---

### TaskStatus

Beschreibt den Bearbeitungszustand einer Aufgabe.

| Wert | Bedeutung |
|------|-----------|
| `OPEN` | Aufgabe wurde noch nicht begonnen. |
| `IN_PROGRESS` | Aufgabe wurde begonnen, ist aber noch nicht abgeschlossen. |
| `DONE` | Aufgabe ist vollständig abgeschlossen. |

Der Status wird automatisch aus dem `progressPercent` abgeleitet.

---

### UrgencyLevel

Beschreibt die Dringlichkeit einer offenen Aufgabe.

| Wert | Bedingung |
|------|-----------|
| `RED` | Deadline liegt höchstens 3 Tage entfernt. |
| `YELLOW` | Deadline liegt mehr als 3 und höchstens 7 Tage entfernt. |
| `GREEN` | Deadline liegt mehr als 7 Tage entfernt. |

`UrgencyLevel` wird nicht in der Datenbank gespeichert, sondern bei der Ausgabe einer Aufgabe serverseitig berechnet.

---

### ReminderChannel

Beschreibt den Kanal, über den eine Erinnerung zugestellt wird.

| Wert | Bedeutung |
|------|-----------|
| `IN_APP` | Erinnerung innerhalb der Anwendung |
| `EMAIL` | Erinnerung per E-Mail |
| `BOTH` | In-App- und E-Mail-Erinnerung |

---

## 12.3 Architekturbegriffe

| Begriff | Erklärung |
|---------|-----------|
| **arc42** | Struktur zur Dokumentation der Softwarearchitektur eines Systems. |
| **ADR** | Architecture Decision Record. Dokumentiert eine wichtige Architekturentscheidung einschließlich Kontext, Entscheidung und Konsequenzen. |
| **Bausteinsicht** | Beschreibt die statische Zerlegung eines Systems in Komponenten und deren Beziehungen. |
| **Laufzeitsicht** | Beschreibt das Zusammenspiel der Komponenten während konkreter Abläufe. |
| **Verteilungssicht** | Beschreibt, auf welchen technischen Knoten beziehungsweise Containern die Systemkomponenten ausgeführt werden. |
| **Querschnittliches Konzept** | Konzept, das mehrere Komponenten oder Architekturbausteine betrifft. |
| **Nachbarsystem** | Externes System, mit dem die Anwendung kommuniziert. |
| **Architekturentscheidung** | Bewusste Entscheidung über eine wesentliche technische oder strukturelle Eigenschaft des Systems. |

---

## 12.4 Backend-Begriffe

| Begriff | Erklärung |
|---------|-----------|
| **Backend** | Serverseitiger Teil der Anwendung, der Geschäftslogik, API und Datenzugriff bereitstellt. |
| **Spring Boot** | Framework zur Entwicklung der Java-basierten Backend-Anwendung. |
| **Spring Security** | Spring-Komponente zur Umsetzung von Authentifizierung und Autorisierung. |
| **Controller** | Backend-Komponente, die HTTP-Anfragen entgegennimmt und an die fachliche Verarbeitung weiterleitet. |
| **Service** | Backend-Komponente, die die fachliche Geschäftslogik implementiert. |
| **Repository** | Abstraktion für den Zugriff auf persistierte Daten. |
| **Spring Data JPA** | Spring-Technologie zur Vereinfachung des Zugriffs auf relationale Datenbanken über JPA. |
| **Hibernate** | ORM-Framework, das die Abbildung von Java-Objekten auf relationale Datenbanktabellen ermöglicht. |
| **Flyway** | Werkzeug zur versionierten Verwaltung und Durchführung von Datenbankmigrationen. |
| **Scheduler** | Automatisch ausgeführter Prozess, der zu festgelegten Zeitpunkten Aufgaben ausführt. |

---

## 12.5 Frontend-Begriffe

| Begriff | Erklärung |
|---------|-----------|
| **Frontend** | Clientseitiger Teil der Anwendung, mit dem der Benutzer interagiert. |
| **React** | JavaScript-/TypeScript-Bibliothek zur Erstellung komponentenbasierter Benutzeroberflächen. |
| **Single Page Application (SPA)** | Webanwendung, bei der die Benutzeroberfläche innerhalb einer Seite dynamisch aktualisiert wird. |
| **TypeScript** | Programmiersprache beziehungsweise Erweiterung von JavaScript mit statischer Typisierung. |
| **Responsive Design** | Gestaltung einer Benutzeroberfläche, die sich an unterschiedliche Bildschirmgrößen anpasst. |
| **Axios** | HTTP-Client, der vom Frontend für die Kommunikation mit der REST-API verwendet werden kann. |

---

## 12.6 API- und Datenbegriffe

| Begriff | Erklärung |
|---------|-----------|
| **REST** | Architekturstil für Webschnittstellen, bei dem Ressourcen über HTTP-Methoden angesprochen werden. |
| **API** | Application Programming Interface; definierte Schnittstelle zur Kommunikation zwischen Softwarekomponenten. |
| **REST-API** | Über HTTP erreichbare Programmierschnittstelle des Backends. |
| **JSON** | Textbasiertes Datenformat für den Austausch strukturierter Daten. |
| **DTO** | Data Transfer Object; Datenobjekt für den Austausch zwischen Frontend und Backend. |
| **JWT** | JSON Web Token; signiertes Token zur Übertragung der authentifizierten Benutzeridentität. |
| **Bearer Token** | Authentifizierungs-Token, das über den HTTP-Header `Authorization` übertragen wird. |
| **HTTP** | Protokoll für die Kommunikation zwischen Client und Server. |
| **HTTP 401** | Antwortcode für fehlende oder ungültige Authentifizierung. |
| **HTTP 403** | Antwortcode für einen verweigerten Zugriff. |
| **HTTP 404** | Antwortcode für eine nicht vorhandene Ressource. |
| **HTTP 500** | Antwortcode für einen internen Serverfehler. |

---

## 12.7 Datenbankbegriffe

| Begriff | Erklärung |
|---------|-----------|
| **PostgreSQL** | Relationale Open-Source-Datenbank, die im Study Planer zur persistenten Speicherung verwendet wird. |
| **Entität** | Persistiertes fachliches Objekt des Datenmodells, beispielsweise `USER` oder `TASK`. |
| **Primärschlüssel (PK)** | Eindeutige Kennzeichnung eines Datensatzes innerhalb einer Tabelle. |
| **Fremdschlüssel (FK)** | Verweis auf einen Datensatz einer anderen Tabelle. |
| **UUID** | Universally Unique Identifier zur eindeutigen Identifikation von Objekten. |
| **Persistenz** | Dauerhafte Speicherung von Daten über die Laufzeit eines Programms hinaus. |
| **Docker Volume** | Persistenter Speicherbereich für Containerdaten. |
| **Migration** | Kontrollierte Änderung des Datenbankschemas. |

---

## 12.8 Sicherheitsbegriffe

| Begriff | Erklärung |
|---------|-----------|
| **Authentifizierung** | Überprüfung der Identität eines Benutzers. |
| **Autorisierung** | Prüfung, ob ein authentifizierter Benutzer eine bestimmte Aktion ausführen darf. |
| **Ownership-Prüfung** | Prüfung, ob eine angeforderte Ressource dem aktuell authentifizierten Benutzer gehört. |
| **bcrypt** | Passwort-Hashing-Verfahren zur sicheren Speicherung von Passwörtern. |
| **Secret** | Sensibler Konfigurationswert, beispielsweise ein JWT-Secret oder Passwort. |
| **XSS** | Cross-Site Scripting; Angriff, bei dem schädlicher Code in eine Webanwendung eingeschleust werden kann. |
| **HTTPS** | Verschlüsselte Variante des HTTP-Protokolls. |
| **TLS** | Protokoll zur verschlüsselten und authentifizierten Kommunikation über Netzwerke. |

---

## 12.9 Deployment- und Infrastrukturbegriffe

| Begriff | Erklärung |
|---------|-----------|
| **Docker** | Plattform zur Ausführung von Anwendungen in isolierten Containern. |
| **Container** | Isolierte Laufzeitumgebung für eine Anwendung und ihre Abhängigkeiten. |
| **Docker Compose** | Werkzeug zur Definition und gemeinsamen Ausführung mehrerer Container. |
| **Service** | Einzelne technische Komponente innerhalb eines Docker-Compose-Setups. |
| **SMTP** | Simple Mail Transfer Protocol zur Übertragung von E-Mails. |
| **STARTTLS** | Verfahren, mit dem eine bestehende Verbindung auf eine verschlüsselte TLS-Verbindung erweitert wird. |
| **iCalendar / iCal** | Standardformat zur Darstellung und zum Austausch von Kalenderdaten. |
| **RFC 5545** | Standard, der das iCalendar-Format definiert. |
| **`.ics`** | Dateiendung für Dateien im iCalendar-Format. |

---

## 12.10 Dokumentationsbegriffe

| Begriff | Erklärung |
|---------|-----------|
| **Spec** | Fachliche und technische Spezifikation des Study Planers. |
| **Use Case (UC)** | Beschreibung einer konkreten Interaktion zwischen Benutzer beziehungsweise Akteur und System. |
| **AF** | Application Function beziehungsweise Anwendungsfunktion. |
| **NFR** | Non-Functional Requirement beziehungsweise nichtfunktionale Anforderung. |
| **ADR** | Architecture Decision Record zur Dokumentation einer Architekturentscheidung. |
| **MVP** | Minimum Viable Product; kleinste funktionsfähige Version des Systems mit den notwendigen Muss-Funktionen. |

---

## 12.11 Abkürzungsverzeichnis

| Abkürzung | Bedeutung |
|-----------|-----------|
| API | Application Programming Interface |
| ADR | Architecture Decision Record |
| AF | Application Function |
| DTO | Data Transfer Object |
| FK | Foreign Key |
| HTTP | Hypertext Transfer Protocol |
| HTTPS | Hypertext Transfer Protocol Secure |
| JWT | JSON Web Token |
| JPA | Java Persistence API |
| MVP | Minimum Viable Product |
| NFR | Non-Functional Requirement |
| ORM | Object-Relational Mapping |
| PK | Primary Key |
| REST | Representational State Transfer |
| SMTP | Simple Mail Transfer Protocol |
| SPA | Single Page Application |
| TLS | Transport Layer Security |
| UC | Use Case |
| UI | User Interface |
| UUID | Universally Unique Identifier |

---

## 12.12 Konsistente Verwendung

Die in diesem Glossar definierten Begriffe sollen im weiteren Projektverlauf einheitlich verwendet werden.

Insbesondere sollen die Bezeichnungen aus der Spezifikation nicht ohne fachlichen Grund im Code oder in der Architekturdokumentation geändert werden.

Dies betrifft insbesondere:

- `Task`
- `TaskType`
- `TaskStatus`
- `UrgencyLevel`
- `LearningPlanDTO`
- `CreateTaskRequest`
- `UpdateProgressRequest`
- `Reminder`
- `LearningSession`
- `Ownership`

Dadurch bleibt die Verbindung zwischen Spezifikation, Architektur und Implementierung nachvollziehbar.