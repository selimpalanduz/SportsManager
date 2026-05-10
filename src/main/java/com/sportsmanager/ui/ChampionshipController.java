package com.sportsmanager.ui;

import com.sportsmanager.model.common.StandingsEntry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

public class ChampionshipController {

    @FXML private Label winnerTeamLabel;
    @FXML private Label statsLabel;

    private Stage stage;
    private Runnable onNewSeason;

    public void initData(Stage stage, StandingsEntry winner) {
        this.stage = stage;
        winnerTeamLabel.setText(winner.getTeam().getName().toUpperCase());
        statsLabel.setText(winner.getPoints() + " Points  |  " +
                winner.getWins() + " Wins  |  " +
                winner.getDraws() + " Draws");
    }

    public void setOnNewSeason(Runnable onNewSeason) {
        this.onNewSeason = onNewSeason;
        injectNewSeasonButton();
    }

    private void injectNewSeasonButton() {
        if (winnerTeamLabel == null) return;

        javafx.scene.Node node = winnerTeamLabel;
        VBox content = null;
        while (node != null) {
            if (node instanceof VBox && ((VBox) node).getAlignment() == Pos.CENTER) {
                content = (VBox) node;
                break;
            }
            node = node.getParent();
        }
        if (content == null) return;

        HBox buttonRow = null;
        for (javafx.scene.Node n : content.getChildren()) {
            if (n instanceof HBox) {
                buttonRow = (HBox) n;
            }
        }
        if (buttonRow == null) return;

        Button newSeason = new Button("⟳   PLAY NEW SEASON");
        newSeason.setPrefWidth(290);
        newSeason.setPrefHeight(54);
        newSeason.getStyleClass().add("start-button");
        newSeason.setOnAction(e -> { if (onNewSeason != null) onNewSeason.run(); });

        buttonRow.setSpacing(16);
        buttonRow.getChildren().add(0, newSeason);
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