package com.sportsmanager.ui;

import com.sportsmanager.core.LeagueManager;
import com.sportsmanager.model.common.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import static com.sportsmanager.Main.SCENE_HEIGHT;
import static com.sportsmanager.Main.SCENE_WIDTH;

public class TeamController {

    private final Stage stage;
    private final String sportType;
    private final Team team;
    private final League league;
    private final LeagueManager leagueManager;
    private final Team userTeam;

    private Runnable onBack;

    public TeamController(
            Stage stage,
            String sportType,
            Team team,
            League league,
            LeagueManager leagueManager,
            Team userTeam
    ) {
        this.stage = stage;
        this.sportType = sportType;
        this.team = team;
        this.league = league;
        this.leagueManager = leagueManager;
        this.userTeam = userTeam;
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void show() {

        Label title = new Label(team.getName().toUpperCase());
        title.getStyleClass().add("game-title");

        Label xpLabel = new Label("⚡ TEAM XP: " + team.getXp());
        xpLabel.getStyleClass().add("vs-label");
        xpLabel.setStyle("-fx-font-size: 16px;");
        Label managerLabel = null;
        if (userTeam != null && userTeam.equals(team) && team.getManagerName() != null) {
        managerLabel = new Label("Manager: " + team.getManagerName());
        managerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(46,204,113,0.85); -fx-font-weight: bold;");
        }

        Label rosterTitle = new Label("ROSTER");
        rosterTitle.getStyleClass().add("period-label");

        TableView<Player> playersTable = createPlayersTable();

        Label coachTitle = new Label("COACHING STAFF");
        coachTitle.getStyleClass().add("period-label");

        TableView<Coach> coachesTable = createCoachesTable();

        Button trainBtn = new Button("💪 TRAIN TEAM (+100 XP)");
        trainBtn.getStyleClass().add("start-button");

        boolean canTrain =
                userTeam != null
                        && team.getName().equals(userTeam.getName())
                        && !team.isTrainedThisWeek();

        trainBtn.setDisable(!canTrain);

        trainBtn.setOnAction(e -> {
            team.addXp(100);
            team.setTrainedThisWeek(true);
            show();
        });

        Button backBtn = new Button("← BACK TO LEAGUE");
        backBtn.getStyleClass().add("back-button");
        backBtn.setPrefWidth(200);

        backBtn.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        HBox buttons = new HBox(20, trainBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15);
        layout.getChildren().addAll(title, xpLabel);
        if (managerLabel != null) layout.getChildren().add(managerLabel);
        layout.getChildren().addAll(rosterTitle, playersTable, coachTitle, coachesTable, buttons);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("main-background");

        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);

        scene.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.show();
    }

    private TableView<Player> createPlayersTable() {

        TableView<Player> table = new TableView<>();

        table.getStyleClass().add("standings-table");
        table.setPrefHeight(250);

        TableColumn<Player, String> nameCol =
                new TableColumn<>("Player Name");

        nameCol.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getName())
        );

        nameCol.setPrefWidth(200);

        TableColumn<Player, String> posCol =
                new TableColumn<>("Pos");

        posCol.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getPosition())
        );

        posCol.setPrefWidth(100);

        TableColumn<Player, String> skillCol =
                new TableColumn<>("Skill");

        skillCol.setCellValueFactory(
                d -> new SimpleStringProperty(
                        String.valueOf(d.getValue().getOverallSkill())
                )
        );

        skillCol.setPrefWidth(80);

        TableColumn<Player, String> statusCol =
                new TableColumn<>("Status");

        statusCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().isInjured()
                                ? "INJURED (" +
                                d.getValue().getInjuryDuration() +
                                "w)"
                                : "Fit"
                )
        );

        statusCol.setPrefWidth(120);

        table.getColumns().addAll(
                nameCol,
                posCol,
                skillCol,
                statusCol
        );

        table.getItems().addAll(team.getRoster());

        return table;
    }

    private TableView<Coach> createCoachesTable() {

        TableView<Coach> table = new TableView<>();

        table.getStyleClass().add("standings-table");
        table.setPrefHeight(100);

        TableColumn<Coach, String> nameCol =
                new TableColumn<>("Coach Name");

        nameCol.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getName())
        );

        nameCol.setPrefWidth(300);

        TableColumn<Coach, String> expCol =
                new TableColumn<>("Experience");

        expCol.setCellValueFactory(
                d -> new SimpleStringProperty(
                        String.valueOf(d.getValue().getExperience())
                )
        );

        expCol.setPrefWidth(150);

        table.getColumns().addAll(nameCol, expCol);

        table.getItems().addAll(team.getCoaches());

        return table;
    }
}