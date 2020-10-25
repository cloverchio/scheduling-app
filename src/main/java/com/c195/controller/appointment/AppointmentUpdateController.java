package com.c195.controller.appointment;

import com.c195.common.AppointmentDTO;
import com.c195.common.CheckedSupplier;
import com.c195.common.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class AppointmentUpdateController extends AppointmentFormController {

    private Integer appointmentId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @FXML
    public void cancelUpdate(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/appointment/appointment.fxml");
    }

    @FXML
    public void updateAppointment() {
        // gets the current user
        // maps that username to an updated appointment id (implicitly updates the appointment)
        // displays messaging to the user if an appointment id was returned
        getUserService().getCurrentUser()
                .map(this::updateAppointment)
                .ifPresent(updatedAppointmentId -> setValidationField("Appointment has been updated!"));
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    private Integer updateAppointment(UserDTO userDTO) {
        return getAppointmentDTOBuilder().map(appointmentDTOBuilder -> {
            final AppointmentDTO appointmentDTO = appointmentDTOBuilder.withId(appointmentId).build();
            final CheckedSupplier<Integer> submitAction = () -> getAppointmentService().updateAppointment(appointmentDTO, userDTO);
            return submitWithOverlapConfirmation(appointmentId, userDTO.getId(), appointmentDTO.getTime(), submitAction).orElse(null);
        }).orElse(null);
    }
}
