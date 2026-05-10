package com.sportsmanager.ui;

import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.Player;
import com.sportsmanager.model.common.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

public class LineupController {

    private final Stage stage;
    private final ISport sport;
    private final Team userTeam;
    private final Match match;

    private Runnable onConfirm;
    private Runnable onCancel;

    private final List<Player> picked = new ArrayList<>();
    private Label countLabel;

    public LineupController(Stage stage, ISport sport, Team userTeam, Match match) {
        this.stage = stage;
        this.sport = sport;
        this.userTeam = userTeam;
        this.match = match;
    }

    public void setOnConfirm(Runnable r) {
        this.onConfirm = r;
    }

    public void setOnCancel(Runnable r) {
        this.onCancel = r;
    }

    public void show() {
        int n = sport.getSquadSize();

        Rectangle topLine = new Rectangle(SCENE_WIDTH - 100, 2);
        topLine.setStyle("-fx-fill: linear-gradient(to right, transparent, #2ecc71, transparent);");

        Label title = new Label("PICK YOUR STARTING " + n);
        title.getStyleClass().add("title-label");

        String opponent = userTeam.equals(match.getHomeTeam())
                ? match.getAwayTeam().getName()
                : match.getHomeTeam().getName();

        Label vsLabel = new Label("VERSUS " + opponent.toUpperCase());
        vsLabel.getStyleClass().add("vs-label");

        VBox titleBox = new VBox(5, title, vsLabel);
        titleBox.setAlignment(Pos.CENTER);

        List<Player> available = new ArrayList<>();

        for (Player p : userTeam.getRoster()) {
            if (!p.isInjured()) {
                available.add(p);
            }
        }

        ListView<Player> rosterView = new ListView<>();
        rosterView.getItems().addAll(available);
        rosterView.setCellFactory(lv -> playerCell());
        rosterView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        rosterView.getStyleClass().add("standings-table");
        rosterView.setPrefHeight(380);
        rosterView.setMaxWidth(600);

        List<Player> sortedBySkill = new ArrayList<>(available);
        sortedBySkill.sort((a, b) -> Integer.compare(b.getOverallSkill(), a.getOverallSkill()));

        int defaultPicks = Math.min(n, sortedBySkill.size());

        for (int i = 0; i < defaultPicks; i++) {
            rosterView.getSelectionModel().select(sortedBySkill.get(i));
        }

        picked.clear();
        picked.addAll(rosterView.getSelectionModel().getSelectedItems());

        countLabel = new Label(picked.size() + " / " + n + " PLAYERS SELECTED");
        countLabel.getStyleClass().add("period-label");

        updateCountLabel(n);

        rosterView.getSelectionModel().getSelectedItems().addListener(
                (javafx.collections.ListChangeListener<Player>) c -> {
                    picked.clear();
                    picked.addAll(rosterView.getSelectionModel().getSelectedItems());
                    updateCountLabel(n);
                });

        Button autoBtn = new Button("⚡ AUTO-PICK");
        autoBtn.getStyleClass().add("menu-button");
        autoBtn.setPrefWidth(160);

        autoBtn.setOnAction(e -> {
            rosterView.getSelectionModel().clearSelection();

            for (int i = 0; i < defaultPicks; i++) {
                rosterView.getSelectionModel().select(sortedBySkill.get(i));
            }
        });

        Button confirmBtn = new Button("⚽ CONFIRM & START");
        confirmBtn.getStyleClass().add("start-button");
        confirmBtn.setPrefWidth(220);
        confirmBtn.setOnAction(e -> confirm(n));

        Button cancelBtn = new Button("✕ CANCEL");
        cancelBtn.getStyleClass().add("back-button");
        cancelBtn.setPrefWidth(140);

        cancelBtn.setOnAction(e -> {
            if (onCancel != null) {
                onCancel.run();
            }
        });

        HBox buttons = new HBox(20, autoBtn, confirmBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        Rectangle botLine = new Rectangle(SCENE_WIDTH - 100, 2);
        botLine.setStyle("-fx-fill: linear-gradient(to right, transparent, rgba(241,196,15,0.55), transparent);");

        VBox content = new VBox(25, topLine, titleBox, countLabel, rosterView, buttons, botLine);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        StackPane root = new StackPane(content);
        root.getStyleClass().add("main-background");

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        String css = Objects.requireNonNull(
                getClass().getResource("/style.css")
        ).toExternalForm();

        scene.getStylesheets().add(css);

        stage.setTitle("Lineup Selection - " + userTeam.getName());
        stage.setScene(scene);
        stage.show();
    }

    private void updateCountLabel(int n) {
        countLabel.setText(picked.size() + " / " + n + " PLAYERS SELECTED");

        if (picked.size() == n) {
            countLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        } else {
            countLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
        }
    }

    private void confirm(int n) {
        if (picked.size() != n) {
            Alert alert = new Alert(
                    Alert.AlertType.WARNING,
                    "You must pick exactly " + n + " players."
            );

            alert.setHeaderText("Invalid Lineup");
            alert.showAndWait();
            return;
        }

        List<Player> roster = userTeam.getRoster();
        List<Player> newOrder = new ArrayList<>(picked);

        for (Player p : roster) {
            if (!newOrder.contains(p)) {
                newOrder.add(p);
            }
        }

        roster.clear();
        roster.addAll(newOrder);

        if (onConfirm != null) {
            onConfirm.run();
        }
    }

    private ListCell<Player> playerCell() {
        return new ListCell<>() {

            @Override
            protected void updateItem(Player p, boolean empty) {
                super.updateItem(p, empty);

                if (empty || p == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(
                            p.getName().toUpperCase()
                                    + "  ("
                                    + p.getPosition()
                                    + " | SKILL: "
                                    + p.getOverallSkill()
                                    + ")"
                    );

                    setStyle(
                            "-fx-text-fill: white; "
                                    + "-fx-font-weight: bold; "
                                    + "-fx-padding: 8;"
                    );
                }
            }
        };
    }
}