package com.c195.controller.appointment;

import com.c195.common.AppointmentDTO;
import com.c195.common.CheckedSupplier;
import com.c195.common.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.time.Instant;
import java.util.Optional;
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
        getUserService().getCurrentUser()
                .map(this::saveAppointment)
                .ifPresent(savedAppointmentId -> setValidationField("Appointment has been saved!"));
    }

    private Integer saveAppointment(UserDTO userDTO) {
        final Optional<AppointmentDTO.Builder> appointmentDTOBuilder = getAppointmentDTO();
        if (appointmentDTOBuilder.isPresent()) {
            final AppointmentDTO appointmentDTO = appointmentDTOBuilder.get().build();
            final Instant start = appointmentDTO.getTime().getUtcStart();
            final Instant end = appointmentDTO.getTime().getUtcEnd();
            final CheckedSupplier<Integer> submitAction  = () -> getAppointmentService().saveAppointment(appointmentDTO, userDTO);
            return submitWithOverlapConfirmation(userDTO.getId(), start, end, submitAction).orElse(null);
        }
        return null;
    }
}
