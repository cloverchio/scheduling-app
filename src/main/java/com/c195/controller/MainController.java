package com.c195.controller;

import com.c195.common.UserDTO;
import com.c195.common.appointment.AppointmentDTO;
import com.c195.dao.AppointmentDAO;
import com.c195.dao.UserDAO;
import com.c195.service.AppointmentService;
import com.c195.service.UserService;
import com.c195.util.ControllerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
                    userService = UserService.getInstance(UserDAO.getInstance(connection));
                    appointmentService = AppointmentService.getInstance(AppointmentDAO.getInstance(connection));
                    getUpcomingAppointmentReminder();
                });
    }

    @FXML
    public void manageCustomers(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../view/customer/customer.fxml");
    }

    @FXML
    public void manageAppointments(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../view/appointment/appointment.fxml");
    }

    @FXML
    public void manageReports(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../view/report/report.fxml");
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
        // if not we will map appointments to their corresponding title
        // and prompt an alert to the user
        performDatabaseAction(() -> appointmentService.getReminderAppointmentsByUser(userId))
                .filter(appointmentDTOS -> !appointmentDTOS.isEmpty())
                .map(appointmentDTOS -> appointmentDTOS
                        .stream()
                        .map(AppointmentDTO::getTitle)
                        .collect(Collectors.joining("\n")))
                .ifPresent(MainController::showAppointmentReminderAlert);
    }

    private static void showAppointmentReminderAlert(String appointmentTitles) {
        ControllerUtils.infoAlert(
                "Appointment reminder",
                "You have an upcoming appointment",
                "You have the following appointments scheduled:\n" + appointmentTitles)
                .showAndWait();
    }
}
