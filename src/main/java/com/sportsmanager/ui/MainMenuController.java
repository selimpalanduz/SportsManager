package com.sportsmanager.ui;

import com.sportsmanager.util.GameState;
import com.sportsmanager.util.SaveManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

public class MainMenuController implements Initializable {

    @FXML
    private Button loadGameButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (loadGameButton != null) {
            loadGameButton.setDisable(!SaveManager.hasSave());
        }
    }

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
        try {
            GameState state = SaveManager.load();
            LeagueController lc = new LeagueController(stage, state);
            lc.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Could not load saved career.\n" + ex.getMessage());
            alert.setHeaderText("Load Failed");
            alert.showAndWait();
        }
    }

    private void startLeague(Stage stage, String sportType) {
        System.out.println("==== " + sportType.toUpperCase() + " LEAGUE STARTING ====");
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