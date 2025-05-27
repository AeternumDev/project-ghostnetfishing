# GhostNet Fishing - Datenbank Setup

## Überblick
Das GhostNet Fishing Projekt verwendet MySQL als Hauptdatenbank für die Persistierung von Geisternetz-Meldungen und Berger-Informationen.

## Datenbank-Konfiguration

### Datenbankname
```
ghostnetfishing_db
```

### Verbindungsparameter
- **Host**: localhost
- **Port**: 3306
- **Benutzer**: root
- **Passwort**: 1234 (Ihr Passwort wählen!)
- **Schema**: ghostnetfishing_db

## Datenbank-Initialisierung

### 1. MySQL Server starten
Stellen Sie sicher, dass Ihr MySQL Server läuft.

### 2. Datenbank erstellen
```sql
-- Verbindung zu MySQL Server als root
CREATE DATABASE IF NOT EXISTS ghostnetfishing_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Benutzer und Berechtigung (optional, falls separater User gewünscht)
USE ghostnetfishing_db;
```

### 3. Automatische Schema-Erstellung
Das Projekt ist mit Hibernate konfiguriert, um automatisch die Tabellen zu erstellen:
- `hibernate.hbm2ddl.auto=update` in der persistence.xml
- Die Tabellen werden beim ersten Start der Anwendung automatisch erstellt

## Datenbankschema

### Tabelle: `person`
```sql
CREATE TABLE person (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    type VARCHAR(50) NOT NULL,
    CONSTRAINT chk_person_type CHECK (type IN ('REPORTER', 'RECOVERER'))
);
```

### Tabelle: `ghost_net`
```sql
CREATE TABLE ghost_net (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    estimated_size VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    reporting_person_id BIGINT,
    recovering_person_id BIGINT,
    CONSTRAINT chk_status CHECK (status IN ('REPORTED', 'RECOVERY_PENDING', 'RECOVERED', 'LOST')),
    CONSTRAINT fk_reporting_person FOREIGN KEY (reporting_person_id) REFERENCES person(id),
    CONSTRAINT fk_recovering_person FOREIGN KEY (recovering_person_id) REFERENCES person(id)
);
```

## Persistence-Konfiguration

Die Datenbankverbindung ist in `src/main/resources/META-INF/persistence.xml` konfiguriert:

```xml
<property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
<property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/ghostnetfishing_db?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC&amp;createDatabaseIfNotExist=true"/>
<property name="jakarta.persistence.jdbc.user" value="root"/>
<property name="jakarta.persistence.jdbc.password" value="1234"/>
<property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect"/>
<property name="hibernate.hbm2ddl.auto" value="update"/>
```

## Deployment-Schritte

1. **MySQL Server sicherstellen**: Stellen Sie sicher, dass MySQL läuft und mit root/1234 erreichbar ist
2. **Datenbank erstellen**: Führen Sie das CREATE DATABASE Command aus (optional, wird automatisch erstellt)
3. **Projekt bauen**: `mvn clean package`
4. **WAR deployen**: Kopieren Sie `target/projectghostnetfishing.war` in Ihren Tomcat webapps Ordner
5. **Server starten**: Starten Sie Tomcat - die Tabellen werden automatisch erstellt

## Funktionalitäten

Das System unterstützt:
- **Anonyme und registrierte Geisternetz-Meldungen**
- **Berger-Registrierung und -zuordnung**
- **Status-Verfolgung** (Gemeldet → Bergung läuft → Geborgen/Verschollen)
- **Automatische Datenpersistierung** in MySQL

## Troubleshooting

### Verbindungsfehler
- Überprüfen Sie MySQL-Server Status
- Prüfen Sie Benutzername/Passwort (root/1234)
- Stellen Sie sicher, dass Port 3306 verfügbar ist

### Schema-Probleme
- Bei Änderungen am Model können Sie `hibernate.hbm2ddl.auto=create-drop` für einen Neustart verwenden
- Für Production sollte `validate` oder `none` verwendet werden

### Encoding-Probleme
- Die Datenbank ist auf UTF8MB4 konfiguriert für vollständige Unicode-Unterstützung
- Dies ermöglicht Emojis und internationale Zeichen

## Daten-Beispiele

Nach dem ersten Start können Sie Testdaten eingeben:

### Berger registrieren
1. Gehen Sie zu `/registerRecoverer.xhtml`
2. Registrieren Sie einige Berger

### Geisternetz melden
1. Gehen Sie zu `/reportNet.xhtml`
2. Melden Sie Geisternetze mit verschiedenen Status

Das System persistiert alle Daten automatisch in der MySQL-Datenbank.
