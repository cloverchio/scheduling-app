package com.c195.controller;

import com.c195.common.UserDTO;
import com.c195.common.appointment.AppointmentDTO;
import com.c195.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;

import java.net.URL;
import java.util.List;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.userService = serviceResolver().getUserService();
        getUpcomingAppointmentReminder();
    }

    @FXML
    public void manageCustomers(ActionEvent actionEvent) {
        eventViewHandler(actionEvent, getClass(), "../view/customer/customer.fxml");
    }

    @FXML
    public void manageAppointments(ActionEvent actionEvent) {
        eventViewHandler(actionEvent, getClass(), "../view/appointment/appointment.fxml");
    }

    @FXML
    public void manageReports(ActionEvent actionEvent) {
        eventViewHandler(actionEvent, getClass(), "../view/report/report.fxml");
    }

    @FXML
    public void logout(ActionEvent actionEvent) {
        userService.logout();
        eventViewHandler(actionEvent, getClass(), "../view/login.fxml");
    }

    private void getUpcomingAppointmentReminder() {
        userService.getCurrentUser()
                .map(UserDTO::getId)
                .ifPresent(this::showAppointmentReminder);
    }

    private void showAppointmentReminder(int userId) {
        // passing the appointment service reminder functionality as a supplier to the handler
        // checking if the returned appointments are empty
        // if not we will map appointments to their corresponding title
        // and prompt an alert to the user
        serviceRequestHandler(() -> serviceResolver().getAppointmentService()
                .getReminderAppointmentsByUser(userId))
                .filter(appointmentDTOS -> !appointmentDTOS.isEmpty())
                .map(MainController::toAppointmentTitles)
                .map(MainController::appointmentReminderAlert)
                .ifPresent(Dialog::showAndWait);
    }

    private static String toAppointmentTitles(List<AppointmentDTO> appointments) {
        return appointments
                .stream()
                .map(AppointmentDTO::getTitle)
                .collect(Collectors.joining("\n"));
    }

    private static Alert appointmentReminderAlert(String appointmentTitles) {
        return infoAlert("Appointment reminder",
                "You have an upcoming appointment",
                "You have the following appointments scheduled:\n" + appointmentTitles);
    }
}
