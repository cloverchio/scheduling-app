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
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Optional;
import java.util.ResourceBundle;

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
     * Displays a confirmation dialogue to the user for specific actions
     * such as deletions. Will only perform the desired action if the user clicks
     * the ok button.
     *
     * @param confirmedDatabaseAction action to be performed upon confirmation.
     * @param <T>                     the return type expected from the database operation.
     */
    public <T> void showConfirmation(CheckedSupplier<T> confirmedDatabaseAction) {
        showConfirmationAlert().ifPresent((response -> {
            if (response == ButtonType.OK) {
                performDatabaseAction(confirmedDatabaseAction);
            }
        }));
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

    private Optional<ButtonType> showConfirmationAlert() {
        return ControllerUtils.infoAlert(messagingService.getConfirmationTitle(),
                messagingService.getConfirmationHeader(),
                messagingService.getConfirmationContent())
                .showAndWait();
    }

    private Optional<ButtonType> showDatabaseAlert() {
        return ControllerUtils.errorAlert(messagingService.getDatabaseErrorTitle(),
                messagingService.getDatabaseErrorHeader(),
                messagingService.getDatabaseErrorContent())
                .showAndWait();
    }

    private Optional<ButtonType> showUnexpectedAlert() {
        return ControllerUtils.errorAlert(messagingService.getUnexpectedErrorTitle(),
                messagingService.getUnexpectedErrorHeader(),
                messagingService.getUnexpectedErrorContent())
                .showAndWait();
    }
}
