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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.beans.property.*;
import java.util.*;
import javafx.scene.control.TableRow;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.application.Platform;

import static com.sportsmanager.Main.SCENE_WIDTH;
import static com.sportsmanager.Main.SCENE_HEIGHT;

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
        this.stage         = stage;
        this.sportType     = sportType;
        this.userTeam      = userTeam;
        this.sport         = SportFactory.createSport(sportType);
        this.leagueManager = new LeagueManager();
        setupLeague();
    }

    public List<Team> getTeams() {
        return league.getTeams();
    }

    private String cssUrl() {
        return Objects.requireNonNull(
                getClass().getResource("/style.css")).toExternalForm();
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
                            60 + random.nextInt(30)));
                }
                ft.addCoach(new FootballCoach("Coach", random.nextInt(10) + 1));
                team = ft;
            } else {
                VolleyballTeam vt = new VolleyballTeam(name);
                for (int i = 0; i < 6; i++) {
                    vt.addPlayer(new VolleyballPlayer(
                            PLAYER_NAMES[random.nextInt(PLAYER_NAMES.length)],
                            60 + random.nextInt(30)));
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

        // ── Top line ─────────────────────────────────────────────
        Rectangle topLine = new Rectangle(SCENE_WIDTH - 60, 2);
        topLine.setStyle(
                "-fx-fill: linear-gradient(to right," +
                        " transparent, rgba(46,204,113,0.50), transparent);");

        // ── Header ───────────────────────────────────────────────
        Label badge = new Label(sport.getSportName().toUpperCase() + "  ·  LEAGUE");
        badge.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: bold;" +
                        "-fx-text-fill: rgba(46,204,113,0.75);" +
                        "-fx-letter-spacing: 4px;");

        Label title = new Label(sport.getSportName().toUpperCase() + " LEAGUE");
        title.getStyleClass().add("title-label");

        weekLabel = new Label(currentWeek > 14 ? "SEASON COMPLETE" : "WEEK  " + currentWeek);
        weekLabel.getStyleClass().add("week-label");

        VBox header = new VBox(4, badge, title, weekLabel);
        header.setAlignment(Pos.CENTER);

        // ── Standings table ───────────────────────────────────────
        TableView<StandingsEntry> table = createTable();

        // ── Buttons ───────────────────────────────────────────────
        simulateBtn = new Button();
        updateSimulateButton();
        simulateBtn.setPrefWidth(240);
        simulateBtn.setPrefHeight(50);
        simulateBtn.setOnAction(e -> simulateWeek(table));

        Button backBtn = new Button("← BACK");
        backBtn.setPrefWidth(130);
        backBtn.setPrefHeight(50);
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
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
        });

        HBox buttons = new HBox(16, simulateBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        // ── Bottom line ───────────────────────────────────────────
        Rectangle botLine = new Rectangle(SCENE_WIDTH - 60, 2);
        botLine.setStyle(
                "-fx-fill: linear-gradient(to right," +
                        " transparent, rgba(241,196,15,0.38), transparent);");

        // ── Content (transparent) ─────────────────────────────────
        VBox content = new VBox(20, topLine, header, table, buttons, botLine);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setStyle("-fx-background-color: transparent;");

        // ── ROOT — stadium background ─────────────────────────────
        StackPane root = new StackPane(content);
        root.getStyleClass().add("main-background");

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(cssUrl());
        stage.setTitle("Sports Manager - Ultimate Edition  ·  League");
        stage.setScene(scene);
        stage.show();
    }

    private void updateSimulateButton() {
        if (currentWeek > 14) {
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
        table.setFixedCellSize(46);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // # rank
        TableColumn<StandingsEntry, String> rankCol = new TableColumn<>("#");
        rankCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); setStyle(""); return; }
                int idx = getIndex() + 1;
                setText(String.valueOf(idx));
                String color = idx == 1 ? "rgba(241,196,15,0.90)"
                        : idx == 2 ? "rgba(200,200,200,0.75)"
                        : idx == 3 ? "rgba(205,127,50,0.80)"
                        :            "rgba(255,255,255,0.35)";
                setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;" +
                        "-fx-alignment: center;");
            }
        });
        rankCol.setPrefWidth(36);
        rankCol.setResizable(false);

        // Team name
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
                        ? "-fx-text-fill: rgba(46,204,113,0.95); -fx-font-weight: bold;" +
                        "-fx-alignment: center-left; -fx-padding: 0 0 0 10;"
                        : "-fx-text-fill: rgba(255,255,255,0.85); -fx-font-weight: bold;" +
                        "-fx-alignment: center-left; -fx-padding: 0 0 0 10;");
            }
        });
        teamCol.setPrefWidth(175);

        // P / W / D / L
        TableColumn<StandingsEntry, Integer> playedCol = makeIntCol("P", "played", 44);
        TableColumn<StandingsEntry, Integer> winsCol   = makeIntCol("W", "wins",   44);
        TableColumn<StandingsEntry, Integer> drawsCol  = makeIntCol("D", "draws",  44);
        TableColumn<StandingsEntry, Integer> lossesCol = makeIntCol("L", "losses", 44);

        // Points
        TableColumn<StandingsEntry, Integer> pointsCol = new TableColumn<>("PTS");
        pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        pointsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(String.valueOf(item));
                setStyle("-fx-text-fill: rgba(241,196,15,0.95);" +
                        "-fx-font-weight: bold; -fx-font-size: 14px;" +
                        "-fx-alignment: center;");
            }
        });
        pointsCol.setPrefWidth(54);

        // Inspect button
        TableColumn<StandingsEntry, Void> actionCol = new TableColumn<>("");
        actionCol.setPrefWidth(44);
        actionCol.setResizable(false);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("⚙");
            {
                applyStyle(false);
                btn.setOnMouseEntered(e -> applyStyle(true));
                btn.setOnMouseExited(e  -> applyStyle(false));
                btn.setOnAction(event -> {
                    StandingsEntry entry = getTableView().getItems().get(getIndex());
                    TeamController tc = new TeamController(
                            stage, sportType, entry.getTeam(), league, leagueManager, userTeam);
                    tc.setOnBack(() -> show());
                    tc.show();
                });
            }
            private void applyStyle(boolean h) {
                btn.setStyle(h
                        ? "-fx-background-color: rgba(255,255,255,0.14);" +
                        "-fx-border-color: rgba(255,255,255,0.35); -fx-border-width:1;" +
                        "-fx-border-radius:6; -fx-background-radius:6;" +
                        "-fx-text-fill:white; -fx-font-size:13; -fx-cursor:hand;"
                        : "-fx-background-color: rgba(255,255,255,0.06);" +
                        "-fx-border-color: rgba(255,255,255,0.14); -fx-border-width:1;" +
                        "-fx-border-radius:6; -fx-background-radius:6;" +
                        "-fx-text-fill:rgba(255,255,255,0.60); -fx-font-size:13; -fx-cursor:hand;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(rankCol, actionCol, teamCol,
                playedCol, winsCol, drawsCol, lossesCol, pointsCol);

        // Row factory — left border highlight + double-click
        table.setRowFactory(tv -> {
            TableRow<StandingsEntry> row = new TableRow<>() {
                @Override
                protected void updateItem(StandingsEntry item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) { setStyle(""); return; }
                    boolean isUser = userTeam != null
                            && item.getTeam().getName().equals(userTeam.getName());
                    if (getIndex() == 0) {
                        setStyle("-fx-border-color: transparent transparent transparent" +
                                " rgba(241,196,15,0.55); -fx-border-width: 0 0 0 3;");
                    } else if (isUser) {
                        setStyle("-fx-border-color: transparent transparent transparent" +
                                " rgba(46,204,113,0.65); -fx-border-width: 0 0 0 3;");
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

    private TableColumn<StandingsEntry, Integer> makeIntCol(
            String title, String prop, double w) {
        TableColumn<StandingsEntry, Integer> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : String.valueOf(item));
                setStyle("-fx-alignment: center; -fx-text-fill: rgba(255,255,255,0.75);");
            }
        });
        col.setPrefWidth(w);
        return col;
    }

    private void simulateWeek(TableView<StandingsEntry> table) {
        if (currentWeek > 14) { checkChampionship(); return; }

        List<Match> weekMatches = league.getFixture().getMatchesByWeek(currentWeek);
        if (weekMatches == null || weekMatches.isEmpty()) {
            currentWeek++;
            checkChampionship();
            return;
        }

        Match userMatch = null;
        for (Match match : weekMatches) {
            if (userTeam != null && match.getHomeTeam() != null && match.getAwayTeam() != null) {
                if (match.getHomeTeam().getName().equals(userTeam.getName()) ||
                        match.getAwayTeam().getName().equals(userTeam.getName())) {
                    userMatch = match;
                    continue;
                }
            }
            if (!match.isPlayed()) {
                IMatchEngine engine = SportFactory.createEngine(sportType);
                GameManager gm = new GameManager(sport, engine, league);
                gm.playMatch(match);
                leagueManager.processMatchResult(league, match, sportType);
            }
        }

        if (userMatch != null && !userMatch.isPlayed()) {
            MatchController mc = new MatchController(
                    stage, sportType, userMatch, league, leagueManager, userTeam);
            mc.setOnFinished(() -> {
                currentWeek++;
                Platform.runLater(() -> {
                    show();
                    checkChampionship();
                });
            });
            mc.show();
        } else {
            currentWeek++;
            updateTable(table);
            checkChampionship();
            updateUIState();
        }
    }

    private void updateUIState() {
        weekLabel.setText(currentWeek > 14 ? "SEASON COMPLETE" : "WEEK  " + currentWeek);
        updateSimulateButton();
    }

    private void checkChampionship() {
        if (currentWeek > 14) {
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
        List<StandingsEntry> sorted = new ArrayList<>(league.getStandings());
        sorted.sort((a, b) -> {
            if (b.getPoints() != a.getPoints())               return b.getPoints() - a.getPoints();
            if (b.getGoalDifference() != a.getGoalDifference())
                return b.getGoalDifference() - a.getGoalDifference();
            return b.getGoalsFor() - a.getGoalsFor();
        });
        table.getItems().addAll(sorted);
    }
}