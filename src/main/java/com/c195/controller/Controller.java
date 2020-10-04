package com.c195.controller;

import com.c195.common.CheckedSupplier;
import com.c195.dao.DAOException;
import com.c195.service.MessagingService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Making the assumption that inheritance with JavaFX controllers is probably
 * more of a pain than I want to deal with right now. That being said, this
 * class represents what perhaps a base controller class would look like in my case
 * by including some common functionality.
 */
public class Controller {

    public static final String TITLE = "C195 Scheduling App";

    /**
     * Since we will most likely want to prompt the same alert for any database/query specific issues
     * that will occur across most of the app, this *should* allow for a convenient way to do so.
     * Should also check off the lambda usage requirements for the project as all callers of this function
     * can provide the database action using the functional interface.
     *
     * @param checkedSupplier  the database operation to be performed as a supplier.
     * @param messagingService for the alert messaging.
     * @param <T>              the return type expected from the database operation.
     * @return optional of the database operation's expected return type.
     */
    public static <T> Optional<T> performDatabaseAction(CheckedSupplier<T> checkedSupplier, MessagingService messagingService) {
        try {
            return Optional.ofNullable(checkedSupplier.getWithIO());
        } catch (DAOException e) {
            showDatabaseAlert(messagingService);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Wraps the view transitioning functionality. Which is expected to remain the same across
     * all sections of the app. With the exception of maybe the main entry point {@link com.c195.App}.
     *
     * @param actionEvent      event that corresponds with the transition.
     * @param clazz            controller class.
     * @param viewPath         path of the view to transition to.
     * @param messagingService for error messaging.
     */
    public static void showView(ActionEvent actionEvent, Class<?> clazz,
                                String viewPath, MessagingService messagingService) {
        try {
            showView(actionEvent, clazz, viewPath);
        } catch (IOException e) {
            showUnexpectedAlert(messagingService);
            e.printStackTrace();
        }
    }

    public static void showView(ActionEvent actionEvent, Class<?> clazz, String viewPath) throws IOException {
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
    }

    public static void showDatabaseAlert(MessagingService messagingService) {
        errorAlert(messagingService.getDatabaseErrorTitle(),
                messagingService.getDatabaseErrorHeader(),
                messagingService.getDatabaseErrorContent())
                .showAndWait();
    }

    public static void showUnexpectedAlert(MessagingService messagingService) {
        errorAlert(messagingService.getUnexpectedErrorTitle(),
                messagingService.getUnexpectedErrorHeader(),
                messagingService.getUnexpectedErrorContent())
                .showAndWait();
    }

    private static Alert errorAlert(String title, String header, String content) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }
}
