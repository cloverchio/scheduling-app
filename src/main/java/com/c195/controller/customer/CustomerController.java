package com.c195.controller.customer;

import com.c195.controller.Controller;
import com.c195.service.MessagingService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

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
public class CustomerController implements Initializable {

    private MessagingService messagingService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.messagingService = MessagingService.getInstance();
    }

    @FXML
    public void create(ActionEvent actionEvent) {
        Controller.showView(actionEvent, getClass(), "../../view/customer/create.fxml", messagingService);
    }

    @FXML
    public void cancel(ActionEvent actionEvent)  {
        Controller.showView(actionEvent, getClass(), "../../view/main.fxml", messagingService);
    }
}
