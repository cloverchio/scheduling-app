package com.c195.controller.appointment;

import com.c195.common.AppointmentDTO;
import com.c195.common.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.time.Instant;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
        getUserService().getCurrentUser().ifPresent(submitAppointment());
    }

    private Consumer<UserDTO> submitAppointment() {
        return user -> getAppointmentDTO()
                .map(AppointmentDTO.Builder::build)
                .ifPresent(appointmentDTO -> {
                    final Instant start = appointmentDTO.getTime().getUtcStart();
                    final Instant end = appointmentDTO.getTime().getUtcEnd();
                    submitWithOverlapConfirmation(user.getId(), start, end, () ->
                            getAppointmentService().saveAppointment(appointmentDTO, user.getUsername()));
                });
    }
}
