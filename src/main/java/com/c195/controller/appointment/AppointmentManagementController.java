package com.c195.controller.appointment;

import com.c195.controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AppointmentManagementController extends Controller implements Initializable {

    @FXML
    private Label messageLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField titleField;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TextField descriptionField;
    @FXML
    private Label contactLabel;
    @FXML
    private TextField contactField;
    @FXML
    private Label typeLabel;
    @FXML
    private TextField typeField;
    @FXML
    private Label urlLabel;
    @FXML
    private TextField urlField;
    @FXML
    private Label startDateLabel;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private Label endDateLabel;
    @FXML
    private DatePicker endDatePicker;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }
}
