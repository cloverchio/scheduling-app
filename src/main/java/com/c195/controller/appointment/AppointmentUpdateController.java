package com.c195.controller.appointment;

import com.c195.common.CheckedSupplier;
import com.c195.common.UserDTO;
import com.c195.common.appointment.AppointmentDTO;
import com.c195.common.appointment.AppointmentTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppointmentUpdateController extends AppointmentFormController {

    private Integer appointmentId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @FXML
    public void cancelUpdate(ActionEvent actionEvent) {
        eventViewHandler(actionEvent, getClass(), "../../view/appointment/appointment.fxml");
    }

    @FXML
    public void updateAppointment() {
        // gets the current user
        // maps that username to an updated appointment id (implicitly updates the appointment)
        // displays messaging to the user if an appointment id was returned
        getUserService().getCurrentUser()
                .map(this::updateAppointment)
                .ifPresent(updatedAppointmentId -> setDefaultOutput("Appointment has been updated!"));
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    private Integer updateAppointment(UserDTO userDTO) {
        final Optional<AppointmentDTO.Builder> appointmentDTOBuilder = getAppointmentDTOBuilder();
        if (appointmentDTOBuilder.isPresent()) {
            final AppointmentDTO appointmentDTO = appointmentDTOBuilder.get()
                    .withId(appointmentId)
                    .build();
            final CheckedSupplier<Integer> formSupplier = () -> getAppointmentService().updateAppointment(appointmentDTO, userDTO);
            return overlapConfirmationHandler(appointmentId, userDTO.getId(), appointmentDTO.getTime(), formSupplier)
                    .orElse(null);
        }
        return null;
    }

    private <T> Optional<T> overlapConfirmationHandler(int appointmentId,
                                                       int userId,
                                                       AppointmentTime appointmentTime,
                                                       CheckedSupplier<T> confirmationSupplier) {
        final List<String> overlappingAppointments =
                getOverlappingAppointments(appointmentId, userId, appointmentTime.getUtcStart(), appointmentTime.getUtcEnd())
                        .stream()
                        .map(AppointmentDTO::getTitle)
                        .collect(Collectors.toList());
        return overlapConfirmationHandler(overlappingAppointments, confirmationSupplier);
    }

    private List<AppointmentDTO> getOverlappingAppointments(int appointmentId, int userId, Instant start, Instant end) {
        return getOverlappingAppointments(userId, start, end)
                .stream()
                .filter(appointment -> appointment.getId() != appointmentId)
                .collect(Collectors.toList());
    }
}
