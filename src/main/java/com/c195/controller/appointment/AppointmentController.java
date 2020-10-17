package com.c195.controller.appointment;

import com.c195.common.AppointmentDTO;
import com.c195.controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Appointment management functionality.
 * <p>
 * The Localization API & DateTime API requirements for this project are
 * unclear as which data is supposed to be used for doing the conversions.
 * It specifically mentions "user" time zones but the user has no geographical data
 * whatsoever and the existing data model doesn't support the collection of it.
 * The "customer" has string representations of city and country; however, my interpretation
 * of the project does not involve "customers" being the end-user. My assumption is
 * that the date/times entered here are supposed to represent the customer's location
 * and that they are supposed to be shifted to the user's time (system default).
 * <p>
 * Addresses the following task requirements:
 * <p>
 * C. Provide the ability to add, update, and delete appointments, capturing the type of appointment
 * and a link to the specific customer record in the database.
 * <p>
 * D. Provide the ability to view the calendar by month and by week.
 * <p>
 * E. Provide the ability to automatically adjust appointment times based on user time zones
 * and daylight saving time.
 */
public class AppointmentController extends Controller implements Initializable {

    @FXML
    private TableView<AppointmentDTO> appointmentTable;
    @FXML
    private TableColumn<AppointmentDTO, String> titleColumn;
    @FXML
    private TableColumn<AppointmentDTO, String> locationColumn;
    @FXML
    private TableColumn<AppointmentDTO, String> customerNameColumn;
    @FXML
    private TableColumn<AppointmentDTO, String> typeColumn;
    @FXML
    private TableColumn<AppointmentDTO, String> startColumn;
    @FXML
    private TableColumn<AppointmentDTO, String> endColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/main.fxml");
    }

    @FXML
    public void create(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/appointment/create.fxml");
    }
}
