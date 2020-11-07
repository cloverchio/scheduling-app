package com.c195.controller.customer;

import com.c195.common.CheckedSupplier;
import com.c195.common.customer.AddressDTO;
import com.c195.common.customer.CustomerDTO;
import com.c195.controller.Controller;
import com.c195.dao.AddressDAO;
import com.c195.dao.CityDAO;
import com.c195.dao.CountryDAO;
import com.c195.dao.CustomerDAO;
import com.c195.service.AddressService;
import com.c195.service.CustomerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Customer management functionality.
 * <p>
 * Addresses the following task requirements:
 * <p>
 * B. Provide the ability to add, update, and delete customer records in the database,
 * including name, address, and phone number.
 */
public class CustomerController extends Controller implements Initializable {

    @FXML
    private TableView<CustomerDTO> customerTable;
    @FXML
    private TableColumn<CustomerDTO, String> idColumn;
    @FXML
    private TableColumn<CustomerDTO, String> nameColumn;
    @FXML
    private TableColumn<CustomerDTO, String> addressColumn;
    @FXML
    private TableColumn<CustomerDTO, String> phoneColumn;
    @FXML
    private TableColumn<CustomerDTO, String> statusColumn;

    private CustomerService customerService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        getDatabaseConnection()
                .ifPresent(connection -> {
                    final AddressDAO addressDAO = AddressDAO.getInstance(connection);
                    final CityDAO cityDAO = CityDAO.getInstance(connection);
                    final CountryDAO countryDAO = CountryDAO.getInstance(connection);
                    final AddressService addressService = AddressService.getInstance(addressDAO, cityDAO, countryDAO, getClock());
                    customerService = CustomerService.getInstance(CustomerDAO.getInstance(connection), addressService, getClock());
                    createCustomerTable();
                });
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        eventViewHandler(actionEvent, getClass(), "../../view/main.fxml");
    }

    @FXML
    public void create(ActionEvent actionEvent) {
        eventViewHandler(actionEvent, getClass(), "../../view/customer/create.fxml");
    }

    @FXML
    public void update(ActionEvent actionEvent) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../view/customer/update.fxml"));
        final Parent parent = fxmlLoader.load();
        Optional.ofNullable(customerTable.getSelectionModel().getSelectedItem())
                .ifPresent(selectedCustomer -> {
                    final CustomerUpdateController customerUpdateController = fxmlLoader.getController();
                    customerUpdateController.setCustomerDTO(selectedCustomer);
                    customerUpdateController.setFields(selectedCustomer);
                    eventStageHandler(actionEvent, parent);
                });
    }

    @FXML
    public void delete() {
        Optional.ofNullable(customerTable.getSelectionModel().getSelectedItem())
                .map(CustomerDTO::getId)
                .map(this::customerDeleteSupplier)
                .ifPresent(this::confirmationHandler);
    }

    private void createCustomerTable() {
        customerTable.setItems(getAllCustomers());
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        addressColumn.setCellValueFactory(c -> new SimpleStringProperty(toAddressLine(c.getValue().getAddressDTO())));
        phoneColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddressDTO().getPhone()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(toActiveLine(c.getValue().isActive())));
    }

    private CheckedSupplier<Void> customerDeleteSupplier(int customerId) {
        return () -> {
            customerService.deleteCustomer(customerId);
            createCustomerTable();
            return null;
        };
    }

    private ObservableList<CustomerDTO> getAllCustomers() {
        return serviceRequestHandler(() -> customerService.getAllCustomers())
                .map(FXCollections::observableList)
                .orElse(FXCollections.emptyObservableList());
    }

    private static String toAddressLine(AddressDTO addressDTO) {
        return String.format("%s %s %s, %s, %s",
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
