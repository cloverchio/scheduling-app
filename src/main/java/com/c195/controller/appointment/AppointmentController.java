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
