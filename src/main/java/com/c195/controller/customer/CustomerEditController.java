package com.c195.controller.customer;

import com.c195.common.AddressDTO;
import com.c195.common.CustomerDTO;
import com.c195.common.UserDTO;
import com.c195.controller.Controller;
import com.c195.dao.*;
import com.c195.service.AddressService;
import com.c195.service.CustomerService;
import com.c195.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CustomerEditController extends Controller implements Initializable {

    @FXML
    private Label messageLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameField;
    @FXML
    private Label addressLabel;
    @FXML
    private TextField addressField;
    @FXML
    private Label aptLabel;
    @FXML
    private TextField aptField;
    @FXML
    private Label cityLabel;
    @FXML
    private TextField cityField;
    @FXML
    private Label countryLabel;
    @FXML
    private TextField countryField;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private TextField postalCodeField;
    @FXML
    private Label phoneLabel;
    @FXML
    private TextField phoneField;
    @FXML
    private CheckBox active;

    private Map<Label, TextField> formFields;
    private UserService userService;
    private CustomerService customerService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.formFields = new HashMap<>();
        formFields.put(nameLabel, nameField);
        formFields.put(addressLabel, addressField);
        formFields.put(aptLabel, aptField);
        formFields.put(cityLabel, cityField);
        formFields.put(countryLabel, countryField);
        formFields.put(postalCodeLabel, postalCodeField);
        formFields.put(phoneLabel, phoneField);
        getDatabaseConnection()
                .ifPresent(connection -> {
                    final AddressDAO addressDAO = AddressDAO.getInstance(connection);
                    final CityDAO cityDAO = CityDAO.getInstance(connection);
                    final CountryDAO countryDAO = CountryDAO.getInstance(connection);
                    final AddressService addressService = AddressService.getInstance(addressDAO, cityDAO, countryDAO);
                    this.userService = UserService.getInstance(UserDAO.getInstance(connection));
                    this.customerService = CustomerService.getInstance(CustomerDAO.getInstance(connection), addressService);
                });
    }

    public void saveCustomer() {
        userService.getCurrentUser()
                .map(UserDTO::getUsername)
                .ifPresent(this::saveCustomer);
    }

    public void updateCustomer(int customerId, int addressId) {
        userService.getCurrentUser()
                .map(UserDTO::getUsername)
                .ifPresent(username -> updateCustomer(customerId, addressId, username));
    }

    private void saveCustomer(String currentUser) {
        formFieldSubmitAction(messageLabel, formFields, () -> {
            final AddressDTO addressDTO = getAddressDTO().build();
            final CustomerDTO customerDTO = getCustomerDTO(addressDTO).build();
            customerService.saveCustomer(customerDTO, currentUser);
            return null;
        });
    }

    private void updateCustomer(int customerId, int addressId, String currentUser) {
        formFieldSubmitAction(messageLabel, formFields, () -> {
            final AddressDTO addressDTO = getAddressDTO()
                    .withId(addressId)
                    .build();
            final CustomerDTO customerDTO = getCustomerDTO(addressDTO)
                    .withId(customerId)
                    .build();
            customerService.updateCustomer(customerDTO, currentUser);
            return null;
        });
    }

    private CustomerDTO.Builder getCustomerDTO(AddressDTO addressDTO) {
        return new CustomerDTO.Builder()
                .withName(nameField.getText())
                .withActive(active.isSelected())
                .withAddressDTO(addressDTO);
    }

    private AddressDTO.Builder getAddressDTO() {
        return new AddressDTO.Builder()
                .withAddress(addressField.getText())
                .withAddress2(aptField.getText())
                .withCity(cityField.getText())
                .withCountry(countryField.getText())
                .withPostalCode(postalCodeField.getText())
                .withPhone(phoneField.getText());
    }
}
