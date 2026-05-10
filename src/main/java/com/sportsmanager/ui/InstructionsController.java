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

        // TextArea'yı tamamen şeffaf yapıyoruz ki arkasına koyacağımız siyah cam gözüksün
        instructionsText.setStyle(
                "-fx-control-inner-background: transparent;" +
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Segoe UI', sans-serif;" +
                        "-fx-font-size: 15px;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;" +
                        "-fx-border-color: transparent;");

        instructionsText.setText(
                "WELCOME TO SPORTS MANAGER - ULTIMATE EDITION\n\n" +
                        "Step into the dugout and lead your team to glory. Here is your managerial guide for the season:\n\n" +
                        "1. START YOUR CAREER: Enter your Manager Name and choose your discipline (Football or Volleyball).\n\n" +
                        "2. CHOOSE YOUR CLUB: Select the team you want to manage. Your name will be registered as the Head Coach.\n\n" +
                        "3. SQUAD MANAGEMENT: Before every match, analyze your 18-player roster. Pick your Starting XI and organize your 7 substitutes strategically based on their skills and positions.\n\n" +
                        "4. MATCHDAY EXPERIENCE: When it is time to play, you have full control:\n" +
                        "   • KICK OFF: Watch the match unfold period by period.\n" +
                        "   • TACTICS: Change your playstyle mid-game to outsmart the opponent.\n" +
                        "   • QUICK SIM: Skip directly to the final whistle.\n" +
                        "   • FORFEIT: Concede the match (automatically results in a 0-3 loss).\n\n" +
                        "5. LEAGUE DASHBOARD: Monitor the standings, track your points and goal differences, and simulate rival matches week by week.\n\n" +
                        "6. THE ULTIMATE GOAL: Maintain your form, gather the most points over the 14-week season, and lift the Championship Trophy.\n\n" +
                        "Good luck, Manager. The stadium awaits."
        );

        // İŞTE SİHİRLİ DOKUNUŞ: Yazı alanını siyah, tok bir cam panel içine alıyoruz
        StackPane textContainer = new StackPane(instructionsText);
        textContainer.getStyleClass().add("glass-panel");
        textContainer.setMaxWidth(1000); // Yanlardan biraz pay bırakıp daha şık durmasını sağlar

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

        // "instructionsText" yerine "textContainer" (siyah kutuyu) ekliyoruz
        VBox content = new VBox(20, topLine, header, textContainer, backBtn, botLine);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 60, 30, 60));
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