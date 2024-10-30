# UniSplitterProject

= Splitter

=== Anleitung zum starten der Anwendung mit GitBash:
Schritt 1

POSTGRES_USER=Bsp_user POSTGRES_PASSWORD=Bsp_password docker compose up

Schritt 2 (In einem anderen GitBash Tab)

CLIENT_ID=Bsp_id CLIENT_SECRET=Bsp_secret POSTGRES_USER=Bsp_user POSTGRES_PASSWORD=Bsp_password ./gradlew bootRun

=== Anleitung zum starten der Anwendung ohne GitBash:

1.  Environment variables für Splitter Application konfigurieren: _CLIENT_ID=Bsp_id CLIENT_SECRET=Bsp_secret POSTGRES_USER=Bsp_user POSTGRES_PASSWORD=Bsp_password_
2.  Environment variables für docker-compose.yaml konfigurieren: _POSTGRES_USER=Bsp_user POSTGRES_PASSWORD=Bsp_password_
3.  docker compose up
4.  Run SplitterApplication

Verwendete Java-Version: 17
