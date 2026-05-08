package com.sportsmanager.ui;

import com.sportsmanager.core.*;
import com.sportsmanager.interfaces.*;
import com.sportsmanager.model.common.*;
import com.sportsmanager.model.sports.football.*;
import com.sportsmanager.model.sports.volleyball.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.*;
import java.util.*;
import javafx.scene.control.TableRow;

public class LeagueController {

    private Stage stage;
    private String sportType;
    private ISport sport;
    private League league;
    private LeagueManager leagueManager;
    private int currentWeek = 1;
    private Label weekLabel;
    private Team userTeam;
    private Button simulateBtn;

    private static final String[] TEAM_NAMES = {
            "Galatasaray", "Fenerbahce", "Besiktas", "Trabzonspor",
            "Basaksehir", "Sivasspor", "Konyaspor", "Antalyaspor"
    };

    private static final String[] PLAYER_NAMES = {
            "Ahmet", "Mehmet", "Ali", "Veli", "Hasan", "Huseyin",
            "Mustafa", "Ibrahim", "Yusuf", "Emre", "Burak", "Arda"
    };

    public LeagueController(Stage stage, String sportType, Team userTeam) {
        this.stage = stage;
        this.sportType = sportType;
        this.userTeam = userTeam;
        this.sport = SportFactory.createSport(sportType);
        this.leagueManager = new LeagueManager();
        setupLeague();
    }

    public List<Team> getTeams() {
        return league.getTeams();
    }

    private void setupLeague() {
        List<Team> teams = new ArrayList<>();
        Random random = new Random();

        for (String name : TEAM_NAMES) {
            Team team;
            if (sportType.equalsIgnoreCase("football")) {
                FootballTeam ft = new FootballTeam(name);
                for (int i = 0; i < 11; i++) {
                    ft.addPlayer(new FootballPlayer(
                            PLAYER_NAMES[random.nextInt(PLAYER_NAMES.length)],
                            60 + random.nextInt(30)
                    ));
                }
                ft.addCoach(new FootballCoach("Coach", random.nextInt(10) + 1));
                team = ft;
            } else {
                VolleyballTeam vt = new VolleyballTeam(name);
                for (int i = 0; i < 6; i++) {
                    vt.addPlayer(new VolleyballPlayer(
                            PLAYER_NAMES[random.nextInt(PLAYER_NAMES.length)],
                            60 + random.nextInt(30)
                    ));
                }
                vt.addCoach(new VolleyballCoach("Coach", random.nextInt(10) + 1));
                team = vt;
            }
            teams.add(team);
        }

        league = leagueManager.createLeague(sport.getSportName() + " League", teams);
        List<StandingsEntry> standings = leagueManager.calcStandings(league);
        league.setStandings(standings);
    }

    public void show() {
        Label title = new Label(sport.getSportName() + " League");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        weekLabel = new Label("Week " + currentWeek);
        weekLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #aaaaaa;");

        TableView<StandingsEntry> table = createTable();

        simulateBtn = new Button("Simulate Week " + currentWeek);
        simulateBtn.setPrefWidth(200);
        simulateBtn.setPrefHeight(45);
        simulateBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
        simulateBtn.setOnAction(e -> simulateWeek(table));

        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(100);
        backBtn.setPrefHeight(45);
        backBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
        backBtn.setOnAction(e -> {
            MainMenuController menu = new MainMenuController(stage);
            menu.show();
        });

        HBox buttons = new HBox(15, simulateBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #1E1E2E;");
        layout.getChildren().addAll(title, weekLabel, table, buttons);

        Scene scene = new Scene(layout, 750, 550);
        stage.setScene(scene);
        stage.show();
    }

    private TableView<StandingsEntry> createTable() {
        TableView<StandingsEntry> table = new TableView<>();
        table.setStyle("-fx-background-color: #2D2D3E;");

        TableColumn<StandingsEntry, String> teamCol = new TableColumn<>("Team");
        teamCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTeam().getName()));
        teamCol.setPrefWidth(180);

        TableColumn<StandingsEntry, Integer> playedCol = new TableColumn<>("P");
        playedCol.setCellValueFactory(new PropertyValueFactory<>("played"));
        playedCol.setPrefWidth(50);

        TableColumn<StandingsEntry, Integer> winsCol = new TableColumn<>("W");
        winsCol.setCellValueFactory(new PropertyValueFactory<>("wins"));
        winsCol.setPrefWidth(50);

        TableColumn<StandingsEntry, Integer> drawsCol = new TableColumn<>("D");
        drawsCol.setCellValueFactory(new PropertyValueFactory<>("draws"));
        drawsCol.setPrefWidth(50);

        TableColumn<StandingsEntry, Integer> lossesCol = new TableColumn<>("L");
        lossesCol.setCellValueFactory(new PropertyValueFactory<>("losses"));
        lossesCol.setPrefWidth(50);

        TableColumn<StandingsEntry, Integer> pointsCol = new TableColumn<>("Pts");
        pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        pointsCol.setPrefWidth(60);

        table.getColumns().addAll(teamCol, playedCol, winsCol, drawsCol, lossesCol, pointsCol);
        table.setRowFactory(tv -> {
            TableRow<StandingsEntry> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getClickCount() == 2) {
                    TeamController tc = new TeamController(stage, sportType,
                            row.getItem().getTeam(), league, leagueManager, userTeam);
                    tc.setOnBack(this::show);
                    tc.show();
                }
            });
            return row;
        });

        updateTable(table);

        return table;
    }

    private void simulateWeek(TableView<StandingsEntry> table) {
        List<Match> weekMatches = league.getFixture().getMatchesByWeek(currentWeek);
        if (weekMatches == null || weekMatches.isEmpty()) return;

        Match userMatch = null;
        for (Match match : weekMatches) {
            if (userTeam != null && (match.getHomeTeam().equals(userTeam) ||
                    match.getAwayTeam().equals(userTeam))) {
                userMatch = match;
            } else if (!match.isPlayed()) {
                IMatchEngine engine = SportFactory.createEngine(sportType);
                GameManager gm = new GameManager(sport, engine, league);
                gm.playMatch(match);
                leagueManager.processMatchResult(league, match, sportType);
            }
        }

        if (userMatch != null && !userMatch.isPlayed()) {
            MatchController matchController = new MatchController(stage, sportType, userMatch,
                    league, leagueManager, userTeam);

            matchController.setOnFinished(() -> {
                currentWeek++;
                weekLabel.setText("Week " + currentWeek);
                simulateBtn.setText("Simulate Week " + currentWeek);
                updateTable(table);
                show();
            });
            matchController.show();
        } else {
            currentWeek++;
            weekLabel.setText("Week " + currentWeek);
            simulateBtn.setText("Simulate Week " + currentWeek);
            updateTable(table);
        }
    }

    private void updateTable(TableView<StandingsEntry> table) {
        table.getItems().clear();
        List<StandingsEntry> sorted = new ArrayList<>(league.getStandings());
        sorted.sort((a, b) -> {
            if (b.getPoints() != a.getPoints()) return b.getPoints() - a.getPoints();
            if (b.getGoalDifference() != a.getGoalDifference()) return b.getGoalDifference() - a.getGoalDifference();
            return b.getGoalsFor() - a.getGoalsFor();
        });
        table.getItems().addAll(sorted);
    }
}