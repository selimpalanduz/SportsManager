package com.sportsmanager;

import javafx.application.Application;
import javafx.stage.Stage;
import com.sportsmanager.ui.MainMenuController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainMenuController menu = new MainMenuController(primaryStage);
        menu.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}