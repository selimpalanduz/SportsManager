package com.sportsmanager.ui;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import com.sportsmanager.model.common.Coach;
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
                "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.90),8,0.6,0,2);");

        Label subtitle = new Label("Choose the club you want to manage this season");
        subtitle.getStyleClass().add("accent-dim");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.55);" +
                "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.85),5,0.4,0,1);");

        VBox titleBlock = new VBox(8, badge, title, subtitle);
        titleBlock.setAlignment(Pos.CENTER);

        // ── Manager name field ────────────────────────────────────
        Label nameFieldLabel = new Label("MANAGER NAME");
        nameFieldLabel.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: bold;" +
                        "-fx-text-fill: rgba(255,255,255,0.40); -fx-letter-spacing: 3px;");

        TextField managerNameField = new TextField();
        managerNameField.setPromptText("Enter your manager name...");
        managerNameField.setMaxWidth(420);
        managerNameField.setStyle(
                "-fx-background-color: rgba(12,14,26,0.88);" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: rgba(255,255,255,0.30);" +
                        "-fx-border-color: rgba(46,204,113,0.40);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 12 16;" +
                        "-fx-font-size: 14px;" +
                        "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.65),8,0,0,2);");

        VBox nameBlock = new VBox(6, nameFieldLabel, managerNameField);
        nameBlock.setAlignment(Pos.CENTER);
        nameBlock.setMaxWidth(420);

        // ── Team button list ──────────────────────────────────────
        VBox teamList = new VBox(10);
        teamList.setAlignment(Pos.CENTER);
        teamList.setMaxWidth(420);
        teamList.setStyle("-fx-background-color: transparent;");

        for (Team team : teams) {
            Button btn = new Button(team.getName().toUpperCase());
            btn.setPrefWidth(380);
            btn.setPrefHeight(54);
            btn.getStyleClass().add("team-button");

            btn.setOnAction(e -> {
                // ════════════════════════════════════════════════
                // GÖREV 1 FIX — Menajer adını hem setManagerName
                // hem de takımın COACH nesnesine set et.
                //
                // Önceki kod:
                //   team.setManagerName(mn);
                //
                // Yeni kod:
                //   team.setManagerName(mn);          ← TeamController label için
                //   team.getCoaches().get(0).setName(mn); ← Hoca ismi sisteme yansısın
                //
                // Böylece TeamController'daki managerLabel VE
                // maç motorunun coach referansı aynı ismi gösterir.
                // ════════════════════════════════════════════════
                String mn = managerNameField.getText().trim();
                if (mn.isEmpty()) mn = "Manager";

                // 1) TeamController'daki "Manager: ..." satırı için
                team.setManagerName(mn);

                // 2) Takımın Coach nesnesi(leri)ne ismi yaz
                //    (LeagueController her takıma 1 coach ekler)
                List<Coach> coaches = team.getCoaches();
                if (coaches != null && !coaches.isEmpty()) {
                    coaches.get(0).setName(mn);
                }

                // 3) Ligi başlat
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

        // ── Inner content ─────────────────────────────────────────
        VBox content = new VBox(24,
                topLine, titleBlock, nameBlock, teamList, backBtn, botLine);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(40, 40, 40, 40));
        content.setStyle("-fx-background-color: transparent;");

        // ── ScrollPane ────────────────────────────────────────────
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle(
                "-fx-background-color: transparent; -fx-border-color: transparent;");

        // ── ROOT — stadium background ─────────────────────────────
        StackPane root = new StackPane(scrollPane);
        root.getStyleClass().add("main-background");

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(cssUrl());

        stage.setTitle("Sports Manager - Ultimate Edition  ·  Select Team");
        stage.setScene(scene);
        stage.show();
    }
}