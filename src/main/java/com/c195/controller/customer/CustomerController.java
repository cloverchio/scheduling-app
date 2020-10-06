package com.c195.controller.customer;

import com.c195.controller.Controller;
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
public class CustomerController extends Controller implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    @FXML
    public void create(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/customer/create.fxml");
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/main.fxml");
    }
}
