package com.c195.controller.customer;

import com.c195.common.AddressDTO;
import com.c195.common.CustomerDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Customer management functionality.
 * <p>
 * Addresses the following task requirements:
 * <p>
 * B. Provide the ability to add, update, and delete customer records in the database,
 * including name, address, and phone number.
 */
public class CustomerController extends CustomerManagementController implements Initializable {

    @FXML
    private TableView<CustomerDTO> customerTable;
    @FXML
    private TableColumn<CustomerDTO, String> customerNameColumn;
    @FXML
    private TableColumn<CustomerDTO, String> addressColumn;
    @FXML
    private TableColumn<CustomerDTO, String> phoneColumn;
    @FXML
    private TableColumn<CustomerDTO, String> statusColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        createCustomerTable();
    }

    @FXML
    public void create(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/customer/create.fxml");
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/main.fxml");
    }

    private void createCustomerTable() {
        customerTable.setItems(getAllCustomers());
        customerNameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        addressColumn.setCellValueFactory(c -> new SimpleStringProperty(toAddressLine(c.getValue().getAddressDTO())));
        phoneColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddressDTO().getPhone()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(toActiveLine(c.getValue().isActive())));
    }

    private static String toAddressLine(AddressDTO addressDTO ) {
        return String.format("%s %s %s %s %s",
                addressDTO.getAddress(),
                addressDTO.getAddress2(),
                addressDTO.getCity(),
                addressDTO.getCountry(),
                addressDTO.getPostalCode());
    }

    private static String toActiveLine(boolean activeStatus) {
        return activeStatus ? "Active" : "Inactive";
    }
}
