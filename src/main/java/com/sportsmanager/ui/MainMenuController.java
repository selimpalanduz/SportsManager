package com.sportsmanager.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuController {

    private Stage stage;

    public MainMenuController(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Label title = new Label("SPORTSMANAGER");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("by Phoenix");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaaaaa; -fx-padding: 20 0 0 10;");

        HBox titleBox = new HBox(title, subtitle);
        titleBox.setAlignment(Pos.BOTTOM_LEFT);
        titleBox.setPadding(new Insets(0, 0, 30, 0));

        Button footballBtn = new Button("Football");
        footballBtn.setPrefWidth(200);
        footballBtn.setPrefHeight(60);
        footballBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 8; -fx-cursor: hand;");
        footballBtn.setOnAction(e -> startGame("football"));

        Button volleyballBtn = new Button("Volleyball");
        volleyballBtn.setPrefWidth(200);
        volleyballBtn.setPrefHeight(60);
        volleyballBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 8; -fx-cursor: hand;");
        volleyballBtn.setOnAction(e -> startGame("volleyball"));

        HBox buttonBox = new HBox(20, footballBtn, volleyballBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(40);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(60));
        layout.setStyle("-fx-background-color: #1E1E2E;");
        layout.getChildren().addAll(titleBox, buttonBox);

        Scene scene = new Scene(layout, 700, 450);
        stage.setTitle("Sports Manager");
        stage.setScene(scene);
        stage.show();
    }

    private void startGame(String sportType) {
    LeagueController temp = new LeagueController(stage, sportType, null);
    TeamSelectController teamSelect = new TeamSelectController(stage, sportType, temp.getTeams());
    teamSelect.show();
}
}