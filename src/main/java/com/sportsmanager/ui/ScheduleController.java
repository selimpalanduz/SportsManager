package com.sportsmanager.ui;

import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

public class ScheduleController {

    private final Stage stage;
    private final League league;
    private final Team userTeam;
    private final int currentWeek;
    private Runnable onBack;

    public ScheduleController(Stage stage, League league, Team userTeam, int currentWeek) {
        this.stage = stage;
        this.league = league;
        this.userTeam = userTeam;
        this.currentWeek = currentWeek;
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    private String cssUrl() {
        return Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
    }

    public void show() {
        Rectangle topLine = new Rectangle(SCENE_WIDTH - 60, 2);
        topLine.setStyle("-fx-fill: linear-gradient(to right, transparent, rgba(46,204,113,0.55), transparent);");

        Label badge = new Label("LEAGUE  ·  SCHEDULE");
        badge.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;"
                + "-fx-text-fill: rgba(46,204,113,0.85); -fx-letter-spacing: 4px;");

        Label title = new Label("FIXTURE  &  RESULTS");
        title.getStyleClass().add("title-label");

        VBox header = new VBox(4, badge, title);
        header.setAlignment(Pos.CENTER);

        VBox weeksBox = new VBox(18);
        weeksBox.setAlignment(Pos.TOP_CENTER);
        weeksBox.setMaxWidth(820);

        Map<Integer, List<Match>> weekly = new TreeMap<>(league.getFixture().getAllMatches());
        for (Map.Entry<Integer, List<Match>> e : weekly.entrySet()) {
            weeksBox.getChildren().add(buildWeekBlock(e.getKey(), e.getValue()));
        }

        ScrollPane scroll = new ScrollPane(weeksBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scroll.setPrefViewportHeight(440);

        Button backBtn = new Button("← BACK");
        backBtn.setPrefWidth(150);
        backBtn.setPrefHeight(46);
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(ev -> {
            if (onBack != null) onBack.run();
        });

        Rectangle botLine = new Rectangle(SCENE_WIDTH - 60, 2);
        botLine.setStyle("-fx-fill: linear-gradient(to right, transparent, rgba(241,196,15,0.45), transparent);");

        VBox content = new VBox(20, topLine, header, scroll, backBtn, botLine);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setStyle("-fx-background-color: transparent;");

        StackPane root = new StackPane(content);
        root.getStyleClass().add("main-background");

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(cssUrl());
        stage.setTitle("Sports Manager - Ultimate Edition  ·  Schedule");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildWeekBlock(int week, List<Match> matches) {
        boolean isCurrent = (week == currentWeek);
        String accent = isCurrent ? "rgba(46,204,113,0.85)" : "rgba(255,255,255,0.40)";

        Label weekLbl = new Label("WEEK  " + week + (isCurrent ? "   ◀  THIS WEEK" : ""));
        weekLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;"
                + "-fx-text-fill: " + accent + "; -fx-letter-spacing: 3px;");

        VBox box = new VBox(6, weekLbl);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.04);"
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 1;"
                + "-fx-border-radius: 12; -fx-background-radius: 12;"
                + "-fx-padding: 14 18 14 18;");
        box.setMaxWidth(820);

        for (Match m : matches) {
            box.getChildren().add(matchRow(m));
        }
        return box;
    }

    private HBox matchRow(Match m) {
        String homeName = m.getHomeTeam().getName();
        String awayName = m.getAwayTeam().getName();
        boolean userInvolved = userTeam != null
                && (homeName.equals(userTeam.getName()) || awayName.equals(userTeam.getName()));

        String scoreText;
        if (m.isPlayed() && m.getResult() != null) {
            scoreText = m.getResult().getTotalHomeScore() + " : " + m.getResult().getTotalAwayScore();
        } else {
            scoreText = " vs ";
        }

        Label home = new Label(homeName.toUpperCase());
        home.setPrefWidth(280);
        home.setStyle(rowStyle(userInvolved && homeName.equals(userTeam.getName()), true));

        Label score = new Label(scoreText);
        score.setPrefWidth(90);
        score.setAlignment(Pos.CENTER);
        score.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;"
                + "-fx-text-fill: rgba(241,196,15,0.95); -fx-alignment: center;");

        Label away = new Label(awayName.toUpperCase());
        away.setPrefWidth(280);
        away.setStyle(rowStyle(userInvolved && awayName.equals(userTeam.getName()), false));

        HBox row = new HBox(8, home, score, away);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(4, 0, 4, 0));
        return row;
    }

    private String rowStyle(boolean isUserSide, boolean alignRight) {
        String color = isUserSide ? "rgba(46,204,113,0.95)" : "rgba(255,255,255,0.78)";
        String align = alignRight ? "center-right" : "center-left";
        return "-fx-text-fill: " + color + "; -fx-font-weight: bold;"
                + "-fx-font-size: 13px; -fx-alignment: " + align + ";";
    }
}