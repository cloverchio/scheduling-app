package com.c195.controller.appointment;

import com.c195.common.AppointmentDTO;
import com.c195.common.AppointmentType;
import com.c195.common.CheckedSupplier;
import com.c195.controller.FormController;
import com.c195.dao.AppointmentDAO;
import com.c195.service.AppointmentService;
import com.c195.util.AppointmentUtils;
import com.c195.util.ControllerUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

public class AppointmentFormController extends FormController {

    private static final String TIME_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    @FXML
    private Label titleLabel;
    @FXML
    private TextField titleField;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TextField descriptionField;
    @FXML
    private Label customerIdLabel;
    @FXML
    private TextField customerIdField;
    @FXML
    private Label contactLabel;
    @FXML
    private TextField contactField;
    @FXML
    private Label typeLabel;
    @FXML
    private ComboBox<AppointmentType> typeComboBox;
    @FXML
    private Label urlLabel;
    @FXML
    private TextField urlField;
    @FXML
    private Label locationLabel;
    @FXML
    private TextField locationField;
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
    private Map<Label, TextField> formFields;
    private Pattern pattern;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.pattern = Pattern.compile(TIME_PATTERN);
        this.typeComboBox.setItems(FXCollections.observableList(Arrays.asList(AppointmentType.values())));
        this.formFields = new HashMap<>();
        this.formFields.put(titleLabel, titleField);
        this.formFields.put(descriptionLabel, descriptionField);
        this.formFields.put(customerIdLabel, customerIdField);
        this.formFields.put(contactLabel, contactField);
        this.formFields.put(urlLabel, urlField);
        this.formFields.put(locationLabel, locationField);
        this.formFields.put(startTimeLabel, startTimeField);
        this.formFields.put(endTimeLabel, endTimeField);
        getDatabaseConnection()
                .ifPresent(connection -> appointmentService = AppointmentService.getInstance(AppointmentDAO.getInstance(connection)));
    }

    protected Map<Label, TextField> getFormFields() {
        return formFields;
    }

    protected AppointmentService getAppointmentService() {
        return appointmentService;
    }

    protected <T> Optional<T> formFieldSubmitAction(CheckedSupplier<T> formDatabaseAction) {
        final boolean validStart = pattern.matcher(startTimeField.getText()).matches();
        final boolean validEnd = pattern.matcher(endTimeField.getText()).matches();
        if (validStart && validEnd) {
            return formFieldSubmitAction(formFields, formDatabaseAction);
        }
        ControllerUtils.displayAsRed(getValidationField());
        setValidationField("An invalid time was provided");
        return Optional.empty();
    }

    protected AppointmentDTO.Builder getAppointmentDTO() {
        return new AppointmentDTO.Builder()
                .withTitle(titleField.getText())
                .withDescription(descriptionField.getText())
                .withUrl(urlField.getText())
                .withLocation(locationField.getText())
                .withContact(contactField.getText())
                .withType(typeComboBox.getValue())
                .withStart(AppointmentUtils.toUTCInstant(startDatePicker.getValue(), startTimeField.getText()))
                .withEnd(AppointmentUtils.toUTCInstant(endDatePicker.getValue(), endTimeField.getText()));
    }

    protected void setFields(AppointmentDTO appointmentDTO) {
        titleField.setText(appointmentDTO.getTitle());
        descriptionField.setText(appointmentDTO.getDescription());
        customerIdField.setText(String.valueOf(appointmentDTO.getCustomerDTO().getId()));
        contactField.setText(appointmentDTO.getContact());
        urlField.setText(appointmentDTO.getUrl());
        locationField.setText(appointmentDTO.getLocation());
        typeComboBox.getSelectionModel().select(appointmentDTO.getType());
        setStartDateTime(appointmentDTO.getStart());
        setEndDateTime(appointmentDTO.getEnd());
    }

    private void setStartDateTime(Instant start) {
        final LocalDateTime startLocalDateTime = AppointmentUtils.toLocalDateTime(start);
        startTimeField.setText(AppointmentUtils.getTime(startLocalDateTime.getHour(), startLocalDateTime.getMinute()));
        startDatePicker.setValue(startLocalDateTime.toLocalDate());
    }

    private void setEndDateTime(Instant end) {
        final LocalDateTime endLocalDateTime = AppointmentUtils.toLocalDateTime(end);
        endTimeField.setText(AppointmentUtils.getTime(endLocalDateTime.getHour(), endLocalDateTime.getMinute()));
        endDatePicker.setValue(endLocalDateTime.toLocalDate());
    }
}
