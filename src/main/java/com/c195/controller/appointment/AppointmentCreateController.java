package com.c195.controller.appointment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class AppointmentCreateController extends AppointmentFormController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @FXML
    public void cancelCreate(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/appointment/appointment.fxml");
    }
}
