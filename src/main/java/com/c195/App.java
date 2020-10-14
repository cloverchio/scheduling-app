package com.c195;

import com.c195.util.ControllerUtils;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ControllerUtils.showView(getClass(), "view/login.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
