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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

/**
 * LineupController v2 — Starting XI (top panel) + 7-man Bench (bottom panel).
 *
 * Bench distribution: 2 Forward · 2 Midfielder · 2 Defender · 1 Goalkeeper.
 * The user selects the Starting XI by clicking rows; substitutes are
 * auto-assigned from the remaining fit players via Team.autoAssignSubstitutes().
 */
public class LineupController {

    private final Stage stage;
    private final ISport sport;
    private final Team   userTeam;
    private final Match  match;

    private Runnable onConfirm;
    private Runnable onCancel;

    private final List<Player> picked = new ArrayList<>();
    private Label countLabel;

    // ── Cached sorted pool ────────────────────────────────────────
    private List<Player> available;
    private List<Player> sortedBySkill;
    private int squadSize;

    // ── Substitute list (rendered separately) ────────────────────
    private VBox subListBox;

    public LineupController(Stage stage, ISport sport, Team userTeam, Match match) {
        this.stage    = stage;
        this.sport    = sport;
        this.userTeam = userTeam;
        this.match    = match;
    }

    public void setOnConfirm(Runnable r) { this.onConfirm = r; }
    public void setOnCancel(Runnable r)  { this.onCancel  = r; }

    // ─────────────────────────────────────────────────────────────
    public void show() {
        squadSize = sport.getSquadSize();

        // Build available pool (non-injured players)
        available = new ArrayList<>();
        for (Player p : userTeam.getRoster()) {
            if (!p.isInjured()) available.add(p);
        }

        // Sort descending by skill for auto-pick
        sortedBySkill = new ArrayList<>(available);
        sortedBySkill.sort((a, b) -> Integer.compare(b.getOverallSkill(), a.getOverallSkill()));

        // Default: pick top-N as starting XI
        picked.clear();
        int defaultPicks = Math.min(squadSize, sortedBySkill.size());
        for (int i = 0; i < defaultPicks; i++) picked.add(sortedBySkill.get(i));

        // ── Top neon line ─────────────────────────────────────────
        Rectangle topLine = new Rectangle(SCENE_WIDTH - 80, 2);
        topLine.setStyle("-fx-fill: linear-gradient(to right, transparent, #2ecc71, transparent);");

        // ── Page title ────────────────────────────────────────────
        Label title = new Label("PICK YOUR STARTING " + squadSize);
        title.getStyleClass().add("title-label");

        String opponent = userTeam.equals(match.getHomeTeam())
                ? match.getAwayTeam().getName()
                : match.getHomeTeam().getName();
        Label vsLbl = new Label("VERSUS  " + opponent.toUpperCase());
        vsLbl.getStyleClass().add("vs-label");

        VBox titleBox = new VBox(4, title, vsLbl);
        titleBox.setAlignment(Pos.CENTER);

        // ── Count label ───────────────────────────────────────────
        countLabel = new Label();
        countLabel.getStyleClass().add("period-label");
        refreshCountLabel();

        // ── Starting XI panel ─────────────────────────────────────
        Label xiHeader = new Label("▶  STARTING ELEVEN");
        xiHeader.getStyleClass().add("lineup-section-header");

        VBox xiListBox = buildRosterRows();

        VBox xiPanel = new VBox(10, xiHeader, xiListBox);
        xiPanel.getStyleClass().add("lineup-panel");
        xiPanel.setPadding(new Insets(16, 20, 16, 20));
        xiPanel.setMaxWidth(560);
        VBox.setVgrow(xiPanel, Priority.NEVER);

        // ── Substitutes panel ─────────────────────────────────────
        Label subHeader = new Label("⬡  SUBSTITUTES  (7)   ·   2 FWD · 2 MID · 2 DEF · 1 GK");
        subHeader.getStyleClass().add("lineup-sub-header");

        subListBox = new VBox(4);

        VBox subPanel = new VBox(10, subHeader, subListBox);
        subPanel.getStyleClass().add("sub-panel");
        subPanel.setPadding(new Insets(14, 20, 14, 20));
        subPanel.setMaxWidth(560);

        refreshSubstitutePanel();

        // ── Action buttons ────────────────────────────────────────
        Button autoBtn = new Button("⚡  AUTO-PICK");
        autoBtn.getStyleClass().add("menu-button");
        autoBtn.setPrefWidth(160);
        autoBtn.setPrefHeight(46);
        autoBtn.setOnAction(e -> {
            picked.clear();
            for (int i = 0; i < Math.min(squadSize, sortedBySkill.size()); i++) {
                picked.add(sortedBySkill.get(i));
            }
            rebuildRosterRows(xiListBox);
            refreshCountLabel();
            refreshSubstitutePanel();
        });

        Button confirmBtn = new Button("⚽  CONFIRM & START");
        confirmBtn.getStyleClass().add("start-button");
        confirmBtn.setPrefWidth(230);
        confirmBtn.setPrefHeight(46);
        confirmBtn.setOnAction(e -> confirm());

        Button cancelBtn = new Button("✕  CANCEL");
        cancelBtn.getStyleClass().add("back-button");
        cancelBtn.setPrefWidth(140);
        cancelBtn.setPrefHeight(46);
        cancelBtn.setOnAction(e -> { if (onCancel != null) onCancel.run(); });

        HBox buttons = new HBox(18, autoBtn, confirmBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        // ── Bottom line ───────────────────────────────────────────
        Rectangle botLine = new Rectangle(SCENE_WIDTH - 80, 2);
        botLine.setStyle("-fx-fill: linear-gradient(to right, transparent, rgba(241,196,15,0.55), transparent);");

        // ── Content ───────────────────────────────────────────────
        VBox content = new VBox(16,
                topLine, titleBox, countLabel,
                xiPanel, subPanel,
                buttons, botLine);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setStyle("-fx-background-color: transparent;");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        StackPane root = new StackPane(scroll);
        root.getStyleClass().add("main-background");

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/style.css")).toExternalForm());

        stage.setTitle("Lineup Selection  ·  " + userTeam.getName());
        stage.setScene(scene);
        stage.show();
    }

    // ── Build clickable player rows for Starting XI ───────────────
    private VBox buildRosterRows() {
        VBox box = new VBox(2);
        for (Player p : available) {
            box.getChildren().add(makeRosterRow(p));
        }
        return box;
    }

    private void rebuildRosterRows(VBox target) {
        target.getChildren().clear();
        for (Player p : available) {
            target.getChildren().add(makeRosterRow(p));
        }
    }

    private HBox makeRosterRow(Player p) {
        boolean selected = picked.contains(p);

        // Position badge
        Label posBadge = new Label(abbreviate(p.getPosition()));
        posBadge.setPrefWidth(36);
        posBadge.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-alignment: center;"
                        + "-fx-text-fill: " + posColor(p.getPosition()) + ";"
                        + "-fx-background-color: " + posBg(p.getPosition()) + ";"
                        + "-fx-background-radius: 5; -fx-padding: 2 4;");

        // Name
        Label nameLbl = new Label(p.getName().toUpperCase());
        nameLbl.setStyle("-fx-text-fill: " + (selected ? "#ffffff" : "rgba(255,255,255,0.70)")
                + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        HBox.setHgrow(nameLbl, Priority.ALWAYS);

        // Skill pill
        Label skillLbl = new Label("OVR  " + p.getOverallSkill());
        skillLbl.setStyle("-fx-text-fill: rgba(241,196,15,0.90); -fx-font-size: 11px;"
                + "-fx-font-weight: bold;");

        // Selection indicator
        Label checkLbl = new Label(selected ? "✔" : "○");
        checkLbl.setPrefWidth(22);
        checkLbl.setStyle("-fx-text-fill: " + (selected ? "#2ecc71" : "rgba(255,255,255,0.25)")
                + "; -fx-font-size: 14px; -fx-alignment: center;");

        HBox row = new HBox(10, posBadge, nameLbl, skillLbl, checkLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(7, 10, 7, 10));
        row.setMaxWidth(Double.MAX_VALUE);
        row.setStyle(selected
                ? "-fx-background-color: rgba(46,204,113,0.14);"
                + "-fx-border-color: rgba(46,204,113,0.35);"
                + "-fx-border-width: 0 0 1 0;"
                : "-fx-background-color: transparent;"
                + "-fx-border-color: rgba(255,255,255,0.06);"
                + "-fx-border-width: 0 0 1 0;");
        row.setStyle(row.getStyle() + " -fx-cursor: hand;");

        // Toggle on click
        row.setOnMouseClicked(e -> {
            if (picked.contains(p)) {
                picked.remove(p);
            } else {
                if (picked.size() < squadSize) {
                    picked.add(p);
                }
            }
            // Rebuild this row in its parent
            VBox parent = (VBox) row.getParent();
            int idx = parent.getChildren().indexOf(row);
            parent.getChildren().set(idx, makeRosterRow(p));
            refreshCountLabel();
            refreshSubstitutePanel();
        });

        return row;
    }

    // ── Refresh substitute panel ──────────────────────────────────
    private void refreshSubstitutePanel() {
        // Delegate to Team's smart auto-assign
        userTeam.autoAssignSubstitutes(picked);
        List<Player> subs = userTeam.getSubstitutes();

        subListBox.getChildren().clear();

        if (subs.isEmpty()) {
            Label empty = new Label("No substitutes available.");
            empty.setStyle("-fx-text-fill: rgba(255,255,255,0.35); -fx-font-size: 12px;");
            subListBox.getChildren().add(empty);
            return;
        }

        for (Player p : subs) {
            subListBox.getChildren().add(makeSubRow(p));
        }
    }

    private HBox makeSubRow(Player p) {
        Label posBadge = new Label(abbreviate(p.getPosition()));
        posBadge.setPrefWidth(36);
        posBadge.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-alignment: center;"
                        + "-fx-text-fill: " + posColor(p.getPosition()) + ";"
                        + "-fx-background-color: " + posBg(p.getPosition()) + ";"
                        + "-fx-background-radius: 5; -fx-padding: 2 4;");

        Label nameLbl = new Label(p.getName().toUpperCase());
        nameLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.75); -fx-font-size: 12px;"
                + "-fx-font-weight: bold;");
        HBox.setHgrow(nameLbl, Priority.ALWAYS);

        Label skillLbl = new Label("OVR  " + p.getOverallSkill());
        skillLbl.setStyle("-fx-text-fill: rgba(241,196,15,0.75); -fx-font-size: 11px;");

        Label subIcon = new Label("↕");
        subIcon.setStyle("-fx-text-fill: rgba(241,196,15,0.60); -fx-font-size: 13px;");

        HBox row = new HBox(10, posBadge, nameLbl, skillLbl, subIcon);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 10, 6, 10));
        row.setMaxWidth(Double.MAX_VALUE);
        row.setStyle("-fx-background-color: transparent;"
                + "-fx-border-color: rgba(241,196,15,0.10);"
                + "-fx-border-width: 0 0 1 0;");
        return row;
    }

    // ── Count label ───────────────────────────────────────────────
    private void refreshCountLabel() {
        countLabel.setText(picked.size() + " / " + squadSize + "  PLAYERS SELECTED");
        countLabel.setStyle(picked.size() == squadSize
                ? "-fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 13px;"
                : "-fx-text-fill: #e67e22; -fx-font-weight: bold; -fx-font-size: 13px;");
    }

    // ── Confirm ───────────────────────────────────────────────────
    private void confirm() {
        if (picked.size() != squadSize) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "You must select exactly " + squadSize + " players.");
            alert.setHeaderText("Invalid Lineup");
            alert.showAndWait();
            return;
        }

        // Reorder roster: starters first, bench next, injured last
        List<Player> newOrder = new ArrayList<>(picked);
        for (Player p : userTeam.getSubstitutes()) {
            if (!newOrder.contains(p)) newOrder.add(p);
        }
        for (Player p : userTeam.getRoster()) {
            if (!newOrder.contains(p)) newOrder.add(p);
        }
        userTeam.getRoster().clear();
        userTeam.getRoster().addAll(newOrder);

        if (onConfirm != null) onConfirm.run();
    }

    // ── Position helpers ──────────────────────────────────────────
    private static String abbreviate(String pos) {
        if (pos == null) return "?";
        String p = pos.toLowerCase();
        if (p.contains("goal"))   return "GK";
        if (p.contains("def"))    return "DEF";
        if (p.contains("mid"))    return "MID";
        if (p.contains("forward") || p.contains("fwd") || p.contains("att")) return "FWD";
        return pos.length() > 3 ? pos.substring(0, 3).toUpperCase() : pos.toUpperCase();
    }

    private static String posColor(String pos) {
        if (pos == null) return "white";
        String p = pos.toLowerCase();
        if (p.contains("goal"))  return "rgba(241,196,15,0.95)";
        if (p.contains("def"))   return "rgba(52,152,219,0.95)";
        if (p.contains("mid"))   return "rgba(46,204,113,0.95)";
        return "rgba(231,76,60,0.95)";  // Forward / attacker
    }

    private static String posBg(String pos) {
        if (pos == null) return "rgba(255,255,255,0.08)";
        String p = pos.toLowerCase();
        if (p.contains("goal"))  return "rgba(241,196,15,0.12)";
        if (p.contains("def"))   return "rgba(52,152,219,0.12)";
        if (p.contains("mid"))   return "rgba(46,204,113,0.12)";
        return "rgba(231,76,60,0.12)";
    }
}