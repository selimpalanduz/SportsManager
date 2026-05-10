package com.sportsmanager.ui;

import com.sportsmanager.core.LeagueManager;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.MatchResult;
import com.sportsmanager.model.common.PeriodResult;
import com.sportsmanager.model.common.Player;
import com.sportsmanager.model.common.Tactic;
import com.sportsmanager.model.common.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private Button subBtn;
    private ComboBox<Tactic> tacticBox;

    private int periodsPlayed = 0;
    private int homeTotal = 0;
    private int awayTotal = 0;
    private boolean started = false;

    private Runnable onFinished;

    public MatchController(Stage stage, String sportType, Match match,
                           League league, LeagueManager leagueManager, Team userTeam) {
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
        Label title = new Label(match.getHomeTeam().getName() + "  vs  " + match.getAwayTeam().getName());
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label sportLabel = new Label(sport.getSportName() + "  -  " + sport.getNumberOfPeriods() + " periods");
        sportLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #aaaaaa;");

        scoreLabel = new Label("0  -  0");
        scoreLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");

        periodLabel = new Label("Pre-match");
        periodLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        String userSide = (userTeam != null && match.getAwayTeam().equals(userTeam)) ? "Away" : "Home";
        String userName = (userTeam != null) ? userTeam.getName() : "—";
        Label tacticLabel = new Label("Your Tactic (" + userName + " - " + userSide + ")");
        tacticLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        tacticBox = new ComboBox<>();
        tacticBox.getItems().addAll(Tactic.values());
        tacticBox.setValue(userTactic);
        tacticBox.setPrefWidth(200);
        tacticBox.setOnAction(e -> userTactic = tacticBox.getValue());

        HBox tacticRow = new HBox(10, tacticLabel, tacticBox);
        tacticRow.setAlignment(Pos.CENTER);

        eventLog = new TextArea();
        eventLog.setEditable(false);
        eventLog.setPrefHeight(180);
        eventLog.setStyle("-fx-control-inner-background: #2D2D3E; -fx-text-fill: white; -fx-font-family: 'Consolas';");
        eventLog.appendText("Match ready: " + match.getHomeTeam().getName()
                + " vs " + match.getAwayTeam().getName() + "\n");
        eventLog.appendText("Set your tactic and click Kick Off to begin.\n");

        actionBtn = new Button("Kick Off");
        actionBtn.setPrefWidth(220);
        actionBtn.setPrefHeight(50);
        actionBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        actionBtn.setOnAction(e -> onActionClicked());

        subBtn = new Button("Substitution");
        subBtn.setPrefWidth(140);
        subBtn.setPrefHeight(50);
        subBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand;");
        subBtn.setDisable(true);
        subBtn.setOnAction(e -> openSubstitutionDialog());

        Button backBtn = new Button("Forfeit / Back");
        backBtn.setPrefWidth(140);
        backBtn.setPrefHeight(50);
        backBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> goBackToLeague());

        HBox buttons = new HBox(15, actionBtn, subBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #1E1E2E;");
        layout.getChildren().addAll(title, sportLabel, scoreLabel, periodLabel, tacticRow, eventLog, buttons);

        Scene scene = new Scene(layout, 750, 650);
        stage.setTitle("Match - " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName());
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
        subBtn.setDisable(false);

        eventLog.appendText("\n--- Kick off! Tactic: " + userTactic + " ---\n");
        periodLabel.setText("Period 1 / " + sport.getNumberOfPeriods());
        actionBtn.setText("Play Period 1");
    }

    private void playNextPeriod() {
        userTactic = tacticBox.getValue();
        eventLog.appendText("\n[Period " + (periodsPlayed + 1) + "] Tactic: " + userTactic + "\n");

        PeriodResult result = engine.simulateNextPeriod();
        periodsPlayed++;
        homeTotal += result.getHomeScore();
        awayTotal += result.getAwayScore();

        eventLog.appendText("Period score: " + result.getHomeScore() + " - " + result.getAwayScore() + "\n");
        scoreLabel.setText(homeTotal + "  -  " + awayTotal);

        List<Player> injured = engine.checkInjuries();
        if (injured != null && !injured.isEmpty()) {
            for (Player p : injured) {
                eventLog.appendText("INJURY: " + p.getName()
                        + " (out " + p.getInjuryDuration() + " games)\n");
            }
        }

        if (engine.isMatchOver()) {
            finishMatch();
        } else {
            int next = periodsPlayed + 1;
            periodLabel.setText("Period " + next + " / " + sport.getNumberOfPeriods());
            actionBtn.setText("Play Period " + next);
            eventLog.appendText("You can change your tactic or make substitutions before the next period.\n");
        }
    }

    private void openSubstitutionDialog() {
        if (userTeam == null) return;

        int squadSize = sport.getSquadSize();
        List<Player> roster = userTeam.getRoster();
        if (roster == null || roster.size() <= squadSize) {
            new Alert(Alert.AlertType.INFORMATION,
                    "No bench players available.").showAndWait();
            return;
        }

        List<Player> starters = new ArrayList<>(roster.subList(0, squadSize));
        List<Player> bench    = new ArrayList<>(roster.subList(squadSize, roster.size()));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Substitution");
        dialog.setHeaderText("Pick one starter and one bench player to swap.\n"
                + "Injured players are listed but should not be brought on.");

        ListView<Player> startersView = new ListView<>();
        startersView.setPrefHeight(220);
        startersView.getItems().addAll(starters);
        startersView.setCellFactory(playerCellFactory());

        ListView<Player> benchView = new ListView<>();
        benchView.setPrefHeight(220);
        benchView.getItems().addAll(bench);
        benchView.setCellFactory(playerCellFactory());

        VBox left  = new VBox(6, new Label("Starters"), startersView);
        VBox right = new VBox(6, new Label("Bench"),    benchView);
        HBox content = new HBox(15, left, right);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType swap   = new ButtonType("Swap",   ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(swap, cancel);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != swap) return;
            Player out = startersView.getSelectionModel().getSelectedItem();
            Player in  = benchView.getSelectionModel().getSelectedItem();
            if (out == null || in == null) {
                new Alert(Alert.AlertType.WARNING,
                        "Please select one player from each list.").showAndWait();
                return;
            }
            if (in.isInjured()) {
                new Alert(Alert.AlertType.WARNING,
                        in.getName() + " is injured and cannot come on.").showAndWait();
                return;
            }
            int outIdx = roster.indexOf(out);
            int inIdx  = roster.indexOf(in);
            if (outIdx >= 0 && inIdx >= 0) {
                Collections.swap(roster, outIdx, inIdx);
                eventLog.appendText("SUBSTITUTION: " + out.getName()
                        + " OFF, " + in.getName() + " ON\n");
            }
        });
    }

    private Callback<ListView<Player>, javafx.scene.control.ListCell<Player>> playerCellFactory() {
        return lv -> new javafx.scene.control.ListCell<Player>() {
            @Override
            protected void updateItem(Player p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                } else {
                    String suffix = p.isInjured() ? "  [INJ " + p.getInjuryDuration() + "]" : "";
                    setText(p.getName() + "  (" + p.getPosition()
                            + ", skill " + p.getOverallSkill() + ")" + suffix);
                }
            }
        };
    }

    private void finishMatch() {
        if (league != null && leagueManager != null) {
            leagueManager.processMatchResult(league, match, sportType);
        }

        MatchResult result = match.getResult();
        eventLog.appendText("\n--- FULL TIME ---\n");
        eventLog.appendText("Final: " + match.getHomeTeam().getName() + " " + homeTotal
                + " - " + awayTotal + " " + match.getAwayTeam().getName() + "\n");

        if (result != null && result.getWinner() != null) {
            eventLog.appendText("Winner: " + result.getWinner().getName() + "\n");
            periodLabel.setText("Winner: " + result.getWinner().getName());
        } else {
            eventLog.appendText("It's a draw.\n");
            periodLabel.setText("Draw");
        }

        tacticBox.setDisable(true);
        subBtn.setDisable(true);
        actionBtn.setText("Back to League");
        actionBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
    }

    private void goBackToLeague() {
        if (onFinished != null) {
            onFinished.run();
            return;
        }
        new LeagueController(stage, sportType, userTeam).show();
    }
}