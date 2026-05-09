package com.sportsmanager.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Objects;

import static com.sportsmanager.Main.SCENE_WIDTH;
import static com.sportsmanager.Main.SCENE_HEIGHT;

public class InstructionsController {

    private final Stage stage;
    private final Runnable onBack;

    public InstructionsController(Stage stage, Runnable onBack) {
        this.stage = stage;
        this.onBack = onBack;
    }

    private String cssUrl() {
        return Objects.requireNonNull(
                getClass().getResource("/style.css")).toExternalForm();
    }

    public void show() {
        Rectangle topLine = new Rectangle(SCENE_WIDTH - 60, 2);
        topLine.setStyle(
                "-fx-fill: linear-gradient(to right," +
                        " transparent, rgba(46,204,113,0.50), transparent);");

        Label badge = new Label("HOW TO PLAY");
        badge.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: bold;" +
                        "-fx-text-fill: rgba(46,204,113,0.75);" +
                        "-fx-letter-spacing: 4px;");

        Label title = new Label("INSTRUCTIONS");
        title.getStyleClass().add("title-label");

        VBox header = new VBox(4, badge, title);
        header.setAlignment(Pos.CENTER);

        TextArea instructionsText = new TextArea();
        instructionsText.setEditable(false);
        instructionsText.setWrapText(true);
        instructionsText.setPrefHeight(420);
        instructionsText.setStyle(
                "-fx-control-inner-background: rgba(8,10,18,0.88);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Segoe UI', sans-serif;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 16;");

        instructionsText.setText(
                "Welcome to Sports Manager!\n\n" +
                "1. Select your sport (Football or Volleyball) from the main menu.\n\n" +
                "2. Choose a team to manage for the season.\n\n" +
                "3. On the league screen, you can see the standings and simulate weeks.\n\n" +
                "4. When your team plays, you can:\n" +
                "   - Watch the match period by period (KICK OFF)\n" +
                "   - Skip directly to the result (SKIP MATCH)\n" +
                "   - Forfeit the match (FORFEIT - results in a 0-3 loss)\n\n" +
                "5. Change your tactic between periods to influence the match outcome.\n\n" +
                "6. After all weeks are completed, the team with the most points wins the league.\n\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
        );

        Button backBtn = new Button("← BACK");
        backBtn.setPrefWidth(150);
        backBtn.setPrefHeight(46);
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        Rectangle botLine = new Rectangle(SCENE_WIDTH - 60, 2);
        botLine.setStyle(
                "-fx-fill: linear-gradient(to right," +
                        " transparent, rgba(241,196,15,0.38), transparent);");

        VBox content = new VBox(20, topLine, header, instructionsText, backBtn, botLine);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(40, 60, 40, 60));
        content.setStyle("-fx-background-color: transparent;");

        StackPane root = new StackPane(content);
        root.getStyleClass().add("main-background");

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(cssUrl());
        stage.setTitle("Sports Manager - Instructions");
        stage.setScene(scene);
        stage.show();
    }
}