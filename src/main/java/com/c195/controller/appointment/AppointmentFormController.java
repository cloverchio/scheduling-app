package com.c195.controller.appointment;

import com.c195.common.*;
import com.c195.controller.FormController;
import com.c195.dao.AppointmentDAO;
import com.c195.service.AppointmentException;
import com.c195.service.AppointmentService;
import com.c195.util.ControllerUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AppointmentFormController extends FormController {

    @FXML
    private Label titleLabel;
    @FXML
    private TextField titleField;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label customerIdLabel;
    @FXML
    private TextField customerIdField;
    @FXML
    private Label contactLabel;
    @FXML
    private TextField contactField;
    @FXML
    private Label urlLabel;
    @FXML
    private TextField urlField;
    @FXML
    private Label locationLabel;
    @FXML
    private ComboBox<String> locationComboBox;
    @FXML
    private Label typeLabel;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private Label startDateLabel;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private Label startTimeLabel;
    @FXML
    private TextField startTimeField;
    @FXML
    private Label endDateLabel;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label endTimeLabel;
    @FXML
    private TextField endTimeField;

    private AppointmentService appointmentService;
    private Map<Label, TextInputControl> formFields;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.typeComboBox.setItems(getAppointmentTypes());
        this.locationComboBox.setItems(getAppointmentLocations());
        this.formFields = new HashMap<>();
        this.formFields.put(titleLabel, titleField);
        this.formFields.put(descriptionLabel, descriptionArea);
        this.formFields.put(customerIdLabel, customerIdField);
        this.formFields.put(contactLabel, contactField);
        this.formFields.put(urlLabel, urlField);
        this.formFields.put(startTimeLabel, startTimeField);
        this.formFields.put(endTimeLabel, endTimeField);
        getDatabaseConnection()
                .ifPresent(connection -> appointmentService = AppointmentService.getInstance(AppointmentDAO.getInstance(connection)));
    }

    protected Map<Label, TextInputControl> getFormFields() {
        return formFields;
    }

    protected AppointmentService getAppointmentService() {
        return appointmentService;
    }

    protected <T> void  submitWithOverlapConfirmation(int userId, Instant start, Instant end, CheckedSupplier<T> submitAction) {
        performDatabaseAction(() -> appointmentService.getAppointmentsByUserBetween(userId, start, end))
                .filter(overlappingAppointments -> !overlappingAppointments.isEmpty())
                .ifPresent(overlappingAppointments -> formFieldSubmitActionWithConfirmation(formFields, submitAction));
    }

    protected Optional<AppointmentDTO.Builder> getAppointmentDTO() {
        return getAppointmentTime()
                .map(time -> new AppointmentDTO.Builder()
                        .withTitle(titleField.getText())
                        .withDescription(descriptionArea.getText())
                        .withUrl(urlField.getText())
                        .withContact(contactField.getText())
                        .withLocation(AppointmentLocation.valueOf(locationComboBox.getValue()))
                        .withType(AppointmentType.valueOf(typeComboBox.getValue()))
                        .withTime(time));
    }

    protected void setFields(AppointmentDTO appointmentDTO) {
        final AppointmentType type = appointmentDTO.getType();
        final AppointmentLocation location = appointmentDTO.getLocation();
        final AppointmentTime time = appointmentDTO.getTime();
        final CustomerDTO customer = appointmentDTO.getCustomerDTO();
        titleField.setText(appointmentDTO.getTitle());
        descriptionArea.setText(appointmentDTO.getDescription());
        contactField.setText(appointmentDTO.getContact());
        urlField.setText(appointmentDTO.getUrl());
        typeComboBox.getSelectionModel().select(type.getName());
        locationComboBox.getSelectionModel().select(location.getName());
        customerIdField.setText(String.valueOf(customer.getId()));
        setStartDateTime(time.getLocationStart());
        setEndDateTime(time.getLocationEnd());
    }

    private Optional<AppointmentTime> getAppointmentTime() {
        try {
            return Optional.of(new AppointmentTime(
                    startDatePicker.getValue(),
                    startTimeField.getText(),
                    endDatePicker.getValue(),
                    endTimeField.getText(),
                    AppointmentLocation.valueOf(locationComboBox.getValue()).getZoneId()));
        } catch (AppointmentException e) {
            ControllerUtils.displayAsRed(getValidationField());
            setValidationField(e.getMessage());
        }
        return Optional.empty();
    }

    private void setStartDateTime(ZonedDateTime locationStart) {
        startDatePicker.setValue(locationStart.toLocalDate());
        startTimeField.setText(getTimeField(locationStart.getHour(), locationStart.getMinute()));
    }

    private void setEndDateTime(ZonedDateTime locationEnd) {
        endDatePicker.setValue(locationEnd.toLocalDate());
        endTimeField.setText(getTimeField(locationEnd.getHour(), locationEnd.getMinute()));
    }

    private static ObservableList<String> getAppointmentTypes() {
        return FXCollections.observableList(
                Arrays.stream(AppointmentType.values())
                        .map(AppointmentType::getName)
                        .collect(Collectors.toList()));
    }

    private static ObservableList<String> getAppointmentLocations() {
        return FXCollections.observableList(
                Arrays.stream(AppointmentLocation.values())
                        .map(AppointmentLocation::getName)
                        .collect(Collectors.toList()));
    }

    private static String getTimeField(int hour, int minute) {
        return String.format("%d:%d", hour, minute);
    }
}
