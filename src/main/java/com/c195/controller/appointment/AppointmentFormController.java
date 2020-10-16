package com.c195.controller.appointment;

import com.c195.common.AppointmentDTO;
import com.c195.common.AppointmentType;
import com.c195.controller.FormController;
import com.c195.dao.AppointmentDAO;
import com.c195.service.AppointmentService;
import com.c195.util.AppointmentUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

public class AppointmentFormController extends FormController {

    private static final String TIME_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    @FXML
    private Label messagingField;
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
        this.formFields = createFormFieldMapping();
        getDatabaseConnection().ifPresent(this::initializeServices);
    }

    @Override
    public void initializeServices(Connection connection) {
        super.initializeServices(connection);
        appointmentService = AppointmentService.getInstance(AppointmentDAO.getInstance(connection));
    }

    protected Map<Label, TextField> getFormFields() {
        return formFields;
    }

    protected AppointmentService getAppointmentService() {
        return appointmentService;
    }

    protected Optional<LocalDateTime> getAppointmentDateTime(LocalDate date, String time) {
        if (!pattern.matcher(time).matches()) {
            messagingField.setText("An invalid time was provided");
            return Optional.empty();
        }
        return Optional.of(AppointmentUtils.toLocalDateTime(date, time));
    }

    protected void setTextFields(AppointmentDTO appointmentDTO) {
        titleField.setText(appointmentDTO.getTitle());
        descriptionField.setText(appointmentDTO.getDescription());
        contactField.setText(appointmentDTO.getContact());
        urlField.setText(appointmentDTO.getUrl());
        locationField.setText(appointmentDTO.getLocation());
    }

    private Map<Label, TextField> createFormFieldMapping() {
        final Map<Label, TextField> formFields = new HashMap<>();
        formFields.put(titleLabel, titleField);
        formFields.put(descriptionLabel, descriptionField);
        formFields.put(contactLabel, contactField);
        formFields.put(urlLabel, urlField);
        formFields.put(locationLabel, locationField);
        formFields.put(startTimeLabel, startTimeField);
        formFields.put(endTimeLabel, endTimeField);
        return formFields;
    }
}
