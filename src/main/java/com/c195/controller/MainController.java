package com.c195.controller;

import com.c195.common.UserDTO;
import com.c195.dao.AppointmentDAO;
import com.c195.dao.UserDAO;
import com.c195.service.AppointmentService;
import com.c195.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Initial functionality.
 * <p>
 * Addresses the following task requirements:
 * <p>
 * H. Write code to provide an alert if there is an appointment within 15 minutes of the user’s log-in.
 * <p>
 * G. Write two or more lambda expressions to make your program more efficient, justifying
 * the use of each lambda expression with an in-line comment.
 */
public class MainController extends Controller implements Initializable {

    private UserService userService;
    private AppointmentService appointmentService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        // initializing multiple service layer classes using lambda expressions
        getDatabaseConnection()
                .ifPresent(connection -> {
                    this.userService = UserService.getInstance(UserDAO.getInstance(connection));
                    this.appointmentService = AppointmentService.getInstance(AppointmentDAO.getInstance(connection));
                    getUpcomingAppointmentReminder();
                });
    }

    @FXML
    public void manageCustomers(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../view/customer/customer.fxml");
    }

    @FXML
    public void logout(ActionEvent actionEvent) {
        userService.logout();
        showView(actionEvent, getClass(), "../view/login.fxml");
    }

    private void getUpcomingAppointmentReminder() {
        userService.getCurrentUser()
                .map(UserDTO::getId)
                .ifPresent(this::showAppointmentReminder);
    }

    private void showAppointmentReminder(int userId) {
        // passing the appointment service reminder functionality as a supplier to the database action method
        // checking if the returned appointments are empty
        // if not we will display the reminder alert to the user
        performDatabaseAction(() -> appointmentService.getReminderAppointmentsByUser(userId))
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
