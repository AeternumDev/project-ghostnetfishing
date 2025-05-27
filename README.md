# Project GhostNetFishing

Dieses Projekt ist eine Webanwendung zur Verwaltung und Bekämpfung von Geisternetzen.

## Getting Started

Folgen Sie diesen Anweisungen, um das Projekt lokal zum Laufen zu bringen.

### Voraussetzungen

*   Java Development Kit (JDK) 17
*   Apache Maven 3.9.9
*   Apache Tomcat 10.1.41
*   Eine konfigurierte Datenbankinstanz (siehe README_db.md)


**Wichtig:** Bevor Sie die Anwendung starten, müssen Sie das Root-Passwort für die Datenbank anpassen. Diese Konfiguration finden Sie in der Datei: `persistence.xml`

### Anwendung starten

1.  **Tomcat stoppen:**
    Stellen Sie sicher, dass Ihr Apache Tomcat Server gestoppt ist. Dies kann über die Windows-Dienste (`services.msc`) 

2.  **Projekt bauen:**
   Führen Sie den folgenden Maven-Befehl im Terminal aus, um das Projekt zu kompilieren und die `.war`-Datei zu erstellen:
    ```
    cd "C:\IHR PFAD\projectghostnetfishing"
    mvn clean install
    ```

3.  **Deployment der .war-Datei:**
    *   Kopieren Sie die generierte `.war`-Datei aus dem Verzeichnis `C:\IHR PFAD\projectghostnetfishing`.
    *   Löschen Sie ggf. eine alte Version der `.war`-Datei und den entpackten Ordner aus dem `webapps`-Verzeichnis Ihres Tomcat-Servers.
    *   Fügen Sie die neue `.war`-Datei in das `webapps`-Verzeichnis Ihres Tomcat-Servers ein.

4.  **Tomcat starten:**
    Starten Sie Ihren Apache Tomcat Server wieder. Tomcat wird die `.war`-Datei automatisch deployen.

5.  **Anwendung aufrufen:**
    Öffnen Sie Ihren Webbrowser und navigieren Sie zu:
    [http://localhost:8080/projectghostnetfishing/](http://localhost:8080/projectghostnetfishing/)
