package com.sportsmanager.ui;

import com.sportsmanager.core.LeagueManager;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.MatchResult;
import com.sportsmanager.model.common.Team;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

public class ScheduleController {

    private final Stage stage;
    private final String sportType;
    private final League league;
    private final LeagueManager leagueManager;
    private final Team userTeam;
    private Runnable onBack;

    public ScheduleController(Stage stage, String sportType, League league, LeagueManager leagueManager, Team userTeam) {
        this.stage = stage;
        this.sportType = sportType;
        this.league = league;
        this.leagueManager = leagueManager;
        this.userTeam = userTeam;
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void show() {

        Rectangle topLine = new Rectangle(SCENE_WIDTH - 60, 2);
        topLine.setStyle("-fx-fill: linear-gradient(to right, transparent, #2ecc71, transparent);");

        Label title = new Label("SEASON SCHEDULE");
        title.getStyleClass().add("title-label");

        Label filterLabel = new Label("Filter by Team:");
        filterLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        ChoiceBox<String> teamFilter = new ChoiceBox<>();
        teamFilter.getStyleClass().add("custom-combo");
        teamFilter.getItems().add("All Teams");
        for (Team t : league.getTeams()) {
            teamFilter.getItems().add(t.getName());
        }
        teamFilter.setValue("All Teams");

        HBox filterBox = new HBox(10, filterLabel, teamFilter);
        filterBox.setAlignment(Pos.CENTER);

        TableView<Row> table = new TableView<>();
        table.getStyleClass().add("standings-table");
        table.setPrefHeight(450);

        TableColumn<Row, String> weekCol = new TableColumn<>("Week");
        weekCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().week)));
        weekCol.setPrefWidth(80);

        TableColumn<Row, String> homeCol = new TableColumn<>("Home Team");
        homeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().home));
        homeCol.setPrefWidth(200);

        TableColumn<Row, String> vsCol = new TableColumn<>("VS");
        vsCol.setCellValueFactory(d -> new SimpleStringProperty("vs"));
        vsCol.setPrefWidth(50);

        TableColumn<Row, String> awayCol = new TableColumn<>("Away Team");
        awayCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().away));
        awayCol.setPrefWidth(200);

        TableColumn<Row, String> scoreCol = new TableColumn<>("Result");
        scoreCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().score));
        scoreCol.setPrefWidth(120);

        table.getColumns().addAll(weekCol, homeCol, vsCol, awayCol, scoreCol);

        teamFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            updateTable(table, newV);
        });

        updateTable(table, "All Teams");


        Button backBtn = new Button("← BACK TO LEAGUE");
        backBtn.getStyleClass().add("back-button");
        backBtn.setPrefWidth(220);
        backBtn.setPrefHeight(45);
        backBtn.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        Rectangle botLine = new Rectangle(SCENE_WIDTH - 60, 2);
        botLine.setStyle("-fx-fill: linear-gradient(to right, transparent, rgba(241,196,15,0.55), transparent);");

        VBox layout = new VBox(20, topLine, title, filterBox, table, backBtn, botLine);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("main-background");

        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void updateTable(TableView<Row> table, String filter) {
        table.getItems().clear();
        Map<Integer, List<Match>> all = league.getFixture().getAllMatches();
        List<Integer> weeks = new ArrayList<>(all.keySet());
        weeks.sort(Comparator.naturalOrder());

        for (int w : weeks) {
            for (Match m : all.get(w)) {
                if (filter != null && !"All Teams".equals(filter) &&
                        !m.getHomeTeam().getName().equals(filter) &&
                        !m.getAwayTeam().getName().equals(filter)) {
                    continue;
                }
                table.getItems().add(new Row(w, m));
            }
        }
    }

    public static class Row {
        public final int week;
        public final String home;
        public final String away;
        public final String score;

        public Row(int week, Match m) {
            this.week = week;
            this.home = m.getHomeTeam().getName();
            this.away = m.getAwayTeam().getName();
            if (m.isPlayed()) {
                MatchResult r = m.getResult();
                this.score = (r != null) ? r.getTotalHomeScore() + " - " + r.getTotalAwayScore() : "Played";
            } else {
                this.score = "—";
            }
        }
    }
}