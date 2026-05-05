package com.sportsmanager.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuController {

    private Stage stage;

    public MainMenuController(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Label title = new Label("Sports Manager");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Label subtitle = new Label("Select a sport to manage");
        subtitle.setStyle("-fx-font-size: 16px;");

        Button footballBtn = new Button("Football");
        footballBtn.setPrefWidth(200);
        footballBtn.setPrefHeight(50);
        footballBtn.setStyle("-fx-font-size: 16px;");
        footballBtn.setOnAction(e -> startGame("football"));

        Button volleyballBtn = new Button("Volleyball");
        volleyballBtn.setPrefWidth(200);
        volleyballBtn.setPrefHeight(50);
        volleyballBtn.setStyle("-fx-font-size: 16px;");
        volleyballBtn.setOnAction(e -> startGame("volleyball"));

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(50));
        layout.getChildren().addAll(title, subtitle, footballBtn, volleyballBtn);

        Scene scene = new Scene(layout, 600, 400);
        stage.setTitle("Sports Manager");
        stage.setScene(scene);
        stage.show();
    }

    private void startGame(String sportType) {
        LeagueController leagueController = new LeagueController(stage, sportType);
        leagueController.show();
    }
}