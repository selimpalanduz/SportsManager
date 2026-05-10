package com.sportsmanager.ui;

import com.sportsmanager.core.GameManager;
import com.sportsmanager.core.LeagueManager;
import com.sportsmanager.core.SaveManager;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.Coach;
import com.sportsmanager.model.common.GameState;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.Player;
import com.sportsmanager.model.common.StandingsEntry;
import com.sportsmanager.model.common.Team;
import com.sportsmanager.model.sports.football.FootballCoach;
import com.sportsmanager.model.sports.football.FootballPlayer;
import com.sportsmanager.model.sports.football.FootballTeam;
import com.sportsmanager.model.sports.volleyball.VolleyballCoach;
import com.sportsmanager.model.sports.volleyball.VolleyballPlayer;
import com.sportsmanager.model.sports.volleyball.VolleyballTeam;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class LeagueController {

    private Stage stage;
    private String sportType;
    private ISport sport;
    private League league;
    private LeagueManager leagueManager;
    private int currentWeek = 1;
    private Team userTeam;

    private Label weekLabel;
    private Button simulateBtn;

    private static final String[] TEAM_NAMES = {
            "Galatasaray", "Fenerbahce", "Besiktas", "Trabzonspor",
            "Basaksehir", "Sivasspor", "Konyaspor", "Antalyaspor",
            "Goztepe", "Kayserispor", "Alanyaspor", "Adana Demir",
            "Hatayspor", "Kasimpasa", "Rizespor", "Gaziantep",
            "Ankaragucu", "Pendikspor", "Samsunspor", "Istanbulspor"
    };

    private static final String[] PLAYER_NAMES = {
            "Ahmet", "Mehmet", "Ali", "Veli", "Hasan", "Huseyin",
            "Mustafa", "Ibrahim", "Yusuf", "Emre", "Burak", "Arda",
            "Cenk", "Hakan", "Kerem", "Salih", "Okan", "Caner",
            "Selim", "Deniz", "Mert", "Eren", "Baris", "Tolga",
            "Sinan", "Furkan", "Berkay", "Ozan", "Umut", "Berke"
    };

    public LeagueController(Stage stage, String sportType, Team userTeam) {
        this.stage = stage;
        this.sportType = sportType;
        this.userTeam = userTeam;
        this.sport = SportFactory.createSport(sportType);
        this.leagueManager = new LeagueManager();
        setupLeague();
    }

    public LeagueController(Stage stage, GameState state) {
        this.stage = stage;
        this.sportType = state.getSportType();
        this.sport = SportFactory.createSport(state.getSportType());
        this.leagueManager = new LeagueManager();
        this.league = state.getLeague();
        this.userTeam = state.getUserTeam();
        this.currentWeek = state.getCurrentWeek();

        for (StandingsEntry e : league.getStandings()) {
            e.setSport(sport);
        }
    }

    public List<Team> getTeams() {
        return league.getTeams();
    }

    private void setupLeague() {
        List<Team> teams = new ArrayList<>();
        Random random = new Random();

        for (String name : TEAM_NAMES) {
            Team team;

            if ("football".equalsIgnoreCase(sportType)) {
                FootballTeam ft = new FootballTeam(name);

                for (int i = 0; i < 18; i++) {
                    ft.addPlayer(new FootballPlayer(
                            PLAYER_NAMES[random.nextInt(PLAYER_NAMES.length)],
                            60 + random.nextInt(30)
                    ));
                }

                ft.addCoach(new FootballCoach("Coach " + name, random.nextInt(10) + 1));
                team = ft;

            } else {

                VolleyballTeam vt = new VolleyballTeam(name);

                for (int i = 0; i < 12; i++) {
                    vt.addPlayer(new VolleyballPlayer(
                            PLAYER_NAMES[random.nextInt(PLAYER_NAMES.length)],
                            60 + random.nextInt(30)
                    ));
                }

                vt.addCoach(new VolleyballCoach("Coach " + name, random.nextInt(10) + 1));
                team = vt;
            }

            teams.add(team);
        }

        league = leagueManager.createLeague(sport.getSportName() + " League", teams, sport);
    }

    public void show() {

        Label title = new Label(sport.getSportName() + " League");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        weekLabel = new Label(weekLabelText());
        weekLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #aaaaaa;");

        TableView<StandingsEntry> table = createTable();

        simulateBtn = new Button("Simulate Week " + currentWeek);
        simulateBtn.setPrefWidth(200);
        simulateBtn.setPrefHeight(45);
        simulateBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
        simulateBtn.setOnAction(e -> simulateWeek(table));

        Button viewMyTeam = new Button("My Team");
        viewMyTeam.setPrefWidth(140);
        viewMyTeam.setPrefHeight(45);
        viewMyTeam.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");

        viewMyTeam.setOnAction(e -> {
            if (userTeam != null) {
                TeamController tc = new TeamController(stage, sportType, userTeam, league, leagueManager, userTeam);
                tc.setOnBack(this::show);
                tc.show();
            }
        });

        Button scheduleBtn = new Button("Schedule");
        scheduleBtn.setPrefWidth(120);
        scheduleBtn.setPrefHeight(45);
        scheduleBtn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");

        scheduleBtn.setOnAction(e -> {
            ScheduleController sc = new ScheduleController(stage, sportType, league, leagueManager, userTeam);
            sc.setOnBack(this::show);
            sc.show();
        });

        Button saveBtn = new Button("Save");
        saveBtn.setPrefWidth(100);
        saveBtn.setPrefHeight(45);
        saveBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
        saveBtn.setOnAction(e -> saveGame());

        Button backBtn = new Button("Main Menu");
        backBtn.setPrefWidth(120);
        backBtn.setPrefHeight(45);
        backBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
        backBtn.setOnAction(e -> new MainMenuController(stage).show());

        HBox buttons = new HBox(15, simulateBtn, viewMyTeam, scheduleBtn, saveBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #1E1E2E;");
        layout.getChildren().addAll(title, weekLabel, table, buttons);

        Scene scene = new Scene(layout, 850, 600);

        stage.setScene(scene);
        stage.setTitle("League - " + sport.getSportName());
        stage.show();
    }

    private String weekLabelText() {

        int total = leagueManager.totalWeeks(league);

        if (currentWeek > total) {
            return "Season finished";
        }

        return "Week " + currentWeek + " / " + total;
    }

    private TableView<StandingsEntry> createTable() {

        TableView<StandingsEntry> table = new TableView<>();
        table.setStyle("-fx-background-color: #2D2D3E;");

        TableColumn<StandingsEntry, String> teamCol = new TableColumn<>("Team");
        teamCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTeam().getName()));
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

        TableColumn<StandingsEntry, Integer> gfCol = new TableColumn<>("GF");
        gfCol.setCellValueFactory(new PropertyValueFactory<>("goalsFor"));
        gfCol.setPrefWidth(55);

        TableColumn<StandingsEntry, Integer> gaCol = new TableColumn<>("GA");
        gaCol.setCellValueFactory(new PropertyValueFactory<>("goalsAgainst"));
        gaCol.setPrefWidth(55);

        TableColumn<StandingsEntry, Integer> gdCol = new TableColumn<>("GD");
        gdCol.setCellValueFactory(new PropertyValueFactory<>("goalDifference"));
        gdCol.setPrefWidth(55);

        TableColumn<StandingsEntry, Integer> pointsCol = new TableColumn<>("Pts");
        pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        pointsCol.setPrefWidth(60);

        table.getColumns().addAll(
                teamCol,
                playedCol,
                winsCol,
                drawsCol,
                lossesCol,
                gfCol,
                gaCol,
                gdCol,
                pointsCol
        );

        table.setRowFactory(tv -> {

            TableRow<StandingsEntry> row = new TableRow<>();

            row.setOnMouseClicked(e -> {

                if (!row.isEmpty() && e.getClickCount() == 2) {

                    TeamController tc = new TeamController(
                            stage,
                            sportType,
                            row.getItem().getTeam(),
                            league,
                            leagueManager,
                            userTeam
                    );

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

        int total = leagueManager.totalWeeks(league);

        if (currentWeek > total) {
            handleSeasonEnd();
            return;
        }

        runTrainingPhase();

        List<Match> weekMatches = league.getFixture().getMatchesByWeek(currentWeek);

        if (weekMatches == null || weekMatches.isEmpty()) {
            advanceWeek(table);
            return;
        }

        Match userMatch = null;

        for (Match match : weekMatches) {

            if (userTeam != null &&
                    (match.getHomeTeam().equals(userTeam) ||
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

            final Match um = userMatch;

            LineupController lc = new LineupController(stage, sport, userTeam, um);

            lc.setOnConfirm(() -> {

                MatchController mc = new MatchController(
                        stage,
                        sportType,
                        um,
                        league,
                        leagueManager,
                        userTeam
                );

                mc.setOnFinished(() -> {
                    advanceWeek(table);
                    show();
                });

                mc.show();
            });

            lc.setOnCancel(this::show);

            lc.show();

        } else {

            advanceWeek(table);
        }
    }

    private void runTrainingPhase() {

        for (Team t : league.getTeams()) {

            for (Coach c : t.getCoaches()) {

                for (Player p : t.getRoster()) {

                    c.trainPlayer(p);
                }
            }
        }
    }

    private void advanceWeek(TableView<StandingsEntry> table) {

        for (Team t : league.getTeams()) {

            for (Player p : t.getRoster()) {

                p.decrementInjury();
            }
        }

        currentWeek++;

        weekLabel.setText(weekLabelText());

        int total = leagueManager.totalWeeks(league);

        if (currentWeek > total) {

            simulateBtn.setText("Season Ended");

            updateTable(table);

            handleSeasonEnd();

        } else {

            simulateBtn.setText("Simulate Week " + currentWeek);

            updateTable(table);
        }
    }

    private void handleSeasonEnd() {

        List<StandingsEntry> sorted = sortedStandings();

        Team champion = sorted.isEmpty() ? null : sorted.get(0).getTeam();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Season Finished");

        alert.setHeaderText(
                champion != null
                        ? champion.getName() + " are the champions!"
                        : "Season finished"
        );

        StringBuilder body = new StringBuilder("Final standings:\n\n");

        int rank = 1;

        for (StandingsEntry e : sorted) {

            body.append(rank++)
                    .append(". ")
                    .append(e.getTeam().getName())
                    .append("  -  ")
                    .append(e.getPoints())
                    .append(" pts\n");
        }

        body.append("\nStart a new season with the same teams?");

        alert.setContentText(body.toString());

        javafx.scene.control.ButtonType yes =
                new javafx.scene.control.ButtonType("New Season");

        javafx.scene.control.ButtonType no =
                new javafx.scene.control.ButtonType("Back to Menu");

        alert.getButtonTypes().setAll(yes, no);

        alert.showAndWait().ifPresent(bt -> {

            if (bt == yes) {
                startNewSeason();
            } else {
                new MainMenuController(stage).show();
            }
        });
    }

    private void startNewSeason() {

        currentWeek = 1;

        for (Team t : league.getTeams()) {

            t.setPoints(0);

            for (Player p : t.getRoster()) {

                p.setInjuryDuration(0);
            }
        }

        League fresh = leagueManager.createLeague(
                league.getName(),
                new ArrayList<>(league.getTeams()),
                sport
        );

        this.league = fresh;

        show();
    }

    private List<StandingsEntry> sortedStandings() {

        List<StandingsEntry> sorted =
                new ArrayList<>(league.getStandings());

        sorted.sort(
                Comparator
                        .comparingInt(StandingsEntry::getPoints).reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        StandingsEntry::getGoalDifference
                                ).reversed()
                        )
                        .thenComparing(
                                Comparator.comparingInt(
                                        StandingsEntry::getGoalsFor
                                ).reversed()
                        )
                        .thenComparing(
                                e -> e.getTeam().getName()
                        )
        );

        return sorted;
    }

    private void updateTable(TableView<StandingsEntry> table) {

        table.getItems().clear();

        table.getItems().addAll(sortedStandings());
    }

    private void saveGame() {

        FileChooser fc = new FileChooser();

        fc.setTitle("Save Game");

        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Save File", "*.sav")
        );

        fc.setInitialFileName("sportsmanager.sav");

        File file = fc.showSaveDialog(stage);

        if (file == null) return;

        GameState state = new GameState(
                sportType,
                league,
                userTeam,
                currentWeek
        );

        try {

            SaveManager.save(file, state);

            new Alert(
                    Alert.AlertType.INFORMATION,
                    "Game saved."
            ).showAndWait();

        } catch (Exception ex) {

            new Alert(
                    Alert.AlertType.ERROR,
                    "Save failed: " + ex.getMessage()
            ).showAndWait();
        }
    }
}