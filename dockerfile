# 1. Basisimage für OpenJDK verwenden
FROM openjdk:17-jdk-slim

# 2. Arbeitsverzeichnis für die Anwendung erstellen
WORKDIR /app

# 3. JAR-Datei ins Arbeitsverzeichnis kopieren
COPY build/libs/UniSplitterProject-0.0.1-SNAPSHOT.jar app.jar

# 4. Exponiere Port 9000 (wie in application.properties)
EXPOSE 9000

# 5. Die Spring Boot Anwendung starten
ENTRYPOINT ["java", "-jar", "app.jar"]