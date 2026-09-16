# 2 — Randbedingungen

Dieses Kapitel beschreibt die technischen, organisatorischen und projektspezifischen
Randbedingungen, die den Lösungsraum der Softwarearchitektur einschränken.

Technologieentscheidungen, die innerhalb dieses Lösungsraums getroffen werden,
werden in Kapitel 9 als Architekturentscheidungen (ADRs) dokumentiert.

## 2.1 Technische Randbedingungen

| ID | Randbedingung | Bedeutung für die Architektur |
|----|---------------|-------------------------------|
| TECH-01 | Backend mit Java 21, Frontend mit TypeScript 5 | Die Architektur muss beide Technologien berücksichtigen. |
| TECH-02 | Die Anwendung muss über `docker compose up --build` gestartet werden können. | Alle für den Betrieb benötigten Komponenten werden containerisiert. |
| TECH-03 | Die Anwendung muss lokal auf Linux, macOS und Windows betrieben werden können. | Der Betrieb darf nicht von plattformspezifischen lokalen Installationen abhängen. |
| TECH-04 | Kein lokales Java oder Node.js darf für den vorgesehenen Betrieb erforderlich sein. | Runtime-Abhängigkeiten werden innerhalb der Container bereitgestellt. |
| TECH-05 | API-Keys, Passwörter und Secrets dürfen nicht im Repository gespeichert werden. | Sensible Konfiguration wird über Umgebungsvariablen bereitgestellt. |
| TECH-06 | Passwörter dürfen ausschließlich als bcrypt-Hash gespeichert werden. | Die Persistenzschicht darf keine Klartextpasswörter speichern. |
| TECH-07 | Nutzer dürfen ausschließlich auf ihre eigenen Ressourcen zugreifen. | Die Backend-Architektur muss eine Prüfung der Ressourcen-Zugehörigkeit ermöglichen und durchsetzen. |

## 2.2 Organisatorische Randbedingungen

| ID | Randbedingung | Bedeutung für die Architektur |
|----|---------------|-------------------------------|
| ORG-01 | Das Projekt wird als Gruppenprojekt mit kontinuierlicher Git-Historie entwickelt. | Architektur und Implementierung müssen nachvollziehbar versioniert werden. |
| ORG-02 | Das Repository ist öffentlich auf GitHub; der Betreuer muss ab Projektbeginn Lesezugriff haben. | Architektur-, Spezifikations- und Code-Dokumentation sind öffentlich einsehbar. Vertrauliche Informationen, Zugangsdaten und Secrets dürfen daher nicht im Repository gespeichert werden. |
| ORG-03 | Personenbezogene Daten der Projektmitglieder dürfen nicht unnötig im öffentlichen Repository gespeichert werden. | Dokumentation und Repository-Inhalte müssen Datenschutzvorgaben berücksichtigen. |

## 2.3 Konventionen

| ID | Randbedingung | Bedeutung für die Architektur |
|----|---------------|-------------------------------|
| CON-01 | Dokumentation wird als Markdown im Repository geführt. | Architektur und Spezifikation bleiben versionierbar und diff-bar. |
| CON-02 | Diagramme müssen mit ihrem Quelltext im Repository nachvollziehbar bleiben. | Mermaid oder PlantUML werden für Architekturdiagramme verwendet. |
| CON-03 | Commit-Messages müssen Conventional Commits entsprechen. | Änderungen an Architektur und Code müssen entsprechend dokumentiert werden. |
| CON-04 | Spezifikation und Architektur müssen aufeinander aufbauen und mit dem Code konsistent sein. | Use Cases, Architekturkomponenten und Code müssen eindeutig aufeinander abbildbar sein. |
| CON-05 | Wesentliche Architekturentscheidungen müssen als ADRs dokumentiert werden. | Architekturentscheidungen werden in Kapitel 9 nachvollziehbar begründet. |
| CON-06 | KI-Werkzeuge dürfen eingesetzt werden, ihre Verwendung muss jedoch dokumentiert und die Ergebnisse müssen vom Projektteam geprüft werden. | KI-generierte Inhalte werden nicht ungeprüft als Architekturentscheidungen übernommen. |

## 2.4 Abgrenzung zu Architekturentscheidungen

Die in diesem Kapitel genannten Punkte sind externe oder projektspezifische
Vorgaben. Die konkrete Auswahl von Frameworks, Datenbanktechnologien,
Authentifizierungsverfahren und Deployment-Lösungen wird dagegen als
Architekturentscheidung behandelt und in Kapitel 9 über ADRs begründet.