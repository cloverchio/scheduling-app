package com.c195.controller.customer;

import com.c195.common.customer.AddressDTO;
import com.c195.common.CheckedSupplier;
import com.c195.common.customer.CustomerDTO;
import com.c195.common.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerCreateController extends CustomerFormController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @FXML
    public void cancelCreate(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/customer/customer.fxml");
    }

    @FXML
    public void saveCustomer() {
        // gets the current user
        // maps that user to a saved customer id (implicitly saves the customer)
        // displays messaging to the user if a customer id was returned
        getUserService().getCurrentUser()
                .map(this::saveCustomer)
                .ifPresent(savedCustomerId -> setValidationField("Customer has been saved!"));
    }

    private Integer saveCustomer(UserDTO currentUser) {
        final AddressDTO addressDTO = getAddressDTO().build();
        final CustomerDTO customerDTO = getCustomerDTOBuilder(addressDTO).build();
        final CheckedSupplier<Integer> submitAction = () -> getCustomerService().saveCustomer(customerDTO, currentUser);
        return formFieldSubmitAction(getFormFields(), submitAction).orElse(null);
    }
}
