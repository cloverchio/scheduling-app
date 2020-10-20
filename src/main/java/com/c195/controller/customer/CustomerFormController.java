package com.c195.controller.customer;

import com.c195.common.AddressDTO;
import com.c195.common.CustomerDTO;
import com.c195.controller.FormController;
import com.c195.dao.AddressDAO;
import com.c195.dao.CityDAO;
import com.c195.dao.CountryDAO;
import com.c195.dao.CustomerDAO;
import com.c195.service.AddressService;
import com.c195.service.CustomerService;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CustomerFormController extends FormController {

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

    private CustomerService customerService;
    private Map<Label, TextInputControl> formFields;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.formFields = new HashMap<>();
        this.formFields.put(nameLabel, nameField);
        this.formFields.put(addressLabel, addressField);
        this.formFields.put(aptLabel, aptField);
        this.formFields.put(cityLabel, cityField);
        this.formFields.put(countryLabel, countryField);
        this.formFields.put(postalCodeLabel, postalCodeField);
        this.formFields.put(phoneLabel, phoneField);
        getDatabaseConnection()
                .ifPresent(connection -> {
                    final AddressDAO addressDAO = AddressDAO.getInstance(connection);
                    final CityDAO cityDAO = CityDAO.getInstance(connection);
                    final CountryDAO countryDAO = CountryDAO.getInstance(connection);
                    final AddressService addressService = AddressService.getInstance(addressDAO, cityDAO, countryDAO);
                    customerService = CustomerService.getInstance(CustomerDAO.getInstance(connection), addressService);
                });
    }

    protected Map<Label, TextInputControl> getFormFields() {
        return formFields;
    }

    protected CustomerService getCustomerService() {
        return customerService;
    }

    protected CustomerDTO.Builder getCustomerDTO(AddressDTO addressDTO) {
        return new CustomerDTO.Builder()
                .withName(nameField.getText())
                .withActive(active.isSelected())
                .withAddressDTO(addressDTO);
    }

    protected AddressDTO.Builder getAddressDTO() {
        return new AddressDTO.Builder()
                .withAddress(addressField.getText())
                .withAddress2(aptField.getText())
                .withCity(cityField.getText())
                .withCountry(countryField.getText())
                .withPostalCode(postalCodeField.getText())
                .withPhone(phoneField.getText());
    }

    protected void setFields(CustomerDTO customerDTO) {
        final AddressDTO addressDTO = customerDTO.getAddressDTO();
        nameField.setText(customerDTO.getName());
        addressField.setText(addressDTO.getAddress());
        aptField.setText(addressDTO.getAddress2());
        cityField.setText(addressDTO.getCity());
        countryField.setText(addressDTO.getCountry());
        postalCodeField.setText(addressDTO.getPostalCode());
        phoneField.setText(addressDTO.getPhone());
        active.setSelected(customerDTO.isActive());
    }
}
