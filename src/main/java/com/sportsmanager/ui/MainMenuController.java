package com.sportsmanager.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

public class MainMenuController {

    // Futbol kariyerini başlatan butonun aksiyonu
    @FXML
    public void handleFootballCareer(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        startLeague(stage, "football");
    }

    // Voleybol kariyerini başlatan butonun aksiyonu
    @FXML
    public void handleVolleyballCareer(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        startLeague(stage, "volleyball");
    }

    // Ortak lig başlatma mantığı
    private void startLeague(Stage stage, String sportType) {
        System.out.println("==== " + sportType.toUpperCase() + " LİGİ BAŞLIYOR ====");
        LeagueController temp = new LeagueController(stage, sportType, null);
        TeamSelectController teamSelect = new TeamSelectController(stage, sportType, temp.getTeams());
        teamSelect.show();
    }
}