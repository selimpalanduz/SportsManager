package com.sportsmanager.ui;
import com.sportsmanager.model.common.Match;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.sportsmanager.core.GameManager;
import com.sportsmanager.core.LeagueManager;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.StandingsEntry;
import com.sportsmanager.model.common.Team;
import com.sportsmanager.model.sports.football.FootballCoach;
import com.sportsmanager.model.sports.football.FootballPlayer;
import com.sportsmanager.model.sports.football.FootballTeam;
import com.sportsmanager.model.sports.volleyball.VolleyballCoach;
import com.sportsmanager.model.sports.volleyball.VolleyballPlayer;
import com.sportsmanager.model.sports.volleyball.VolleyballTeam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LeagueController {

    private Stage stage;
    private String sportType;
    private ISport sport;
    private IMatchEngine engine;
    private League league;
    private GameManager gameManager;
    private LeagueManager leagueManager;

    private static final String[] TEAM_NAMES = {
        "Galatasaray", "Fenerbahce", "Besiktas", "Trabzonspor",
        "Basaksehir", "Sivasspor", "Konyaspor", "Antalyaspor"
    };

    private static final String[] PLAYER_NAMES = {
        "Selim", "Deniz", "Ali", "Veli", "Hasan", "Huseyin",
        "Mustafa", "Ibrahim", "Yusuf", "Emre", "Burak", "Arda",
        "Kerem", "Hakan", "Merih", "Ozan", "Caglar", "Orkun"
    };

    public LeagueController(Stage stage, String sportType) {
        this.stage = stage;
        this.sportType = sportType;
        this.sport = SportFactory.createSport(sportType);
        this.engine = SportFactory.createEngine(sportType);
        this.leagueManager = new LeagueManager();
        setupLeague();
    }

    private void setupLeague() {
        List<Team> teams = new ArrayList<>();
        Random random = new Random();

        for (String name : TEAM_NAMES) {
            Team team;
            if (sportType.equalsIgnoreCase("football")) {
                FootballTeam ft = new FootballTeam(name);
                for (int i = 0; i < 11; i++) {
                    String playerName = PLAYER_NAMES[random.nextInt(PLAYER_NAMES.length)];
                    ft.addPlayer(new FootballPlayer(playerName, 60 + random.nextInt(30)));
                }
                ft.addCoach(new FootballCoach("Coach " + name, random.nextInt(10) + 1));
                team = ft;
            } else {
                VolleyballTeam vt = new VolleyballTeam(name);
                for (int i = 0; i < 6; i++) {
                    String playerName = PLAYER_NAMES[random.nextInt(PLAYER_NAMES.length)];
                    vt.addPlayer(new VolleyballPlayer(playerName, 60 + random.nextInt(30)));
                }
                vt.addCoach(new VolleyballCoach("Coach " + name, random.nextInt(10) + 1));
                team = vt;
            }
            teams.add(team);
        }

        league = leagueManager.createLeague(sport.getSportName() + " League", teams);
        gameManager = new GameManager(sport, engine, league);
    }

    public void show() {
        Label title = new Label(sport.getSportName() + " League");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TableView<StandingsEntry> table = new TableView<>();

        TableColumn<StandingsEntry, String> teamCol = new TableColumn<>("Team");
        teamCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getTeam().getName()));
        teamCol.setPrefWidth(150);

        TableColumn<StandingsEntry, Integer> playedCol = new TableColumn<>("P");
        playedCol.setCellValueFactory(new PropertyValueFactory<>("played"));
        playedCol.setPrefWidth(40);

        TableColumn<StandingsEntry, Integer> winsCol = new TableColumn<>("W");
        winsCol.setCellValueFactory(new PropertyValueFactory<>("wins"));
        winsCol.setPrefWidth(40);

        TableColumn<StandingsEntry, Integer> drawsCol = new TableColumn<>("D");
        drawsCol.setCellValueFactory(new PropertyValueFactory<>("draws"));
        drawsCol.setPrefWidth(40);

        TableColumn<StandingsEntry, Integer> lossesCol = new TableColumn<>("L");
        lossesCol.setCellValueFactory(new PropertyValueFactory<>("losses"));
        lossesCol.setPrefWidth(40);

        TableColumn<StandingsEntry, Integer> pointsCol = new TableColumn<>("Pts");
        pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        pointsCol.setPrefWidth(50);

        table.getColumns().addAll(teamCol, playedCol, winsCol, drawsCol, lossesCol, pointsCol);
        updateTable(table);

        Button simulateWeekBtn = new Button("Simulate Next Week");
        simulateWeekBtn.setPrefWidth(200);
        simulateWeekBtn.setStyle("-fx-font-size: 14px;");
        simulateWeekBtn.setOnAction(e -> {
            simulateWeek();
            updateTable(table);
        });

        Button backBtn = new Button("Back to Menu");
        backBtn.setOnAction(e -> {
            MainMenuController menu = new MainMenuController(stage);
            menu.show();
        });

        HBox buttons = new HBox(10, simulateWeekBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(title, table, buttons);

        Scene scene = new Scene(layout, 700, 500);
        stage.setScene(scene);
        stage.show();
    }

    private int currentWeek = 1;

    private void simulateWeek() {
        List<Match> weekMatches = league.getFixture().getMatchesByWeek(currentWeek);
        
        if (weekMatches.isEmpty()) {
            System.out.println("Season is over!");
            return;
        }

        for (Match match : weekMatches) {
            if (!match.isPlayed()) {
                GameManager gm = new GameManager(sport, SportFactory.createEngine(sportType), league);
                gm.playMatch(match);
                leagueManager.processMatchResult(league, match);
            }
        }
        currentWeek++;
    }

    private void updateTable(TableView<StandingsEntry> table) {
    table.getItems().clear();
    table.getItems().addAll(league.getStandings());
    }
}