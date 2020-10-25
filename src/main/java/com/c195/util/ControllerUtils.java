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
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ControllerUtils {

    public static final String TITLE = "C195 Scheduling App";

    public static Map<Label, TextInputControl> getInvalidTextFields(Map<Label, TextInputControl> textFieldMap) {
        return textFieldMap.entrySet()
                .stream()
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

    public static void displayAsDefault(Label label) {
        label.setStyle("-fx-text-fill: #000000;");
    }

    public static void displayAsRed(Label label) {
        label.setStyle("-fx-text-fill: #FF0000;");
    }

    public static Alert infoAlert(String title, String header, String content) {
        return alert(title, header, content, Alert.AlertType.INFORMATION);
    }

    public static Alert errorAlert(String title, String header, String content) {
        return alert(title, header, content, Alert.AlertType.ERROR);
    }

    public static Alert alert(String title, String header, String content, Alert.AlertType alertType) {
        final Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setResizable(true);
        return alert;
    }

    private static void closeDatabaseConnection() throws RuntimeException {
        try {
            MysqlConnection.close();
        } catch (DAOConfigException e) {
            throw new RuntimeException(e);
        }
    }

    private static Predicate<TextInputControl> validTextInput() {
        return textField -> {
            final String text = textField.getText();
            return text != null && !text.isEmpty();
        };
    }
}
