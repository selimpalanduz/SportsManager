package com.sportsmanager.ui;

import com.sportsmanager.core.GameManager;
import com.sportsmanager.core.LeagueManager;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.util.GameState;
import com.sportsmanager.util.SaveManager;

import com.sportsmanager.util.NameGenerator;

import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.StandingsEntry;
import com.sportsmanager.model.common.Team;
import com.sportsmanager.model.sports.football.FootballCoach;
import com.sportsmanager.model.sports.football.FootballPlayer;
import com.sportsmanager.model.sports.football.FootballTeam;
import com.sportsmanager.model.sports.volleyball.VolleyballCoach;
import com.sportsmanager.model.sports.volleyball.VolleyballPlayer;
import com.sportsmanager.model.sports.volleyball.VolleyballTeam;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

public class LeagueController {

    private final Stage stage;
    private final String sportType;
    private final ISport sport;
    private League league;
    private final LeagueManager leagueManager;
    private int currentWeek = 1;
    private int totalWeeks;
    private Label weekLabel;
    private final Team userTeam;
    private Button simulateBtn;

    private static final String[] TEAM_NAMES = {
            "Galatasaray", "Fenerbahce", "Besiktas", "Trabzonspor",
            "Basaksehir", "Sivasspor", "Konyaspor", "Antalyaspor",
            "Goztepe", "Adana Demirspor", "Alanyaspor", "Hatayspor",
            "Kayserispor", "Gaziantep FK", "Kasimpasa", "Rizespor",
            "Bodrumspor", "Pendikspor", "Samsunspor", "Istanbulspor"
    };

    public LeagueController(Stage stage, String sportType, Team userTeam) {
        this.stage = stage;
        this.sportType = sportType;
        this.userTeam = userTeam;
        this.sport = SportFactory.createSport(sportType);
        this.leagueManager = new LeagueManager();
        setupLeague();
        this.totalWeeks = leagueManager.totalWeeks(league);
    }

    public LeagueController(Stage stage, GameState state) {
        this.stage = stage;
        this.sportType = state.getSportType();
        this.sport = SportFactory.createSport(sportType);
        this.leagueManager = new LeagueManager();
        this.league = state.getLeague();
        this.currentWeek = state.getCurrentWeek();
        this.totalWeeks = leagueManager.totalWeeks(league);

        Team picked = null;
        if (state.getUserTeamName() != null) {
            for (Team t : league.getTeams()) {
                if (t.getName().equals(state.getUserTeamName())) {
                    picked = t;
                    break;
                }
            }
        }
        this.userTeam = picked;
    }

    public List<Team> getTeams() {
        return league.getTeams();
    }

    private String cssUrl() {
        return Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
    }

    private void setupLeague() {
        List<Team> teams = new ArrayList<>();
        Random random = new Random();
        NameGenerator nameGen = new NameGenerator(sportType.equalsIgnoreCase("volleyball"));

        String[] footballPositions = {
            "Goalkeeper", "Defender", "Defender", "Defender", "Defender",
            "Midfielder", "Midfielder", "Midfielder", "Midfielder",
            "Forward", "Forward"
        };

        String[] volleyballPositions = {
            "Setter", "Libero", "Outside Hitter", "Outside Hitter",
            "Middle Blocker", "Opposite"
        };

        for (String name : TEAM_NAMES) {
            Team team;
            if (sportType.equalsIgnoreCase("football")) {
                FootballTeam ft = new FootballTeam(name);
                for (int i = 0; i < 11; i++) {
                    FootballPlayer p = new FootballPlayer(
                        nameGen.generate(),
                        60 + random.nextInt(30),
                        footballPositions[i]
                    );
                    ft.addPlayer(p);
                }
                ft.addCoach(new FootballCoach(nameGen.generateCoachName(), random.nextInt(10) + 1));
                team = ft;
            } else {
                VolleyballTeam vt = new VolleyballTeam(name);
                for (int i = 0; i < 6; i++) {
                    VolleyballPlayer p = new VolleyballPlayer(
                        nameGen.generate(),
                        60 + random.nextInt(30),
                        volleyballPositions[i]
                    );
                    vt.addPlayer(p);
                }
                vt.addCoach(new VolleyballCoach(nameGen.generateCoachName(), random.nextInt(10) + 1));
                team = vt;
            }
            teams.add(team);
        }

        league = leagueManager.createLeague(sport.getSportName() + " League", teams);
        List<StandingsEntry> standings = leagueManager.calcStandings(league);
        league.setStandings(standings);
    }

    public void show() {
        Rectangle topLine = new Rectangle(SCENE_WIDTH - 60, 2);
        topLine.setStyle("-fx-fill: linear-gradient(to right, transparent, rgba(46,204,113,0.50), transparent);");

        Label badge = new Label(sport.getSportName().toUpperCase() + "  ·  LEAGUE");
        badge.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;"
                + "-fx-text-fill: rgba(46,204,113,0.75); -fx-letter-spacing: 4px;");

        Label title = new Label(sport.getSportName().toUpperCase() + " LEAGUE");
        title.getStyleClass().add("title-label");

        weekLabel = new Label(currentWeek > totalWeeks ? "SEASON COMPLETE" : "WEEK  " + currentWeek);
        weekLabel.getStyleClass().add("week-label");

        VBox header = new VBox(4, badge, title, weekLabel);
        header.setAlignment(Pos.CENTER);

        TableView<StandingsEntry> table = createTable();

        simulateBtn = new Button();
        updateSimulateButton();
        simulateBtn.setPrefWidth(220);
        simulateBtn.setPrefHeight(48);
        simulateBtn.setOnAction(e -> simulateWeek(table));
        
        Button myTeamBtn = new Button("👤  MY TEAM");
        myTeamBtn.setPrefWidth(180);
        myTeamBtn.setPrefHeight(50);
        myTeamBtn.getStyleClass().add("back-button");
        myTeamBtn.setOnAction(e -> {
            if (userTeam != null) {
                Team teamInLeague = null;
                for (Team t : league.getTeams()) {
                    if (t.getName().equals(userTeam.getName())) {
                        teamInLeague = t;
                        break;
                    }
                }
                if (teamInLeague != null) {
                    TeamController tc = new TeamController(stage, sportType, teamInLeague, league, leagueManager, teamInLeague);
                    tc.setOnBack(() -> show());
                    tc.show();
                }
            }
        });

        Button scheduleBtn = new Button("📅  SCHEDULE");
        scheduleBtn.setPrefWidth(160);
        scheduleBtn.setPrefHeight(48);
        scheduleBtn.getStyleClass().add("menu-button");
        scheduleBtn.setOnAction(e -> openSchedule());

        Button saveBtn = new Button("💾  SAVE");
        saveBtn.setPrefWidth(140);
        saveBtn.setPrefHeight(48);
        saveBtn.getStyleClass().add("menu-button");
        saveBtn.setOnAction(e -> saveGame());

        Button backBtn = new Button("← BACK");
        backBtn.setPrefWidth(120);
        backBtn.setPrefHeight(48);
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> goToMainMenu());

        HBox buttons = new HBox(12, simulateBtn,myTeamBtn, scheduleBtn, saveBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        Rectangle botLine = new Rectangle(SCENE_WIDTH - 60, 2);
        botLine.setStyle("-fx-fill: linear-gradient(to right, transparent, rgba(241,196,15,0.38), transparent);");

        VBox content = new VBox(20, topLine, header, table, buttons, botLine);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setStyle("-fx-background-color: transparent;");

        StackPane root = new StackPane(content);
        root.getStyleClass().add("main-background");

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(cssUrl());
        stage.setTitle("Sports Manager - Ultimate Edition  ·  League");
        stage.setScene(scene);
        stage.show();
    }

    private void updateSimulateButton() {
        if (currentWeek > totalWeeks) {
            simulateBtn.setText("🏆  VIEW CHAMPION");
            simulateBtn.getStyleClass().setAll("champion-button");
        } else {
            simulateBtn.setText("▶  SIMULATE  WEEK  " + currentWeek);
            simulateBtn.getStyleClass().setAll("start-button");
        }
    }

    private TableView<StandingsEntry> createTable() {
        TableView<StandingsEntry> table = new TableView<>();
        table.getStyleClass().add("standings-table");
        table.setFixedCellSize(40);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<StandingsEntry, String> rankCol = new TableColumn<>("RANK");
        rankCol.setCellValueFactory(d -> new SimpleStringProperty(""));
        rankCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); setStyle(""); return; }
                int idx = getIndex() + 1;
                String prefix = idx == 1 ? "🏆 " : idx == 2 ? "🥈 " : idx == 3 ? "🥉 " : "";
                setText(prefix + idx);
                String color = idx == 1 ? "rgba(241,196,15,0.95)"
                        : idx == 2 ? "rgba(220,220,220,0.92)"
                        : idx == 3 ? "rgba(205,127,50,0.92)"
                        : "rgba(255,255,255,0.55)";
                setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;"
                        + "-fx-font-size: 14px; -fx-alignment: center;");
            }
        });
        rankCol.setPrefWidth(80);
        rankCol.setResizable(false);

        TableColumn<StandingsEntry, String> teamCol = new TableColumn<>("TEAM");
        teamCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTeam().getName()));
        teamCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                boolean isUser = userTeam != null && item.equals(userTeam.getName());
                setText(isUser ? "▶  " + item.toUpperCase() : item.toUpperCase());
                setStyle(isUser
                        ? "-fx-text-fill: rgba(46,204,113,0.95); -fx-font-weight: bold;"
                        + "-fx-alignment: center-left; -fx-padding: 0 0 0 10;"
                        : "-fx-text-fill: rgba(255,255,255,0.85); -fx-font-weight: bold;"
                        + "-fx-alignment: center-left; -fx-padding: 0 0 0 10;");
            }
        });
        teamCol.setPrefWidth(195);

        TableColumn<StandingsEntry, Integer> playedCol = makeIntCol("P", "played", 40);
        TableColumn<StandingsEntry, Integer> winsCol   = makeIntCol("W", "wins",   40);
        TableColumn<StandingsEntry, Integer> drawsCol  = makeIntCol("D", "draws",  40);
        TableColumn<StandingsEntry, Integer> lossesCol = makeIntCol("L", "losses", 40);
        TableColumn<StandingsEntry, Integer> gfCol     = makeIntCol("GF", "goalsFor", 44);
        TableColumn<StandingsEntry, Integer> gaCol     = makeIntCol("GA", "goalsAgainst", 44);

        TableColumn<StandingsEntry, Integer> gdCol = new TableColumn<>("GD");
        gdCol.setCellValueFactory(d -> {
            int gd = d.getValue().getGoalDifference();
            return new javafx.beans.property.SimpleObjectProperty<>(gd);
        });
        gdCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                String prefix = item > 0 ? "+" : "";
                setText(prefix + item);
                setStyle("-fx-alignment: center; -fx-font-weight: bold;"
                        + "-fx-text-fill: " + (item > 0 ? "rgba(46,204,113,0.92)"
                        : item < 0 ? "rgba(231,76,60,0.92)" : "rgba(255,255,255,0.70)") + ";");
            }
        });
        gdCol.setPrefWidth(50);

        TableColumn<StandingsEntry, Integer> pointsCol = new TableColumn<>("PTS");
        pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        pointsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(String.valueOf(item));
                setStyle("-fx-text-fill: rgba(241,196,15,0.95);"
                        + "-fx-font-weight: bold; -fx-font-size: 15px;"
                        + "-fx-alignment: center;");
            }
        });
        pointsCol.setPrefWidth(54);

        TableColumn<StandingsEntry, Void> actionCol = new TableColumn<>("");
        actionCol.setPrefWidth(44);
        actionCol.setResizable(false);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("⚙");
            {
                applyStyle(false);
                btn.setOnMouseEntered(e -> applyStyle(true));
                btn.setOnMouseExited(e -> applyStyle(false));
                btn.setOnAction(event -> {
                    StandingsEntry entry = getTableView().getItems().get(getIndex());
                    TeamController tc = new TeamController(
                            stage, sportType, entry.getTeam(), league, leagueManager, userTeam);
                    tc.setOnBack(LeagueController.this::show);
                    tc.show();
                });
            }
            private void applyStyle(boolean h) {
                btn.setStyle(h
                        ? "-fx-background-color: rgba(255,255,255,0.14);"
                        + "-fx-border-color: rgba(255,255,255,0.35); -fx-border-width:1;"
                        + "-fx-border-radius:6; -fx-background-radius:6;"
                        + "-fx-text-fill:white; -fx-font-size:13; -fx-cursor:hand;"
                        : "-fx-background-color: rgba(255,255,255,0.06);"
                        + "-fx-border-color: rgba(255,255,255,0.14); -fx-border-width:1;"
                        + "-fx-border-radius:6; -fx-background-radius:6;"
                        + "-fx-text-fill:rgba(255,255,255,0.60); -fx-font-size:13; -fx-cursor:hand;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(rankCol, actionCol, teamCol,
                playedCol, winsCol, drawsCol, lossesCol,
                gfCol, gaCol, gdCol, pointsCol);

        table.setRowFactory(tv -> {
            TableRow<StandingsEntry> row = new TableRow<>() {
                @Override
                protected void updateItem(StandingsEntry item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) { setStyle(""); return; }
                    boolean isUser = userTeam != null
                            && item.getTeam().getName().equals(userTeam.getName());
                    if (getIndex() == 0) {
                        setStyle("-fx-border-color: transparent transparent transparent"
                                + " rgba(241,196,15,0.55); -fx-border-width: 0 0 0 3;");
                    } else if (isUser) {
                        setStyle("-fx-border-color: transparent transparent transparent"
                                + " rgba(46,204,113,0.65); -fx-border-width: 0 0 0 3;");
                    } else {
                        setStyle("");
                    }
                }
            };
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getClickCount() == 2) {
                    TeamController tc = new TeamController(
                            stage, sportType, row.getItem().getTeam(),
                            league, leagueManager, userTeam);
                    tc.setOnBack(this::show);
                    tc.show();
                }
            });
            return row;
        });

        updateTable(table);
        return table;
    }

    private TableColumn<StandingsEntry, Integer> makeIntCol(String title, String prop, double w) {
        TableColumn<StandingsEntry, Integer> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : String.valueOf(item));
                setStyle("-fx-alignment: center; -fx-text-fill: rgba(255,255,255,0.78);");
            }
        });
        col.setPrefWidth(w);
        return col;
    }

    private void simulateWeek(TableView<StandingsEntry> table) {
        if (currentWeek > totalWeeks) { checkChampionship(); return; }

        leagueManager.runWeeklyTraining(league);

        List<Match> weekMatches = league.getFixture().getMatchesByWeek(currentWeek);
        if (weekMatches == null || weekMatches.isEmpty()) {
            currentWeek++;
            checkChampionship();
            return;
        }

        Match userMatch = null;
        for (Match match : weekMatches) {
            if (userTeam != null
                    && match.getHomeTeam() != null && match.getAwayTeam() != null
                    && (match.getHomeTeam().getName().equals(userTeam.getName())
                    || match.getAwayTeam().getName().equals(userTeam.getName()))) {
                userMatch = match;
                continue;
            }
            if (!match.isPlayed()) {
                IMatchEngine engine = SportFactory.createEngine(sportType);
                GameManager gm = new GameManager(sport, engine, league);
                gm.playMatch(match);
                leagueManager.processMatchResult(league, match, sportType);
            }
        }

        if (userMatch != null && !userMatch.isPlayed()) {
            final Match um = userMatch;
            MatchController mc = new MatchController(
                    stage, sportType, um, league, leagueManager, userTeam);
            mc.setOnFinished(() -> {
                afterWeek();
                Platform.runLater(() -> {
                    show();
                    checkChampionship();
                });
            });
            mc.show();
        } else {
            afterWeek();
            updateTable(table);
            checkChampionship();
            updateUIState();
        }
    }

    private void afterWeek() {
        leagueManager.decrementInjuries(league);
        leagueManager.sortStandings(league);
        currentWeek++;
    }

    private void updateUIState() {
        weekLabel.setText(currentWeek > totalWeeks ? "SEASON COMPLETE" : "WEEK  " + currentWeek);
        updateSimulateButton();
    }

    private void checkChampionship() {
        if (currentWeek > totalWeeks) {
            try {
                StandingsEntry winner = league.getStandings().get(0);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Championship.fxml"));
                Parent root = loader.load();
                ChampionshipController ctrl = loader.getController();
                ctrl.initData(stage, winner);
                Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
                scene.getStylesheets().add(cssUrl());
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTable(TableView<StandingsEntry> table) {
        table.getItems().clear();
        leagueManager.sortStandings(league);
        table.getItems().addAll(new ArrayList<>(league.getStandings()));
    }

    private void openSchedule() {
        ScheduleController sc = new ScheduleController(stage, league, userTeam, currentWeek);
        sc.setOnBack(this::show);
        sc.show();
    }

    private void saveGame() {
        try {
            String userName = userTeam != null ? userTeam.getName() : null;
            GameState state = new GameState(league, sportType, userName, currentWeek);
            SaveManager.save(state);
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Career saved.");
            ok.setHeaderText("Save Successful");
            ok.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR, "Save failed: " + ex.getMessage());
            err.setHeaderText("Save Failed");
            err.showAndWait();
        }
    }

    private void goToMainMenu() {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("/MainMenu.fxml")));
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
            scene.getStylesheets().add(cssUrl());
            stage.setTitle("Sports Manager - Ultimate Edition");
            stage.setScene(scene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}