package com.c195.controller;

import com.c195.dao.AppointmentDAO;
import com.c195.dao.DAOException;
import com.c195.dao.UserDAO;
import com.c195.dao.config.DAOConfigException;
import com.c195.dao.config.MysqlConfig;
import com.c195.dao.config.MysqlConnection;
import com.c195.model.User;
import com.c195.service.AppointmentService;
import com.c195.service.MessagingService;
import com.c195.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

/**
 * Landing page and follow up interactions after login.
 * <p>
 * Addresses the following task requirements:
 * <p>
 * H. Write code to provide an alert if there is an appointment within 15 minutes of the user’s log-in.
 */
public class MainController implements Initializable {

    private MessagingService messagingService;
    private UserService userService;
    private AppointmentService appointmentService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.messagingService = MessagingService.getInstance();
        try {
            final Connection connection = MysqlConnection.getInstance(MysqlConfig.getInstance());
            this.userService = UserService.getInstance(UserDAO.getInstance(connection));
            this.appointmentService = AppointmentService.getInstance(AppointmentDAO.getInstance(connection));
            getUpcomingAppointmentReminder();
        } catch (DAOConfigException e) {
            Controller.showDatabaseAlert(messagingService);
            e.printStackTrace();
        }
    }

    @FXML
    public void logout(ActionEvent actionEvent) {
        try {
            userService.logout();
            getLoginView(actionEvent);
        } catch (IOException e) {
            Controller.showUnexpectedAlert(messagingService);
            e.printStackTrace();
        }
    }

    private void getUpcomingAppointmentReminder() {
        userService.getCurrentUser()
                .map(User::getId)
                .ifPresent(this::showAppointmentReminder);
    }

    private void showAppointmentReminder(int userId) {
        try {
            if (!appointmentService.getReminderAppointmentsByUser(userId).isEmpty()) {
                showAppointmentReminderAlert();
            }
        } catch (DAOException e) {
            Controller.showDatabaseAlert(messagingService);
            e.printStackTrace();
        }
    }

    private void showAppointmentReminderAlert() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment reminder");
        alert.setHeaderText("You have an upcoming appointment");
        alert.setContentText("You have an appointment scheduled within the next 15 minutes");
        alert.showAndWait();
    }

    private void getLoginView(ActionEvent actionEvent) throws IOException {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        final Parent root = FXMLLoader.load(getClass().getResource("../view/login.fxml"));
        final Scene scene = new Scene(root);
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}
