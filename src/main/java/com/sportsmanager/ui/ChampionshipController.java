package com.sportsmanager.ui;

import com.sportsmanager.model.common.StandingsEntry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.Objects;

import static com.sportsmanager.Main.SCENE_WIDTH;
import static com.sportsmanager.Main.SCENE_HEIGHT;

public class ChampionshipController {

    @FXML private Label winnerTeamLabel;
    @FXML private Label statsLabel;

    private Stage stage;

    public void initData(Stage stage, StandingsEntry winner) {
        this.stage = stage;
        winnerTeamLabel.setText(winner.getTeam().getName().toUpperCase());
        statsLabel.setText(winner.getPoints() + " Points  |  " +
                winner.getWins() + " Wins  |  " +
                winner.getDraws() + " Draws");
    }

    @FXML
    private void handleReturnToMenu() {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("/MainMenu.fxml")));
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
            String css = Objects.requireNonNull(
                    getClass().getResource("/style.css")).toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("Sports Manager - Ultimate Edition");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}