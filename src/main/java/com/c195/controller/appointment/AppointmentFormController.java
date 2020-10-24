package com.c195.controller.appointment;

import com.c195.common.*;
import com.c195.controller.FormController;
import com.c195.dao.*;
import com.c195.service.*;
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
    private CustomerService customerService;
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
                .ifPresent(connection -> {
                    final AddressDAO addressDAO = AddressDAO.getInstance(connection);
                    final CityDAO cityDAO = CityDAO.getInstance(connection);
                    final CountryDAO countryDAO = CountryDAO.getInstance(connection);
                    final AddressService addressService = AddressService.getInstance(addressDAO, cityDAO, countryDAO);
                    customerService = CustomerService.getInstance(CustomerDAO.getInstance(connection), addressService);
                    appointmentService = AppointmentService.getInstance(AppointmentDAO.getInstance(connection));
                });
    }

    protected Map<Label, TextInputControl> getFormFields() {
        return formFields;
    }

    protected AppointmentService getAppointmentService() {
        return appointmentService;
    }

    protected <T> Optional<T> submitWithOverlapConfirmation(int userId, Instant start, Instant end, CheckedSupplier<T> submitAction) {
        final List<String> overlappingAppointments = getOverlappingAppointments(userId, start, end);
        if (overlappingAppointments.isEmpty()) {
            return formFieldSubmitAction(formFields, submitAction);
        } else {
            final Alert overlapAlert = overlapAlert(String.join("\n", overlappingAppointments));
            return formFieldSubmitActionWithConfirmation(formFields, overlapAlert, submitAction);
        }
    }

    protected Optional<AppointmentDTO.Builder> getAppointmentDTO() {
        final Optional<CustomerDTO> customer = getCustomerDTO();
        final Optional<AppointmentTime> time = getAppointmentTime();
        if (customer.isPresent() && time.isPresent()) {
            return Optional.of(new AppointmentDTO.Builder()
                    .withTitle(titleField.getText())
                    .withDescription(descriptionArea.getText())
                    .withUrl(urlField.getText())
                    .withContact(contactField.getText())
                    .withLocation(AppointmentLocation.valueOf(locationComboBox.getValue().toUpperCase()))
                    .withType(AppointmentType.valueOf(typeComboBox.getValue().toUpperCase()))
                    .withTime(time.get())
                    .withCustomerDTO(customer.get()));
        }
        return Optional.empty();
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

    private void setStartDateTime(ZonedDateTime locationStart) {
        startDatePicker.setValue(locationStart.toLocalDate());
        startTimeField.setText(getTimeField(locationStart.getHour(), locationStart.getMinute()));
    }

    private void setEndDateTime(ZonedDateTime locationEnd) {
        endDatePicker.setValue(locationEnd.toLocalDate());
        endTimeField.setText(getTimeField(locationEnd.getHour(), locationEnd.getMinute()));
    }

    private Optional<AppointmentTime> getAppointmentTime() {
        try {
            return Optional.of(new AppointmentTime(startDatePicker.getValue(),
                    startTimeField.getText(),
                    endDatePicker.getValue(),
                    endTimeField.getText(),
                    AppointmentLocation.valueOf(locationComboBox.getValue().toUpperCase()).getZoneId()));
        } catch (AppointmentException e) {
            setValidationFieldError(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Optional<CustomerDTO> getCustomerDTO() {
        try {
            final int customerId = Integer.parseInt(customerIdField.getText());
            return performDatabaseAction(() -> customerService.getCustomerById(customerId));
        } catch (CustomerException | NumberFormatException e) {
            setValidationFieldError(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private List<String> getOverlappingAppointments(int userId, Instant start, Instant end) {
        try {
            return performDatabaseAction(() ->
                    appointmentService.getAppointmentsByUserBetween(userId, start, end))
                    .orElse(Collections.emptyList()).stream()
                    .map(AppointmentDTO::getTitle)
                    .collect(Collectors.toList());
        } catch (AppointmentException e) {
            setValidationFieldError(e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
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

    private static Alert overlapAlert(String appointmentTitles) {
        return ControllerUtils.infoAlert(
                "Overlapping Appointments",
                "You have a other appointments in this timeframe...",
                "You have the following appointments scheduled within the provided timeframe\n" + appointmentTitles);
    }
}
