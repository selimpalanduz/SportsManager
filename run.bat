@echo off
java --module-path "lib" --add-modules javafx.controls,javafx.fxml -cp target/SportsManagerProject-1.0-SNAPSHOT.jar com.sportsmanager.Main
pause