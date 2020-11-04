package com.c195;

import com.c195.controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Controller.eventViewHandler(getClass(), "view/login.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
