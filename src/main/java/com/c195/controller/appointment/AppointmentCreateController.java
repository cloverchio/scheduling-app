package com.c195.controller.appointment;

import com.c195.common.AppointmentDTO;
import com.c195.common.CheckedSupplier;
import com.c195.common.UserDTO;
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

    @FXML
    public void saveAppointment() {
        // gets the current user
        // maps that user to a saved appointment id (implicitly saves the appointment)
        // displays messaging to the user if an appointment id was returned
        getUserService().getCurrentUser()
                .map(this::saveAppointment)
                .ifPresent(savedAppointmentId -> setValidationField("Appointment has been saved!"));
    }

    private Integer saveAppointment(UserDTO userDTO) {
        return getAppointmentDTOBuilder().map(appointmentDTOBuilder -> {
            final AppointmentDTO appointmentDTO = appointmentDTOBuilder.build();
            final CheckedSupplier<Integer> submitAction = () -> getAppointmentService().saveAppointment(appointmentDTO, userDTO);
            return submitWithOverlapConfirmation(userDTO.getId(), appointmentDTO.getTime(), submitAction).orElse(null);
        }).orElse(null);
    }
}
