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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ScheduleController {

    private final Stage stage;
    private final String sportType;
    private final League league;
    private final LeagueManager leagueManager;
    private final Team userTeam;

    private Runnable onBack;

    public ScheduleController(Stage stage,
                              String sportType,
                              League league,
                              LeagueManager leagueManager,
                              Team userTeam) {

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

        Label title = new Label("Schedule");

        title.setStyle(
                "-fx-font-size: 26px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: white;"
        );

        ChoiceBox<String> filter = new ChoiceBox<>();

        filter.getItems().add("All Teams");

        for (Team t : league.getTeams()) {

            filter.getItems().add(t.getName());
        }

        filter.setValue(
                userTeam != null
                        ? userTeam.getName()
                        : "All Teams"
        );

        TableView<Row> table = buildTable();

        loadRows(table, filter.getValue());

        filter.setOnAction(
                e -> loadRows(table, filter.getValue())
        );

        Button back = new Button("Back");

        back.setPrefWidth(140);

        back.setPrefHeight(40);

        back.setStyle(
                "-fx-background-color: #e74c3c; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 8;"
        );

        back.setOnAction(e -> {

            if (onBack != null) {

                onBack.run();
            }
        });

        HBox top = new HBox(
                15,
                new Label("Filter:"),
                filter
        );

        top.setAlignment(Pos.CENTER_LEFT);

        VBox layout = new VBox(
                15,
                title,
                top,
                table,
                back
        );

        layout.setAlignment(Pos.TOP_CENTER);

        layout.setPadding(new Insets(25));

        layout.setStyle("-fx-background-color: #1E1E2E;");

        Scene scene = new Scene(layout, 750, 600);

        stage.setTitle("Schedule - " + league.getName());

        stage.setScene(scene);

        stage.show();
    }

    private TableView<Row> buildTable() {

        TableView<Row> table = new TableView<>();

        table.setStyle("-fx-background-color: #2D2D3E;");

        TableColumn<Row, String> wk = new TableColumn<>("Week");

        wk.setCellValueFactory(
                d -> new SimpleStringProperty(
                        String.valueOf(d.getValue().week)
                )
        );

        wk.setPrefWidth(60);

        TableColumn<Row, String> home = new TableColumn<>("Home");

        home.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().home)
        );

        home.setPrefWidth(220);

        TableColumn<Row, String> score = new TableColumn<>("Result");

        score.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().score)
        );

        score.setPrefWidth(120);

        TableColumn<Row, String> away = new TableColumn<>("Away");

        away.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().away)
        );

        away.setPrefWidth(220);

        table.getColumns().addAll(
                wk,
                home,
                score,
                away
        );

        return table;
    }

    private void loadRows(TableView<Row> table, String filter) {

        table.getItems().clear();

        Map<Integer, List<Match>> all =
                league.getFixture().getAllMatches();

        List<Integer> weeks = new ArrayList<>(all.keySet());

        weeks.sort(Comparator.naturalOrder());

        for (int w : weeks) {

            for (Match m : all.get(w)) {

                if (
                        filter != null
                                && !"All Teams".equals(filter)
                                && !m.getHomeTeam().getName().equals(filter)
                                && !m.getAwayTeam().getName().equals(filter)
                ) {

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

                this.score = (
                        r != null
                )
                        ? r.getTotalHomeScore()
                        + " - "
                        + r.getTotalAwayScore()
                        : "Played";

            } else {

                this.score = "—";
            }
        }
    }
}