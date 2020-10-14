package com.c195.util;

import com.c195.dao.config.DAOConfigException;
import com.c195.dao.config.MysqlConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ControllerUtils {

    public static final String TITLE = "C195 Scheduling App";

    public static Map<Label, TextField> getInvalidTextFields(Map<Label, TextField> textFieldMap) {
        return textFieldMap.entrySet().stream()
                .filter(entry -> validTextInput().negate().test(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static void showEventView(ActionEvent actionEvent, Class<?> clazz, String viewPath) throws IOException {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        showView(clazz, viewPath);
    }

    public static void showView(Class<?> clazz, String viewPath) throws IOException {
        setStage(FXMLLoader.load(clazz.getResource(viewPath)));
    }

    public static void setEventStage(ActionEvent actionEvent, Parent parent) {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        setStage(parent);
    }

    public static void setStage(Parent parent) {
        final Scene scene = new Scene(parent);
        final Stage stage = new Stage();
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> closeDatabaseConnection());
    }

    public static void closeDatabaseConnection() throws RuntimeException {
        try {
            MysqlConnection.close();
        } catch (DAOConfigException e) {
            throw new RuntimeException(e);
        }
    }

    public static void displayAsRed(Label label) {
        label.setStyle("-fx-text-fill: #FF0000;");
    }

    public static Predicate<TextField> validTextInput() {
        return textField -> {
            final String text = textField.getText();
            return text != null && !text.isEmpty();
        };
    }

    public static Alert errorAlert(String title, String header, String content) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }
}
