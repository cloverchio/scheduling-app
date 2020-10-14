package com.c195.controller;

import com.c195.common.CheckedSupplier;
import com.c195.dao.DAOException;
import com.c195.dao.config.DAOConfigException;
import com.c195.dao.config.MysqlConfig;
import com.c195.dao.config.MysqlConnection;
import com.c195.service.MessagingService;
import com.c195.util.ControllerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Common functionality.
 */
public class Controller implements Initializable {

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
        final Map<Label, TextField> invalidFields = ControllerUtils.getInvalidTextFields(formFields);
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
            ControllerUtils.showEventView(actionEvent, clazz, viewPath);
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
        ControllerUtils.displayAsRed(messageLabel);
    }

    private void showDatabaseAlert() {
        ControllerUtils.errorAlert(messagingService.getDatabaseErrorTitle(),
                messagingService.getDatabaseErrorHeader(),
                messagingService.getDatabaseErrorContent())
                .showAndWait();
    }

    private void showUnexpectedAlert() {
        ControllerUtils.errorAlert(messagingService.getUnexpectedErrorTitle(),
                messagingService.getUnexpectedErrorHeader(),
                messagingService.getUnexpectedErrorContent())
                .showAndWait();
    }
}
