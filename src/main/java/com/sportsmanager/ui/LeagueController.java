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
import com.sportsmanager.util.NameGenerator;
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
import java.util.List;
import java.util.Random;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

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
    private final NameGenerator nameGenerator = new NameGenerator();

    private static final String[] TEAM_NAMES = {
            "Galatasaray", "Fenerbahce", "Besiktas", "Trabzonspor",
            "Basaksehir", "Sivasspor", "Konyaspor", "Antalyaspor",
            "Goztepe", "Kayserispor", "Alanyaspor", "Adana Demir",
            "Hatayspor", "Kasimpasa", "Rizespor", "Gaziantep",
            "Ankaragucu", "Pendikspor", "Samsunspor", "Istanbulspor"
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
                    ft.addPlayer(new FootballPlayer(nameGenerator.maleName(), 60 + random.nextInt(30)));
                }
                ft.addCoach(new FootballCoach(nameGenerator.coachName(), random.nextInt(10) + 1));
                team = ft;
            } else {
                VolleyballTeam vt = new VolleyballTeam(name);
                for (int i = 0; i < 12; i++) {
                    vt.addPlayer(new VolleyballPlayer(nameGenerator.femaleName(), 60 + random.nextInt(30)));
                }
                vt.addCoach(new VolleyballCoach(nameGenerator.coachName(), random.nextInt(10) + 1));
                team = vt;
            }
            teams.add(team);
        }
        league = leagueManager.createLeague(sport.getSportName() + " League", teams, sport);
    }

    public void show() {
        Label title = new Label(sport.getSportName().toUpperCase() + " LEAGUE");
        title.getStyleClass().add("game-title");

        weekLabel = new Label(weekLabelText());
        weekLabel.getStyleClass().add("week-label");

        TableView<StandingsEntry> table = createTable();

        simulateBtn = new Button("SIMULATE WEEK " + currentWeek);
        simulateBtn.setPrefWidth(220);
        simulateBtn.setPrefHeight(50);
        simulateBtn.getStyleClass().add("start-button");
        simulateBtn.setOnAction(e -> simulateWeek(table));

        Button viewMyTeam = new Button("MY TEAM");
        viewMyTeam.setPrefWidth(160);
        viewMyTeam.setPrefHeight(50);
        viewMyTeam.getStyleClass().add("menu-button");
        viewMyTeam.setOnAction(e -> {
            if (userTeam != null) {
                TeamController tc = new TeamController(stage, sportType, userTeam, league, leagueManager, userTeam);
                tc.setOnBack(this::show);
                tc.show();
            }
        });

        Button scheduleBtn = new Button("SCHEDULE");
        scheduleBtn.setPrefWidth(160);
        scheduleBtn.setPrefHeight(50);
        scheduleBtn.getStyleClass().add("menu-button");
        scheduleBtn.setOnAction(e -> {
            ScheduleController sc = new ScheduleController(stage, sportType, league, leagueManager, userTeam);
            sc.setOnBack(this::show);
            sc.show();
        });

        Button saveBtn = new Button("SAVE");
        saveBtn.setPrefWidth(120);
        saveBtn.setPrefHeight(50);
        saveBtn.getStyleClass().add("finish-button");
        saveBtn.setOnAction(e -> saveGame());

        Button backBtn = new Button("EXIT");
        backBtn.setPrefWidth(120);
        backBtn.setPrefHeight(50);
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> returnToMainMenu());

        HBox buttons = new HBox(15, simulateBtn, viewMyTeam, scheduleBtn, saveBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(25);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("main-background");
        layout.getChildren().addAll(title, weekLabel, table, buttons);

        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("League - " + sport.getSportName());
        stage.show();
    }

    private String weekLabelText() {
        int total = leagueManager.totalWeeks(league);
        if (currentWeek > total) return "SEASON COMPLETE";
        return "WEEK " + currentWeek + " / " + total;
    }

    private TableView<StandingsEntry> createTable() {
        TableView<StandingsEntry> table = new TableView<>();
        table.getStyleClass().add("standings-table");

        TableColumn<StandingsEntry, String> teamCol = new TableColumn<>("TEAM");
        teamCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTeam().getName().toUpperCase()));
        teamCol.setPrefWidth(220);

        TableColumn<StandingsEntry, Integer> playedCol = new TableColumn<>("P");
        playedCol.setCellValueFactory(new PropertyValueFactory<>("played"));
        playedCol.setPrefWidth(60);

        TableColumn<StandingsEntry, Integer> winsCol = new TableColumn<>("W");
        winsCol.setCellValueFactory(new PropertyValueFactory<>("wins"));
        winsCol.setPrefWidth(60);

        TableColumn<StandingsEntry, Integer> drawsCol = new TableColumn<>("D");
        drawsCol.setCellValueFactory(new PropertyValueFactory<>("draws"));
        drawsCol.setPrefWidth(60);

        TableColumn<StandingsEntry, Integer> lossesCol = new TableColumn<>("L");
        lossesCol.setCellValueFactory(new PropertyValueFactory<>("losses"));
        lossesCol.setPrefWidth(60);

        TableColumn<StandingsEntry, Integer> gdCol = new TableColumn<>("GD");
        gdCol.setCellValueFactory(new PropertyValueFactory<>("goalDifference"));
        gdCol.setPrefWidth(70);

        TableColumn<StandingsEntry, Integer> pointsCol = new TableColumn<>("PTS");
        pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        pointsCol.setPrefWidth(80);

        table.getColumns().addAll(teamCol, playedCol, winsCol, drawsCol, lossesCol, gdCol, pointsCol);
        table.setRowFactory(tv -> {
            TableRow<StandingsEntry> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getClickCount() == 2) {
                    TeamController tc = new TeamController(stage, sportType, row.getItem().getTeam(), league, leagueManager, userTeam);
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

        List<Match> weekMatches = league.getFixture().getMatchesByWeek(currentWeek);
        if (weekMatches == null || weekMatches.isEmpty()) {
            advanceWeek(table);
            return;
        }

        Match userMatch = null;
        for (Match match : weekMatches) {
            if (userTeam != null && (match.getHomeTeam().equals(userTeam) || match.getAwayTeam().equals(userTeam))) {
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
                MatchController mc = new MatchController(stage, sportType, um, league, leagueManager, userTeam);
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

    private void advanceWeek(TableView<StandingsEntry> table) {
        leagueManager.decrementInjuries(league);
        currentWeek++;
        weekLabel.setText(weekLabelText());
        int total = leagueManager.totalWeeks(league);
        if (currentWeek > total) {
            simulateBtn.setText("SEASON ENDED");
            updateTable(table);
            handleSeasonEnd();
        } else {
            simulateBtn.setText("SIMULATE WEEK " + currentWeek);
            updateTable(table);
        }
    }

    private void handleSeasonEnd() {
        leagueManager.sortStandings(league);
        StandingsEntry winnerEntry = league.getStandings().get(0);

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/Championship.fxml"));
            javafx.scene.Parent root = loader.load();

            ChampionshipController controller = loader.getController();
            controller.initData(stage, winnerEntry);
            controller.setOnNewSeason(this::startNewSeason);

            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("CHAMPIONS: " + winnerEntry.getTeam().getName());
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.INFORMATION, "Winner: " + winnerEntry.getTeam().getName()).showAndWait();
            returnToMainMenu();
        }
    }

    private void startNewSeason() {
        currentWeek = 1;
        leagueManager.startNewSeason(league, sport);
        show();
    }

    private void updateTable(TableView<StandingsEntry> table) {
        leagueManager.sortStandings(league);
        table.getItems().clear();
        table.getItems().addAll(league.getStandings());
    }

    private void saveGame() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Career");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save File", "*.sav"));
        fc.setInitialFileName("career_save.sav");
        File file = fc.showSaveDialog(stage);
        if (file == null) return;

        GameState state = new GameState(sportType, league, userTeam, currentWeek);
        try {
            SaveManager.save(file, state);
            new Alert(Alert.AlertType.INFORMATION, "Career saved successfully.").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Save failed: " + ex.getMessage()).showAndWait();
        }
    }

    private void returnToMainMenu() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/MainMenu.fxml"));
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setTitle("Sports Manager - Ultimate Edition");
            stage.setScene(scene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}