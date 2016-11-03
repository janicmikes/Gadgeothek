@%~d0
@cd %~p0
START /MIN node server.js
java -Dserver=localhost:8080 -jar gadgeothek-admin.jar
