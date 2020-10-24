package com.c195.controller.customer;

import com.c195.common.AddressDTO;
import com.c195.common.CheckedSupplier;
import com.c195.common.CustomerDTO;
import com.c195.common.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerUpdateController extends CustomerFormController {

    private CustomerDTO customerDTO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @FXML
    public void cancelUpdate(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/customer/customer.fxml");
    }

    @FXML
    public void updateCustomer() {
        // gets the current user
        // maps that username to an updated customer id (implicitly updates the customer)
        // displays messaging to the user if a customer id was returned
        getUserService().getCurrentUser()
                .map(currentUser -> updateCustomer(customerDTO.getId(), customerDTO.getAddressDTO().getId(), currentUser))
                .ifPresent(updatedCustomerId -> setValidationField("Customer has been updated!"));
    }

    public void setCustomerDTO(CustomerDTO customerDTO) {
        this.customerDTO = customerDTO;
    }

    private Integer updateCustomer(int customerId, int addressId, UserDTO currentUser) {
        final AddressDTO addressDTO = getAddressDTO().withId(addressId).build();
        final CustomerDTO customerDTO = getCustomerDTO(addressDTO).withId(customerId).build();
        final CheckedSupplier<Integer> submitAction = () -> getCustomerService().updateCustomer(customerDTO, currentUser);
        return formFieldSubmitAction(getFormFields(), submitAction).orElse(null);
    }
}
