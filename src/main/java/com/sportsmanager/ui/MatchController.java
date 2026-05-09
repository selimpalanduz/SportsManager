package com.sportsmanager.ui;

import com.sportsmanager.core.LeagueManager;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

import static com.sportsmanager.Main.SCENE_WIDTH;
import static com.sportsmanager.Main.SCENE_HEIGHT;

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

    private Label scoreLabel;
    private Label periodLabel;
    private TextArea eventLog;
    private Button actionBtn;
    private ComboBox<Tactic> tacticBox;

    private int periodsPlayed = 0;
    private int homeTotal     = 0;
    private int awayTotal     = 0;
    private boolean started   = false;

    private Runnable onFinished;

    public MatchController(Stage stage, String sportType, Match match,
                           League league, LeagueManager leagueManager, Team userTeam) {
        this.stage         = stage;
        this.sportType     = sportType;
        this.match         = match;
        this.league        = league;
        this.leagueManager = leagueManager;
        this.userTeam      = userTeam;
        this.sport         = SportFactory.createSport(sportType);
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    private String cssUrl() {
        return Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
    }

    public void show() {
        // ── Üst neon çizgi ──────────────────────────────────────
        Rectangle topLine = new Rectangle(SCENE_WIDTH - 60, 2);
        topLine.setStyle("-fx-fill: linear-gradient(to right, transparent, #2ecc71, transparent);");

        // ── Takım isimleri başlığı ───────────────────────────────
        String homeN = match.getHomeTeam().getName();
        String awayN = match.getAwayTeam().getName();

        Label homeLabel = new Label(homeN);
        homeLabel.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:white;");

        Label vsLabel = new Label("VS");
        vsLabel.setStyle("-fx-font-size:16px; -fx-font-weight:bold;" +
                "-fx-text-fill:#e74c3c;" +
                "-fx-effect: dropshadow(gaussian,#e74c3c,10,0.65,0,0);");

        Label awayLabel = new Label(awayN);
        awayLabel.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:white;");

        HBox matchHeader = new HBox(24, homeLabel, vsLabel, awayLabel);
        matchHeader.setAlignment(Pos.CENTER);

        // ── Spor / periyot bilgisi ───────────────────────────────
        Label sportLabel = new Label(
                sport.getSportName() + "   ·   " + sport.getNumberOfPeriods() + " Periods");
        sportLabel.getStyleClass().add("sport-info-label");

        // ── Skor ─────────────────────────────────────────────────
        scoreLabel = new Label("0  —  0");
        scoreLabel.getStyleClass().add("score-label");

        // ── Periyot durumu ───────────────────────────────────────
        periodLabel = new Label("PRE-MATCH");
        periodLabel.getStyleClass().add("period-label");

        // ── Skor + periyot kutusu ────────────────────────────────
        VBox scoreBox = new VBox(6, scoreLabel, periodLabel);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setStyle(
                "-fx-background-color: rgba(12,15,22,0.88);" +
                        "-fx-border-color: rgba(46,204,113,0.22);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 16;" +
                        "-fx-background-radius: 16;" +
                        "-fx-padding: 20 50;" +
                        "-fx-effect: dropshadow(gaussian,rgba(46,204,113,0.15),16,0,0,0);");

        // ── Taktik seçici ────────────────────────────────────────
        String userSide = (userTeam != null && match.getAwayTeam().equals(userTeam))
                ? "AWAY" : "HOME";
        String userName = (userTeam != null) ? userTeam.getName().toUpperCase() : "YOU";

        Label tacticHeader = new Label("TACTIC  ·  " + userName + "  (" + userSide + ")");
        tacticHeader.getStyleClass().add("tactic-label");

        tacticBox = new ComboBox<>();
        tacticBox.getItems().addAll(Tactic.values());
        tacticBox.setValue(userTactic);
        tacticBox.setPrefWidth(210);
        tacticBox.getStyleClass().add("custom-combo");
        tacticBox.setOnAction(e -> userTactic = tacticBox.getValue());

        HBox tacticRow = new HBox(14, tacticHeader, tacticBox);
        tacticRow.setAlignment(Pos.CENTER);

        // ── Canlı maç logu ───────────────────────────────────────
        Label logHeader = new Label("MATCH COMMENTARY");
        logHeader.setStyle(
                "-fx-font-size:10px; -fx-font-weight:bold;" +
                        "-fx-text-fill: rgba(46,204,113,0.85);" +
                        "-fx-padding: 0 0 4 2;");

        eventLog = new TextArea();
        eventLog.setEditable(false);
        eventLog.setPrefHeight(200);
        eventLog.getStyleClass().add("match-log");
        eventLog.appendText("⚽  " + homeN + "  vs  " + awayN + "\n");
        eventLog.appendText("Set your tactic and press KICK OFF to begin.\n");

        VBox logBox = new VBox(4, logHeader, eventLog);
        logBox.setStyle(
                "-fx-background-color: rgba(8,10,18,0.88);" +
                        "-fx-border-color: rgba(46,204,113,0.15);" +
                        "-fx-border-width:1; -fx-border-radius:12; -fx-background-radius:12;" +
                        "-fx-padding:16;");

        // ── Aksiyon butonu ───────────────────────────────────────
        actionBtn = new Button("⚽  KICK OFF");
        actionBtn.setPrefWidth(240);
        actionBtn.setPrefHeight(52);
        actionBtn.getStyleClass().add("start-button");
        actionBtn.setOnAction(e -> onActionClicked());

        // ── Forfeit butonu ───────────────────────────────────────
        Button backBtn = new Button("✕  FORFEIT");
        backBtn.setPrefWidth(150);
        backBtn.setPrefHeight(52);
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> goBackToLeague());

        HBox buttons = new HBox(16, actionBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        // ── Alt neon çizgi ───────────────────────────────────────
        Rectangle botLine = new Rectangle(SCENE_WIDTH - 60, 2);
        botLine.setStyle("-fx-fill: linear-gradient(to right, transparent," +
                "rgba(241,196,15,0.55), transparent);");

        // ── Ana layout ───────────────────────────────────────────
        VBox layout = new VBox(18);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(30, 40, 30, 40));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #0a0a0f, #0d1117, #0a0f1e);");
        layout.getChildren().addAll(
                topLine, matchHeader, sportLabel,
                scoreBox, tacticRow, logBox,
                buttons, botLine);

        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(cssUrl());
        stage.setTitle("Match Day — " + homeN + " vs " + awayN);
        stage.setScene(scene);
        stage.show();
    }

    // ── Aksiyon butonu tıklaması ─────────────────────────────────
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

        appendLog("── KICK OFF  ·  Tactic: " + userTactic + " ──");
        periodLabel.setText("PERIOD  1 / " + sport.getNumberOfPeriods());
        actionBtn.setText("▶  PLAY PERIOD 1");
    }

    private void playNextPeriod() {
        userTactic = tacticBox.getValue();
        appendLog("\n[ Period " + (periodsPlayed + 1) + " ]  Tactic: " + userTactic);

        PeriodResult result = engine.simulateNextPeriod();
        periodsPlayed++;
        homeTotal += result.getHomeScore();
        awayTotal += result.getAwayScore();

        appendLog("Period: " + result.getHomeScore() + " – " + result.getAwayScore());
        scoreLabel.setText(homeTotal + "  —  " + awayTotal);

        List<Player> injured = engine.checkInjuries();
        if (injured != null) {
            for (Player p : injured) {
                appendLog("🚑 INJURY: " + p.getName() +
                        " (out " + p.getInjuryDuration() + " games)");
            }
        }

        if (engine.isMatchOver()) {
            finishMatch();
        } else {
            int next = periodsPlayed + 1;
            periodLabel.setText("PERIOD  " + next + " / " + sport.getNumberOfPeriods());
            actionBtn.setText("▶  PLAY PERIOD " + next);
            appendLog("You can change your tactic before the next period.");
        }
    }

    private void finishMatch() {
        if (league != null && leagueManager != null) {
            leagueManager.processMatchResult(league, match, sportType);
        }

        MatchResult result = match.getResult();
        appendLog("\n══════════  FULL TIME  ══════════");
        appendLog(match.getHomeTeam().getName() + "  " + homeTotal +
                "  –  " + awayTotal + "  " + match.getAwayTeam().getName());

        if (result != null && result.getWinner() != null) {
            appendLog("🏆  Winner: " + result.getWinner().getName());
            periodLabel.setText("WINNER: " + result.getWinner().getName().toUpperCase());
        } else {
            appendLog("🤝  Draw");
            periodLabel.setText("DRAW");
        }

        tacticBox.setDisable(true);
        actionBtn.setText("✓  FINISH MATCH");
        actionBtn.getStyleClass().setAll("finish-button");
    }

    private void appendLog(String text) {
        eventLog.appendText(text + "\n");
    }

    private void goBackToLeague() {
        if (onFinished != null) {
            onFinished.run();
            return;
        }
        if (league == null) {
            try {
                Parent root = FXMLLoader.load(
                        Objects.requireNonNull(getClass().getResource("/MainMenu.fxml")));
                Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
                scene.getStylesheets().add(cssUrl());
                stage.setTitle("Sports Manager - Ultimate Edition");
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new LeagueController(stage, sportType, userTeam).show();
        }
    }
}
