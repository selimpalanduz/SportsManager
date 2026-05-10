package com.sportsmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class Main extends Application {


    public static final int SCENE_WIDTH  = 1280;
    public static final int SCENE_HEIGHT = 720;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/MainMenu.fxml")));

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        String css = Objects.requireNonNull(
                getClass().getResource("/style.css")).toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Sports Manager - Ultimate Edition");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}