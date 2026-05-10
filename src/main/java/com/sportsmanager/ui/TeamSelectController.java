package com.sportsmanager.ui;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import com.sportsmanager.model.common.Team;
import javafx.fxml.FXMLLoader;

import java.util.Objects;
import java.util.List;

import static com.sportsmanager.Main.SCENE_WIDTH;
import static com.sportsmanager.Main.SCENE_HEIGHT;

public class TeamSelectController {

    private final Stage stage;
    private final String sportType;
    private final List<Team> teams;

    public TeamSelectController(Stage stage, String sportType, List<Team> teams) {
        this.stage     = stage;
        this.sportType = sportType;
        this.teams     = teams;
    }

    private String cssUrl() {
        return Objects.requireNonNull(
                getClass().getResource("/style.css")).toExternalForm();
    }

    public void show() {

        // ── Top neon line ────────────────────────────────────────
        Rectangle topLine = new Rectangle(SCENE_WIDTH - 60, 2);
        topLine.setStyle(
                "-fx-fill: linear-gradient(to right," +
                        " transparent, rgba(46,204,113,0.55), transparent);");

        // ── Title block ───────────────────────────────────────────
        Label badge = new Label(sportType.toUpperCase() + "  ·  CAREER MODE");
        badge.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: bold;" +
                        "-fx-text-fill: rgba(46,204,113,0.85);" +
                        "-fx-letter-spacing: 4px;");

        Label title = new Label("SELECT YOUR TEAM");
        title.getStyleClass().add("title-label");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-effect: dropshadow(gaussian,rgba(255,255,255,0.18),14,0.12,0,0);");

        Label subtitle = new Label("Choose the club you want to manage this season");
        subtitle.getStyleClass().add("accent-dim");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.55);");

        VBox titleBlock = new VBox(8, badge, title, subtitle);
        titleBlock.setAlignment(Pos.CENTER);
        
        TextField managerNameField = new TextField();
        managerNameField.setPromptText("Enter your manager name");
        managerNameField.setMaxWidth(420);
        managerNameField.setStyle(
            "-fx-background-color: rgba(255,255,255,0.06);" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(255,255,255,0.4);" +
            "-fx-border-color: rgba(46,204,113,0.4);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 14;");
        // ── Team button list ──────────────────────────────────────
        // No glass-panel wrapper — buttons float directly over the stadium BG.
        // Each individual team-button already has its own dark glass style from CSS.
        VBox teamList = new VBox(12);
        teamList.setAlignment(Pos.CENTER);
        teamList.setMaxWidth(420);
        teamList.setStyle("-fx-background-color: transparent;"); // explicitly transparent

        for (Team team : teams) {
            Button btn = new Button(team.getName().toUpperCase());
            btn.setPrefWidth(380);
            btn.setPrefHeight(54);
            btn.getStyleClass().add("team-button");
            btn.setOnAction(e -> {
                String mn = managerNameField.getText().trim();
                if (mn.isEmpty()) mn = "Manager";
                team.setManagerName(mn);
                LeagueController lc = new LeagueController(stage, sportType, team);
                lc.show();
            });
            teamList.getChildren().add(btn);
        }

        // ── Back button ───────────────────────────────────────────
        Button backBtn = new Button("← BACK");
        backBtn.setPrefWidth(150);
        backBtn.setPrefHeight(46);
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            try {
                Parent root = FXMLLoader.load(
                        Objects.requireNonNull(
                                getClass().getResource("/MainMenu.fxml")));
                Scene menuScene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
                menuScene.getStylesheets().add(cssUrl());
                stage.setTitle("Sports Manager - Ultimate Edition");
                stage.setScene(menuScene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ── Bottom neon line ──────────────────────────────────────
        Rectangle botLine = new Rectangle(SCENE_WIDTH - 60, 2);
        botLine.setStyle(
                "-fx-fill: linear-gradient(to right," +
                        " transparent, rgba(241,196,15,0.45), transparent);");

        // ── Inner content — fully transparent, stadium shows through ─
        VBox content = new VBox(28, topLine, titleBlock,managerNameField, teamList, backBtn, botLine);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(40, 40, 40, 40));
        content.setStyle("-fx-background-color: transparent;");

        // ── ScrollPane ────────────────────────────────────────────
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // ── ROOT — stadium background covers entire window ────────
        StackPane root = new StackPane(scrollPane);
        root.getStyleClass().add("main-background");

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(cssUrl());

        stage.setTitle("Sports Manager - Ultimate Edition  ·  Select Team");
        stage.setScene(scene);
        stage.show();
    }

    
}