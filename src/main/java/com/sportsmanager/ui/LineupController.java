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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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

        Label title = new Label("Pick Your Starting " + n);

        title.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: white;"
        );

        Label subtitle = new Label(
                userTeam.getName()
                        + " vs "
                        + (
                        userTeam.equals(match.getHomeTeam())
                                ? match.getAwayTeam().getName()
                                : match.getHomeTeam().getName()
                )
        );

        subtitle.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #aaaaaa;"
        );

        List<Player> available = new ArrayList<>();

        for (Player p : userTeam.getRoster()) {

            if (!p.isInjured()) {
                available.add(p);
            }
        }

        ListView<Player> rosterView = new ListView<>();

        rosterView.getItems().addAll(available);

        rosterView.setCellFactory(lv -> playerCell());

        rosterView.getSelectionModel().setSelectionMode(
                javafx.scene.control.SelectionMode.MULTIPLE
        );

        rosterView.setPrefHeight(360);

        List<Player> sortedBySkill = new ArrayList<>(available);

        sortedBySkill.sort(
                (a, b) -> Integer.compare(
                        b.getOverallSkill(),
                        a.getOverallSkill()
                )
        );

        int defaultPicks = Math.min(n, sortedBySkill.size());

        for (int i = 0; i < defaultPicks; i++) {

            rosterView.getSelectionModel().select(sortedBySkill.get(i));
        }

        picked.clear();

        picked.addAll(
                rosterView.getSelectionModel().getSelectedItems()
        );

        countLabel = new Label(
                picked.size() + " / " + n + " picked"
        );

        countLabel.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #2ecc71;"
        );

        rosterView.getSelectionModel()
                .getSelectedItems()
                .addListener(
                        (javafx.collections.ListChangeListener<Player>) c -> {

                            picked.clear();

                            picked.addAll(
                                    rosterView.getSelectionModel().getSelectedItems()
                            );

                            countLabel.setText(
                                    picked.size() + " / " + n + " picked"
                            );

                            countLabel.setStyle(
                                    "-fx-font-size: 14px; -fx-text-fill: "
                                            + (
                                            picked.size() == n
                                                    ? "#2ecc71"
                                                    : "#e67e22"
                                    )
                                            + ";"
                            );
                        }
                );

        Button autoBtn = new Button("Auto-Pick Best");

        autoBtn.setPrefWidth(150);
        autoBtn.setPrefHeight(40);

        autoBtn.setStyle(
                "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 8;"
        );

        autoBtn.setOnAction(e -> {

            rosterView.getSelectionModel().clearSelection();

            for (int i = 0; i < defaultPicks; i++) {

                rosterView.getSelectionModel().select(sortedBySkill.get(i));
            }
        });

        Button confirmBtn = new Button("Confirm & Kick Off");

        confirmBtn.setPrefWidth(180);
        confirmBtn.setPrefHeight(45);

        confirmBtn.setStyle(
                "-fx-background-color: #2ecc71; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );

        confirmBtn.setOnAction(e -> confirm(n));

        Button cancelBtn = new Button("Cancel");

        cancelBtn.setPrefWidth(120);
        cancelBtn.setPrefHeight(45);

        cancelBtn.setStyle(
                "-fx-background-color: #e74c3c; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 8;"
        );

        cancelBtn.setOnAction(e -> {

            if (onCancel != null) {
                onCancel.run();
            }
        });

        HBox buttons = new HBox(
                15,
                autoBtn,
                confirmBtn,
                cancelBtn
        );

        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(
                12,
                title,
                subtitle,
                countLabel,
                rosterView,
                buttons
        );

        layout.setAlignment(Pos.TOP_CENTER);

        layout.setPadding(new Insets(25));

        layout.setStyle("-fx-background-color: #1E1E2E;");

        Scene scene = new Scene(layout, 700, 600);

        stage.setTitle("Lineup - " + userTeam.getName());

        stage.setScene(scene);

        stage.show();
    }

    private void confirm(int n) {

        if (picked.size() != n) {

            new Alert(
                    Alert.AlertType.WARNING,
                    "You must pick exactly " + n + " players."
            ).showAndWait();

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

        return new ListCell<Player>() {

            @Override
            protected void updateItem(Player p, boolean empty) {

                super.updateItem(p, empty);

                setText(
                        empty || p == null
                                ? null
                                : p.getName()
                                + "  ("
                                + p.getPosition()
                                + ", skill "
                                + p.getOverallSkill()
                                + ")"
                );
            }
        };
    }
}