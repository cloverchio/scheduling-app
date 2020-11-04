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
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * Common functionality.
 */
public class Controller implements Initializable {

    private static final String TITLE = "C195 Scheduling App";

    private MessagingService messagingService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.messagingService = MessagingService.getInstance();
    }

    protected MessagingService getMessagingService() {
        return messagingService;
    }

    /**
     * Provides a confirmation dialogue before handling the given service operation.
     *
     * @param confirmationSupplier the service operation to be performed as a supplier.
     */
    protected <T> void confirmationHandler(CheckedSupplier<T> confirmationSupplier) {
        confirmationHandler(confirmationAlert(), confirmationSupplier, this::serviceRequestHandler);
    }

    /**
     * Provides a confirmation dialogue before handling the given service operation.
     *
     * @param confirmationAlert    the alert that will represent the confirmation dialogue.
     * @param confirmationSupplier the service operation to be performed as a supplier.
     * @param handler              the handler to use perform the service operation.
     * @param <T>                  the return type expected from the service operation.
     * @return optional value of what is expected from the service operation.
     */
    protected <T> Optional<T> confirmationHandler(Alert confirmationAlert,
                                                  CheckedSupplier<T> confirmationSupplier,
                                                  Function<CheckedSupplier<T>, Optional<T>> handler) {
        return confirmationAlert.showAndWait()
                .filter(buttonType -> buttonType == ButtonType.OK)
                .flatMap(buttonResponse -> handler.apply(confirmationSupplier));
    }

    /**
     * Since we will most likely want to prompt the same alert for any database/query specific issues
     * that will occur across most of the app, this *should* allow for a convenient way to do so.
     * Checks off the lambda usage requirements for the project as all callers of this function
     * can provide the service action using the functional interface.
     *
     * @param checkedSupplier the service operation to be performed as a supplier.
     * @param <T>             the return type expected from the service operation.
     * @return optional value of what is expected from the service operation.
     */
    protected <T> Optional<T> serviceRequestHandler(CheckedSupplier<T> checkedSupplier) {
        try {
            return Optional.ofNullable(checkedSupplier.getWithIO());
        } catch (DAOException e) {
            databaseAlert().showAndWait();
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
    protected Optional<Connection> getDatabaseConnection() {
        try {
            return Optional.ofNullable(MysqlConnection.getInstance(MysqlConfig.getInstance()));
        } catch (DAOConfigException e) {
            databaseAlert().showAndWait();
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
    protected void eventViewHandler(ActionEvent actionEvent, Class<?> clazz, String viewPath) {
        try {
            viewHandler(actionEvent, clazz, viewPath);
        } catch (IOException e) {
            unexpectedAlert().showAndWait();
            e.printStackTrace();
        }
    }

    public static void eventViewHandler(Class<?> clazz, String viewPath) throws IOException {
        setStage(FXMLLoader.load(clazz.getResource(viewPath)));
    }

    protected static void eventStageHandler(ActionEvent actionEvent, Parent parent) {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        setStage(parent);
    }

    protected static Alert infoAlert(String title, String header, String content) {
        return alert(title, header, content, Alert.AlertType.INFORMATION);
    }

    protected static Alert errorAlert(String title, String header, String content) {
        return alert(title, header, content, Alert.AlertType.ERROR);
    }

    private static void viewHandler(ActionEvent actionEvent, Class<?> clazz, String viewPath) throws IOException {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        eventViewHandler(clazz, viewPath);
    }

    private Alert confirmationAlert() {
        return infoAlert(messagingService.getConfirmationTitle(),
                messagingService.getConfirmationHeader(),
                messagingService.getConfirmationContent());
    }

    private Alert databaseAlert() {
        return errorAlert(messagingService.getDatabaseErrorTitle(),
                messagingService.getDatabaseErrorHeader(),
                messagingService.getDatabaseErrorContent());
    }

    private Alert unexpectedAlert() {
        return errorAlert(messagingService.getUnexpectedErrorTitle(),
                messagingService.getUnexpectedErrorHeader(),
                messagingService.getUnexpectedErrorContent());
    }

    private static void setStage(Parent parent) {
        final Scene scene = new Scene(parent);
        final Stage stage = new Stage();
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> closeDatabaseConnection());
    }

    private static void closeDatabaseConnection() throws RuntimeException {
        try {
            MysqlConnection.close();
        } catch (DAOConfigException e) {
            throw new RuntimeException(e);
        }
    }

    private static Alert alert(String title, String header, String content, Alert.AlertType alertType) {
        final Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setResizable(true);
        return alert;
    }
}
