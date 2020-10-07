package com.c195.controller;

import com.c195.common.CheckedSupplier;
import com.c195.dao.DAOException;
import com.c195.dao.config.DAOConfigException;
import com.c195.dao.config.MysqlConfig;
import com.c195.dao.config.MysqlConnection;
import com.c195.service.MessagingService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Common functionality.
 */
public class Controller implements Initializable {

    public static final String TITLE = "C195 Scheduling App";

    private MessagingService messagingService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.messagingService = MessagingService.getInstance();
    }

    public MessagingService getMessagingService() {
        return messagingService;
    }

    /**
     * This is probably crossing the line but the result of an idea I had to reuse the form field
     * validation in conjunction with a database operation. Which are things we will most likely
     * want to do on every form submission across the project.
     *
     * @param messageLabel       label in which the validation message will be set to.
     * @param formFields         fields to perform validation on.
     * @param formDatabaseAction database action to be performed if no invalid fields.
     * @param <T>                the return type expected from the database operation.
     * @return optional of the database operation's expected return type.
     */
    public <T> Optional<T> formFieldSubmitAction(Label messageLabel, Map<Label, TextField> formFields,
                                                 CheckedSupplier<T> formDatabaseAction) {
        final Map<Label, TextField> invalidFields = getInvalidTextFields(formFields);
        if (!invalidFields.isEmpty()) {
            showRequiredFieldMessage(messageLabel, invalidFields.keySet());
            return Optional.empty();
        } else {
            return performDatabaseAction(formDatabaseAction);
        }
    }

    /**
     * Since we will most likely want to prompt the same alert for any database/query specific issues
     * that will occur across most of the app, this *should* allow for a convenient way to do so.
     * Should also check off the lambda usage requirements for the project as all callers of this function
     * can provide the database action using the functional interface.
     *
     * @param checkedSupplier the database operation to be performed as a supplier.
     * @param <T>             the return type expected from the database operation.
     * @return optional of the database operation's expected return type.
     */
    public <T> Optional<T> performDatabaseAction(CheckedSupplier<T> checkedSupplier) {
        try {
            return Optional.ofNullable(checkedSupplier.getWithIO());
        } catch (DAOException e) {
            showDatabaseAlert();
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Provides the database connection instance. Will prompt an alert in the event
     * that an exception is thrown while trying to establish the connection.
     *
     * @return optional instance of the database connection.
     */
    public Optional<Connection> getDatabaseConnection() {
        try {
            return Optional.ofNullable(MysqlConnection.getInstance(MysqlConfig.getInstance()));
        } catch (DAOConfigException e) {
            showDatabaseAlert();
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Wraps the view transitioning functionality. Which is expected to remain the same across
     * all sections of the app. With the exception of maybe the main entry point {@link com.c195.App}.
     *
     * @param actionEvent event that corresponds with the transition.
     * @param clazz       controller class.
     * @param viewPath    path of the view to transition to.
     */
    public void showView(ActionEvent actionEvent, Class<?> clazz, String viewPath) {
        try {
            showEventView(actionEvent, clazz, viewPath);
        } catch (IOException e) {
            showUnexpectedAlert();
            e.printStackTrace();
        }
    }

    /**
     * Sets a given label to the invalid field message with the labels
     * of the offending text fields appended.
     *
     * @param messageLabel  to set the invalid field message to.
     * @param invalidLabels labels that correspond to the invalid text fields.
     */
    public void showRequiredFieldMessage(Label messageLabel, Set<Label> invalidLabels) {
        final String requiredFields = invalidLabels.stream()
                .map(Labeled::getText)
                .collect(Collectors.joining(", "));
        messageLabel.setText(messagingService.getRequiredFields() + ": " + requiredFields);
        displayAsRed(messageLabel);
    }

    /**
     * Given a map of a labels to their corresponding text fields, this will identify
     * ones that did not provided valid input (empty or null).
     *
     * @param textFieldMap map of labels to text fields.
     * @return map of labels and text fields that did not provide valid input.
     */
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
        final Parent root = FXMLLoader.load(clazz.getResource(viewPath));
        final Scene scene = new Scene(root);
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

    private void showDatabaseAlert() {
        errorAlert(messagingService.getDatabaseErrorTitle(),
                messagingService.getDatabaseErrorHeader(),
                messagingService.getDatabaseErrorContent())
                .showAndWait();
    }

    private void showUnexpectedAlert() {
        errorAlert(messagingService.getUnexpectedErrorTitle(),
                messagingService.getUnexpectedErrorHeader(),
                messagingService.getUnexpectedErrorContent())
                .showAndWait();
    }

    private static Predicate<TextField> validTextInput() {
        return textField -> {
            final String text = textField.getText();
            return text != null && !text.isEmpty();
        };
    }

    private static Alert errorAlert(String title, String header, String content) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }
}
