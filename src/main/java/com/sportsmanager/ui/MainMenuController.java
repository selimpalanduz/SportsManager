package com.sportsmanager.ui;

import com.sportsmanager.model.common.GameState;
import com.sportsmanager.core.SaveManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

public class MainMenuController {

    @FXML
    public void handleFootballCareer(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        startLeague(stage, "football");
    }

    @FXML
    public void handleVolleyballCareer(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        startLeague(stage, "volleyball");
    }

    @FXML
    public void handleLoadGame(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load Career");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save File", "*.sav"));
        File file = chooser.showOpenDialog(stage);
        if (file == null) return;
        try {
            GameState state = SaveManager.load(file);
            new LeagueController(stage, state).show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Failed to load: " + e.getMessage()).showAndWait();
        }
    }

    private void startLeague(Stage stage, String sportType) {
        System.out.println("==== " + sportType.toUpperCase() + " LİGİ BAŞLIYOR ====");
        LeagueController temp = new LeagueController(stage, sportType, null);
        TeamSelectController teamSelect = new TeamSelectController(stage, sportType, temp.getTeams());
        teamSelect.show();
    }

    @FXML
    public void handleInstructions(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        InstructionsController ic = new InstructionsController(stage, () -> {
            try {
                Parent root = javafx.fxml.FXMLLoader.load(
                        java.util.Objects.requireNonNull(
                                getClass().getResource("/MainMenu.fxml")));
                Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
                scene.getStylesheets().add(
                        java.util.Objects.requireNonNull(
                                getClass().getResource("/style.css")).toExternalForm());
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ic.show();
    }
}