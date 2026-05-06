package com.sportsmanager.ui;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.sportsmanager.model.common.Team;

import java.util.List;

public class TeamSelectController {

    private Stage stage;
    private String sportType;
    private List<Team> teams;

    public TeamSelectController(Stage stage, String sportType, List<Team> teams) {
        this.stage = stage;
        this.sportType = sportType;
        this.teams = teams;
    }

    public void show() {
        Label title = new Label("Select Your Team");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Choose a team to manage this season");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #aaaaaa;");

        VBox teamList = new VBox(10);
        teamList.setAlignment(Pos.CENTER);

        for (Team team : teams) {
            Button teamBtn = new Button(team.getName());
            teamBtn.setPrefWidth(300);
            teamBtn.setPrefHeight(50);
            teamBtn.setStyle("-fx-background-color: #2D2D3E; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 8; -fx-cursor: hand;");
            teamBtn.setOnMouseEntered(e ->
                teamBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 8; -fx-cursor: hand;")
            );
            teamBtn.setOnMouseExited(e ->
                teamBtn.setStyle("-fx-background-color: #2D2D3E; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 8; -fx-cursor: hand;")
            );
            teamBtn.setOnAction(e -> {
                LeagueController leagueController = new LeagueController(stage, sportType, team);
                leagueController.show();
            });
            teamList.getChildren().add(teamBtn);
        }

        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(100);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
        backBtn.setOnAction(e -> {
            MainMenuController menu = new MainMenuController(stage);
            menu.show();
        });

        VBox layout = new VBox(25);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #1E1E2E;");
        layout.getChildren().addAll(title, subtitle, teamList, backBtn);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #1E1E2E;");

        Scene scene = new Scene(scrollPane, 700, 600);
        stage.setScene(scene);
        stage.show();
    }
}