package com.sportsmanager;

import com.sportsmanager.ui.MainMenuController;
import javafx.application.Application;
import javafx.stage.Stage;

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