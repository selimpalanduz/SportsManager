package com.sportsmanager.ui;

import com.sportsmanager.core.LeagueManager;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

public class MatchController {
    private final Stage stage;
    private final String sportType;
    private final Match match;
    private final League league;
    private final LeagueManager leagueManager;
    private final Team userTeam;
    private final ISport sport;
    private IMatchEngine engine;
    private Tactic userTactic = Tactic.BALANCED;

    private Label scoreLabel, periodLabel;
    private TextArea eventLog;
    private Button actionBtn;
    private int periodsPlayed = 0, homeTotal = 0, awayTotal = 0;
    private boolean started = false;
    private Runnable onFinished;

    public MatchController(Stage stage, String sportType, Match match, League league, LeagueManager leagueManager, Team userTeam) {
        this.stage = stage;
        this.sportType = sportType;
        this.match = match;
        this.league = league;
        this.leagueManager = leagueManager;
        this.userTeam = userTeam;
        this.sport = SportFactory.createSport(sportType);
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    public void show() {
        Rectangle topLine = new Rectangle(SCENE_WIDTH - 100, 2);
        topLine.setStyle("-fx-fill: linear-gradient(to right, transparent, #2ecc71, transparent);");

        Label homeLabel = new Label(match.getHomeTeam().getName().toUpperCase());
        homeLabel.getStyleClass().add("title-label");

        Label vsLabel = new Label("VS");
        vsLabel.getStyleClass().add("vs-label");
        vsLabel.setStyle("-fx-font-size: 20px; -fx-padding: 0 20;");

        Label awayLabel = new Label(match.getAwayTeam().getName().toUpperCase());
        awayLabel.getStyleClass().add("title-label");

        HBox matchHeader = new HBox(15, homeLabel, vsLabel, awayLabel);
        matchHeader.setAlignment(Pos.CENTER);

        Label xpInfo = new Label("⚡ " + match.getHomeTeam().getXp() + " XP   vs   " + match.getAwayTeam().getXp() + " XP ⚡");
        xpInfo.getStyleClass().add("vs-label");

        scoreLabel = new Label("0  —  0");
        scoreLabel.getStyleClass().add("score-label");

        periodLabel = new Label("PRE-MATCH");
        periodLabel.getStyleClass().add("period-label");

        VBox scoreBox = new VBox(5, scoreLabel, periodLabel);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.getStyleClass().add("glass-panel");
        scoreBox.setPadding(new Insets(20, 60, 20, 60));

        eventLog = new TextArea();
        eventLog.setEditable(false);
        eventLog.getStyleClass().add("match-log");
        eventLog.setPrefHeight(220);
        eventLog.setMaxWidth(800);

        actionBtn = new Button("⚽  KICK OFF");
        actionBtn.getStyleClass().add("start-button");
        actionBtn.setPrefWidth(240);
        actionBtn.setPrefHeight(54);
        actionBtn.setOnAction(e -> onActionClicked());

        Button skipBtn = new Button("⏩  SKIP MATCH");
        skipBtn.getStyleClass().add("menu-button");
        skipBtn.setPrefWidth(200);
        skipBtn.setPrefHeight(54);
        skipBtn.setOnAction(e -> skipMatch());

        Button forfeitBtn = new Button("✕  FORFEIT");
        forfeitBtn.getStyleClass().add("back-button");
        forfeitBtn.setPrefWidth(160);
        forfeitBtn.setPrefHeight(54);
        forfeitBtn.setOnAction(e -> forfeitMatch());

        HBox buttons = new HBox(20, actionBtn, skipBtn, forfeitBtn);
        buttons.setAlignment(Pos.CENTER);

        Rectangle botLine = new Rectangle(SCENE_WIDTH - 100, 2);
        botLine.setStyle("-fx-fill: linear-gradient(to right, transparent, rgba(241,196,15,0.55), transparent);");

        VBox layout = new VBox(25, topLine, matchHeader, xpInfo, scoreBox, eventLog, buttons, botLine);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.getStyleClass().add("main-background");

        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private void onActionClicked() {
        if (!started) {
            startMatch();
            return;
        }

        if (engine != null && !engine.isMatchOver()) {
            playNextPeriod();
        } else {
            goBackToLeague();
        }
    }

    private void startMatch() {
        engine = SportFactory.createEngine(sportType);
        engine.setupMatch(match);

        started = true;
        actionBtn.setText("NEXT PERIOD");

        eventLog.appendText("Match started! Atmosphere is electric.\n");
    }

    private void playNextPeriod() {
        PeriodResult res = engine.simulateNextPeriod();

        periodsPlayed++;
        homeTotal += res.getHomeScore();
        awayTotal += res.getAwayScore();

        scoreLabel.setText(homeTotal + "  —  " + awayTotal);

        eventLog.appendText(
                "Period " + periodsPlayed + " Result: "
                        + res.getHomeScore() + " - "
                        + res.getAwayScore() + "\n"
        );

        if (engine.isMatchOver()) {
            finishMatch();
        }
    }

    private void skipMatch() {
        if (engine == null) {
            engine = SportFactory.createEngine(sportType);
            engine.setupMatch(match);
        }

        while (!engine.isMatchOver()) {
            PeriodResult res = engine.simulateNextPeriod();

            homeTotal += res.getHomeScore();
            awayTotal += res.getAwayScore();
            periodsPlayed++;
        }

        scoreLabel.setText(homeTotal + "  —  " + awayTotal);

        finishMatch();
    }

    private void forfeitMatch() {
        boolean userIsHome =
                userTeam != null && match.getHomeTeam().equals(userTeam);

        List<PeriodResult> periods = new ArrayList<>();

        int h = userIsHome ? 0 : 3;
        int a = userIsHome ? 3 : 0;

        periods.add(new PeriodResult(h, a));

        match.setResult(
                new MatchResult(
                        match.getHomeTeam(),
                        match.getAwayTeam(),
                        periods
                )
        );

        leagueManager.processMatchResult(league, match, sportType);

        goBackToLeague();
    }

    private void finishMatch() {
        leagueManager.processMatchResult(league, match, sportType);

        periodLabel.setText("FULL TIME");
        periodLabel.setStyle("-fx-text-fill: #f1c40f;");

        actionBtn.setText("RETURN TO LEAGUE");

        actionBtn.getStyleClass().removeAll("start-button");
        actionBtn.getStyleClass().add("finish-button");
    }

    private void goBackToLeague() {
        if (onFinished != null) {
            onFinished.run();
        }
    }
}