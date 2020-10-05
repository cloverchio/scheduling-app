package com.c195.controller.customer;

import com.c195.controller.Controller;
import com.c195.dao.CustomerDAO;
import com.c195.service.CustomerService;
import com.c195.service.MessagingService;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerCreateController implements Initializable {

    private MessagingService messagingService;
    private CustomerService customerService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.messagingService = MessagingService.getInstance();
        Controller.getDatabaseConnection(messagingService)
                .ifPresent(connection -> customerService = CustomerService.getInstance(CustomerDAO.getInstance(connection)));
    }
}
