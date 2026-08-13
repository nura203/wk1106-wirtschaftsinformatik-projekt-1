# 2 — Randbedingungen

Dieses Kapitel beschreibt die technischen, organisatorischen und projektspezifischen
Randbedingungen, die den Lösungsraum der Softwarearchitektur einschränken.

Technologieentscheidungen, die innerhalb dieses Lösungsraums getroffen werden,
werden in Kapitel 9 als Architekturentscheidungen (ADRs) dokumentiert.

## 2.1 Technische Randbedingungen

| ID | Randbedingung | Bedeutung für die Architektur |
|----|---------------|-------------------------------|
| CON-01 | Backend mit Java 21, Frontend mit TypeScript 5 | Die Architektur muss beide Technologien berücksichtigen. |
| CON-02 | Die Anwendung muss über `docker compose up --build` gestartet werden können. | Alle für den Betrieb benötigten Komponenten werden containerisiert. |
| CON-03 | Die Anwendung muss lokal auf Linux, macOS und Windows betrieben werden können. | Der Betrieb darf nicht von plattformspezifischen lokalen Installationen abhängen. |
| CON-04 | Kein lokales Java oder Node.js darf für den vorgesehenen Betrieb erforderlich sein. | Runtime-Abhängigkeiten werden innerhalb der Container bereitgestellt. |
| CON-05 | Dokumentation wird als Markdown im Repository geführt. | Architektur und Spezifikation bleiben versionierbar und diff-bar. |
| CON-06 | Diagramme müssen mit ihrem Quelltext im Repository nachvollziehbar bleiben. | Mermaid oder PlantUML werden für Architekturdiagramme verwendet. |

## 2.2 Sicherheits- und Datenschutzrandbedingungen

| ID | Randbedingung | Bedeutung für die Architektur |
|----|---------------|-------------------------------|
| CON-07 | API-Keys, Passwörter und Secrets dürfen nicht im Repository gespeichert werden. | Sensible Konfiguration wird über Umgebungsvariablen bereitgestellt. |
| CON-08 | Passwörter dürfen ausschließlich als bcrypt-Hash gespeichert werden. | Die Persistenzschicht darf keine Klartextpasswörter speichern. |
| CON-09 | Nutzer dürfen ausschließlich auf ihre eigenen Ressourcen zugreifen. | Die Service-Schicht muss eine Ownership-Prüfung durchführen. |
| CON-10 | Personenbezogene Daten der Projektmitglieder dürfen nicht unnötig im öffentlichen Repository gespeichert werden. | Dokumentation und Repository-Inhalte müssen Datenschutzvorgaben berücksichtigen. |

## 2.3 Projekt- und Prozessrandbedingungen

| ID | Randbedingung | Bedeutung für die Architektur |
|----|---------------|-------------------------------|
| CON-11 | Das Projekt wird als Gruppenprojekt mit kontinuierlicher Git-Historie entwickelt. | Architektur und Implementierung müssen nachvollziehbar versioniert werden. |
| CON-12 | Commit-Messages müssen Conventional Commits entsprechen. | Änderungen an Architektur und Code müssen entsprechend dokumentiert werden. |
| CON-13 | Spezifikation und Architektur müssen aufeinander aufbauen und mit dem Code konsistent sein. | Use Cases, Architekturkomponenten und Code müssen eindeutig aufeinander abbildbar sein. |
| CON-14 | Wesentliche Architekturentscheidungen müssen als ADRs dokumentiert werden. | Architekturentscheidungen werden in Kapitel 9 nachvollziehbar begründet. |
| CON-15 | KI-Werkzeuge dürfen eingesetzt werden, ihre Verwendung muss jedoch dokumentiert und die Ergebnisse müssen vom Projektteam geprüft werden. | KI-generierte Inhalte werden nicht ungeprüft als Architekturentscheidungen übernommen. |

## 2.4 Abgrenzung zu Architekturentscheidungen

Die in diesem Kapitel genannten Punkte sind externe oder projektspezifische
Vorgaben. Die konkrete Auswahl von Frameworks, Datenbanktechnologien,
Authentifizierungsverfahren und Deployment-Lösungen wird dagegen als
Architekturentscheidung behandelt und in Kapitel 9 über ADRs begründet.