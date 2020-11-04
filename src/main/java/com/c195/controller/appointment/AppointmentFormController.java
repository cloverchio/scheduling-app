package com.c195.controller.appointment;

import com.c195.common.CheckedSupplier;
import com.c195.common.appointment.*;
import com.c195.common.customer.CustomerDTO;
import com.c195.common.customer.CustomerException;
import com.c195.controller.FormController;
import com.c195.dao.*;
import com.c195.service.AddressService;
import com.c195.service.AppointmentService;
import com.c195.service.CustomerService;
import com.c195.util.InputForm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AppointmentFormController extends FormController<TextInputControl> {

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
    private ComboBox<String> locationComboBox;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private Label startTimeLabel;
    @FXML
    private TextField startTimeField;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label endTimeLabel;
    @FXML
    private TextField endTimeField;

    private AppointmentService appointmentService;
    private CustomerService customerService;
    private InputForm<TextInputControl> inputForm;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.typeComboBox.setItems(getAppointmentTypes());
        this.locationComboBox.setItems(getAppointmentLocations());
        this.inputForm = createInputForm();
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

    protected AppointmentService getAppointmentService() {
        return appointmentService;
    }

    protected  <T> Optional<T> overlapConfirmationHandler(List<String> overlappingAppointments,
                                                       CheckedSupplier<T> confirmationSupplier) {
        if (overlappingAppointments.isEmpty()) {
            return formSubmitHandler(inputForm, confirmationSupplier);
        } else {
            final Alert overlapAlert = overlapAlert(String.join("\n", overlappingAppointments));
            return formSubmitConfirmationHandler(inputForm, overlapAlert, confirmationSupplier);
        }
    }

    protected List<AppointmentDTO> getOverlappingAppointments(int userId, Instant start, Instant end) {
        try {
            return serviceRequestHandler(() -> appointmentService.getAppointmentsByUserBetween(userId, start, end))
                    .orElse(Collections.emptyList());
        } catch (AppointmentException e) {
            setRedOutput(e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    protected Optional<AppointmentDTO.Builder> getAppointmentDTOBuilder() {
        final Optional<CustomerDTO> customer = getCustomerDTO();
        final Optional<AppointmentTime> time = getAppointmentTime();
        if (customer.isPresent() && time.isPresent()) {
            return Optional.of(new AppointmentDTO.Builder()
                    .withTitle(titleField.getText())
                    .withDescription(descriptionArea.getText())
                    .withUrl(urlField.getText())
                    .withContact(contactField.getText())
                    .withLocation(AppointmentLocation.fromName(locationComboBox.getValue()))
                    .withType(AppointmentType.fromName(typeComboBox.getValue()))
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
        startTimeField.setText(locationStart.toLocalTime().toString());
    }

    private void setEndDateTime(ZonedDateTime locationEnd) {
        endDatePicker.setValue(locationEnd.toLocalDate());
        endTimeField.setText(locationEnd.toLocalTime().toString());
    }

    private Optional<AppointmentTime> getAppointmentTime() {
        try {
            return Optional.of(createAppointmentTime());
        } catch (AppointmentException e) {
            setRedOutput(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private AppointmentTime createAppointmentTime() throws AppointmentException {
        return new AppointmentTime(
                startDatePicker.getValue(),
                startTimeField.getText(),
                endDatePicker.getValue(),
                endTimeField.getText(),
                AppointmentLocation.fromName(locationComboBox.getValue()).getZoneId());
    }

    private Optional<CustomerDTO> getCustomerDTO() {
        try {
            final int customerId = Integer.parseInt(customerIdField.getText());
            return serviceRequestHandler(() -> customerService.getCustomerById(customerId));
        } catch (CustomerException | NumberFormatException e) {
            setRedOutput(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private InputForm<TextInputControl> createInputForm() {
        final Map<String, TextInputControl> fields = new HashMap<String, TextInputControl>() {
            {
                put(titleLabel.getText(), titleField);
                put(descriptionLabel.getText(), descriptionArea);
                put(urlLabel.getText(), urlField);
                put(customerIdLabel.getText(), customerIdField);
                put(contactLabel.getText(), contactField);
            }
        };
        return new InputForm<>(fields);
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

    private static Alert overlapAlert(String appointmentTitles) {
        return infoAlert(
                "Overlapping Appointments",
                "You have a other appointments in this timeframe...",
                "You have the following appointments scheduled within the provided timeframe\n" + appointmentTitles);
    }
}
