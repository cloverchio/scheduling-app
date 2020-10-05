package com.c195.controller.customer;

import com.c195.controller.Controller;
import com.c195.dao.CustomerDAO;
import com.c195.service.CustomerService;
import com.c195.service.MessagingService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerCreateController implements Initializable {

    @FXML
    private Label messageLabel;
    @FXML
    private TextField name;
    @FXML
    private TextField address;
    @FXML
    private TextField apt;
    @FXML
    private TextField city;
    @FXML
    private TextField country;
    @FXML
    private TextField postalCode;
    @FXML
    private TextField phone;
    @FXML
    private CheckBox active;

    private MessagingService messagingService;
    private CustomerService customerService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.messagingService = MessagingService.getInstance();
        Controller.getDatabaseConnection(messagingService)
                .ifPresent(connection -> customerService = CustomerService.getInstance(CustomerDAO.getInstance(connection)));
    }

    @FXML
    public void addCustomer(ActionEvent actionEvent) {

    }
}
