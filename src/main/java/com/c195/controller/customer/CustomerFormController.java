package com.c195.controller.customer;

import com.c195.common.customer.AddressDTO;
import com.c195.common.customer.CustomerDTO;
import com.c195.controller.FormController;
import com.c195.util.form.InputForm;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CustomerFormController extends FormController<TextField> {

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

    private InputForm<TextField> inputForm;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        inputForm = createInputForm();
    }

    protected InputForm<TextField> getInputForm() {
        return inputForm;
    }

    protected CustomerDTO.Builder getCustomerDTOBuilder(AddressDTO addressDTO) {
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

    private InputForm<TextField> createInputForm() {
        final Map<String, TextField> fields = new HashMap<String, TextField>() {
            {
                put(nameLabel.getText(), nameField);
                put(addressLabel.getText(), addressField);
                put(aptLabel.getText(), aptField);
                put(cityLabel.getText(), cityField);
                put(countryLabel.getText(), countryField);
                put(postalCodeLabel.getText(), postalCodeField);
                put(phoneLabel.getText(), phoneField);
            }
        };
        return new InputForm<>(fields);
    }
}
