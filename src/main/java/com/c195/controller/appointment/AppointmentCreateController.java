package com.c195.controller.appointment;

import com.c195.common.CheckedSupplier;
import com.c195.common.UserDTO;
import com.c195.common.appointment.AppointmentDTO;
import com.c195.common.appointment.AppointmentTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppointmentCreateController extends AppointmentFormController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @FXML
    public void cancelCreate(ActionEvent actionEvent) {
        eventViewHandler(actionEvent, getClass(), "../../view/appointment/appointment.fxml");
    }

    @FXML
    public void saveAppointment() {
        // gets the current user
        // maps that user to a saved appointment id (implicitly saves the appointment)
        // displays messaging to the user if an appointment id was returned
        getUserService().getCurrentUser()
                .map(this::saveAppointment)
                .ifPresent(savedAppointmentId -> setDefaultOutput("Appointment has been saved!"));
    }

    private Integer saveAppointment(UserDTO userDTO) {
        final Optional<AppointmentDTO.Builder> appointmentDTOBuilder = getAppointmentDTOBuilder();
        if (appointmentDTOBuilder.isPresent()) {
            final AppointmentDTO appointmentDTO = appointmentDTOBuilder.get().build();
            final CheckedSupplier<Integer> formSupplier = () -> getAppointmentService().saveAppointment(appointmentDTO, userDTO);
            return overlapConfirmationHandler(userDTO.getId(), appointmentDTO.getTime(), formSupplier)
                    .orElse(null);
        }
        return null;
    }

    private <T> Optional<T> overlapConfirmationHandler(int userId,
                                                       AppointmentTime appointmentTime,
                                                       CheckedSupplier<T> confirmationSupplier) {
        final List<String> overlappingAppointments =
                getOverlappingAppointments(userId, appointmentTime.getUtcStart(), appointmentTime.getUtcEnd())
                        .stream()
                        .map(AppointmentDTO::getTitle)
                        .collect(Collectors.toList());
        return overlapConfirmationHandler(overlappingAppointments, confirmationSupplier);
    }
}
