package com.c195.controller;

import com.c195.common.CheckedSupplier;
import com.c195.dao.DAOException;
import com.c195.dao.config.DAOConfigException;
import com.c195.dao.config.MysqlConfig;
import com.c195.dao.config.MysqlConnection;
import com.c195.service.MessagingService;
import com.c195.service.ServiceResolver;
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
import java.time.Clock;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * Common functionality.
 */
public class Controller implements Initializable {

    private static final String TITLE = "C195 Scheduling App";

    private Clock clock;
    private static ServiceResolver serviceResolver;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.clock = Clock.systemUTC();
    }

    /**
     * Provides a service resolver instance populated with a database connection
     * and clock. Used to more conveniently access the different service classes
     * without having to initialize all of their dependencies.
     *
     * @return an instance of {@link ServiceResolver}
     */
    protected ServiceResolver serviceResolver() {
        if (serviceResolver == null) {
            serviceResolver = getDatabaseConnection()
                    .map(connection -> new ServiceResolver(connection, clock))
                    .orElse(null);
        }
        return serviceResolver;
    }

    /**
     * Provides a confirmation dialogue before handling the given service operation.
     *
     * @param confirmationSupplier the service operation to be performed as a supplier.
     */
    protected static <T> void confirmationHandler(CheckedSupplier<T> confirmationSupplier) {
        confirmationHandler(confirmationAlert(), confirmationSupplier, Controller::serviceRequestHandler);
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
    protected static <T> Optional<T> confirmationHandler(Alert confirmationAlert,
                                                         CheckedSupplier<T> confirmationSupplier,
                                                         Function<CheckedSupplier<T>, Optional<T>> handler) {
        return confirmationAlert.showAndWait()
                .filter(buttonType -> buttonType == ButtonType.OK)
                .flatMap(buttonResponse -> handler.apply(confirmationSupplier));
    }

    /**
     * Since we will most likely want to prompt the same alert for any database/query specific issues
     * that can occur throughout the app, this *should* allow for a more convenient
     * and reusable way to do so.
     * <p>
     * Checks off the lambda usage requirements for the project as the callers of this function
     * will provide the service layer functionality as a supplier
     * (which uses a functional interface).
     *
     * @param checkedSupplier the service operation to be performed as a supplier.
     * @param <T>             the return type expected from the service operation.
     * @return optional value of what is expected from the service operation.
     */
    protected static <T> Optional<T> serviceRequestHandler(CheckedSupplier<T> checkedSupplier) {
        try {
            return Optional.ofNullable(checkedSupplier.getWithIO());
        } catch (DAOException e) {
            databaseAlert().showAndWait();
        }
        return Optional.empty();
    }

    /**
     * Wraps the view transitioning functionality, which is expected to be consistent across
     * all sections of the app.
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

    private static Alert confirmationAlert() {
        final MessagingService messagingService = ServiceResolver.getMessagingService();
        return infoAlert(messagingService.getConfirmationTitle(),
                messagingService.getConfirmationHeader(),
                messagingService.getConfirmationContent());
    }

    private static Alert databaseAlert() {
        final MessagingService messagingService = ServiceResolver.getMessagingService();
        return errorAlert(messagingService.getDatabaseErrorTitle(),
                messagingService.getDatabaseErrorHeader(),
                messagingService.getDatabaseErrorContent());
    }

    private static Alert unexpectedAlert() {
        final MessagingService messagingService = ServiceResolver.getMessagingService();
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

    private static Optional<Connection> getDatabaseConnection() {
        try {
            return Optional.ofNullable(MysqlConnection.getInstance(MysqlConfig.getInstance()));
        } catch (DAOConfigException e) {
            databaseAlert().showAndWait();
        }
        return Optional.empty();
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
