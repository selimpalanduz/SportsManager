package com.sportsmanager.ui;

import com.sportsmanager.core.LeagueManager;
import com.sportsmanager.model.common.Coach;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Player;
import com.sportsmanager.model.common.Team;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TeamController {

    private final Stage stage;
    private final String sportType;
    private final Team team;
    private final League league;
    private final LeagueManager leagueManager;
    private final Team userTeam;


    private Runnable onBack;

    public TeamController(Stage stage, String sportType, Team team,
                          League league, LeagueManager leagueManager, Team userTeam) {
        this.stage = stage;
        this.sportType = sportType;
        this.team = team;
        this.league = league;
        this.leagueManager = leagueManager;
        this.userTeam = userTeam;
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void show() {
        Label title = new Label(team.getName());
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        String tag = (userTeam != null && userTeam.equals(team))
                ? "Your Team - Roster & Coaching Staff"
                : "Roster & Coaching Staff";
        Label subtitle = new Label(tag);
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #aaaaaa;");

        Label rosterLabel = new Label("Players");
        rosterLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        TableView<Player> playersTable = createPlayersTable();

        Label coachLabel = new Label("Coaches");
        coachLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        TableView<Coach> coachesTable = createCoachesTable();

        Button backBtn = new Button("Back to League");
        backBtn.setPrefWidth(180);
        backBtn.setPrefHeight(45);
        backBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> goBack());

        HBox buttons = new HBox(15, backBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #1E1E2E;");
        layout.getChildren().addAll(title, subtitle, rosterLabel, playersTable, coachLabel, coachesTable, buttons);

        Scene scene = new Scene(layout, 750, 650);
        stage.setTitle("Team - " + team.getName());
        stage.setScene(scene);
        stage.show();
    }

    private TableView<Player> createPlayersTable() {
        TableView<Player> table = new TableView<>();
        table.setStyle("-fx-background-color: #2D2D3E;");
        table.setPrefHeight(280);

        TableColumn<Player, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        nameCol.setPrefWidth(220);

        TableColumn<Player, String> positionCol = new TableColumn<>("Position");
        positionCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPosition()));
        positionCol.setPrefWidth(150);

        TableColumn<Player, String> skillCol = new TableColumn<>("Skill");
        skillCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getOverallSkill())));
        skillCol.setPrefWidth(80);

        TableColumn<Player, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> {
            Player p = d.getValue();
            if (p.isInjured()) {
                return new SimpleStringProperty("Injured (" + p.getInjuryDuration() + " games)");
            }
            return new SimpleStringProperty("Available");
        });
        statusCol.setPrefWidth(180);

        table.getColumns().addAll(nameCol, positionCol, skillCol, statusCol);
        table.getItems().addAll(team.getRoster());
        return table;
    }

    private TableView<Coach> createCoachesTable() {
        TableView<Coach> table = new TableView<>();
        table.setStyle("-fx-background-color: #2D2D3E;");
        table.setPrefHeight(140);

        TableColumn<Coach, String> nameCol = new TableColumn<>("Coach");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        nameCol.setPrefWidth(300);

        TableColumn<Coach, String> expCol = new TableColumn<>("Experience");
        expCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getExperience())));
        expCol.setPrefWidth(150);

        table.getColumns().addAll(nameCol, expCol);
        table.getItems().addAll(team.getCoaches());
        return table;
    }

    private void goBack() {
        if (onBack != null) {
            onBack.run();
            return;
        }
        new LeagueController(stage, sportType, userTeam).show();
    }
}