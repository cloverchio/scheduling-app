package com.c195.controller;

import com.c195.dao.AppointmentDAO;
import com.c195.dao.UserDAO;
import com.c195.model.User;
import com.c195.service.AppointmentService;
import com.c195.service.MessagingService;
import com.c195.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Initial functionality and follow up interactions after login.
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
        Controller.getDatabaseConnection(messagingService)
                .ifPresent(connection -> {
                    this.userService = UserService.getInstance(UserDAO.getInstance(connection));
                    this.appointmentService = AppointmentService.getInstance(AppointmentDAO.getInstance(connection));
                    getUpcomingAppointmentReminder();
                });
    }

    @FXML
    public void manageCustomers(ActionEvent actionEvent) {
        Controller.showView(actionEvent, getClass(), "../view/customer/customer.fxml", messagingService);
    }

    @FXML
    public void logout(ActionEvent actionEvent) {
        userService.logout();
        Controller.showView(actionEvent, getClass(), "../view/login.fxml", messagingService);
    }

    private void getUpcomingAppointmentReminder() {
        userService.getCurrentUser()
                .map(User::getId)
                .ifPresent(this::showAppointmentReminder);
    }

    private void showAppointmentReminder(int userId) {
        Controller.performDatabaseAction(() -> appointmentService.getReminderAppointmentsByUser(userId), messagingService)
                .filter(appointments -> !appointments.isEmpty())
                .ifPresent(appointments -> showAppointmentReminderAlert());
    }

    private static void showAppointmentReminderAlert() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment reminder");
        alert.setHeaderText("You have an upcoming appointment");
        alert.setContentText("You have an appointment scheduled within the next 15 minutes");
        alert.showAndWait();
    }
}
