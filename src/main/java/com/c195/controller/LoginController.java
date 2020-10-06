package com.c195.controller;

import com.c195.dao.UserDAO;
import com.c195.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Login functionality.
 * <p>
 * Addresses the following task requirements:
 * <p>
 * A. Create a log-in form that can determine the user’s location and translate log-in and error control messages
 * (e.g., “The username and password did not match.”) into two languages.
 * <p>
 * G. Write two or more lambda expressions to make your program more efficient, justifying
 * the use of each lambda expression with an in-line comment.
 */
public class LoginController extends Controller implements Initializable {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private Map<Label, TextField> formFields;
    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.messageLabel.setText(getMessagingService().getNewLogin());
        this.usernameLabel.setText(getMessagingService().getUsername());
        this.passwordLabel.setText(getMessagingService().getPassword());
        this.loginButton.setText(getMessagingService().getLogin());
        this.formFields = new HashMap<>();
        formFields.put(usernameLabel, usernameField);
        formFields.put(passwordLabel, passwordField);
        // leveraging a lambda expression to initialize the user service if the connection available
        getDatabaseConnection()
                .ifPresent(connection -> this.userService = UserService.getInstance(UserDAO.getInstance(connection)));
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        final Map<Label, TextField> invalidFields = getInvalidTextFields(formFields);
        if (!invalidFields.isEmpty()) {
            showRequiredFieldMessage(messageLabel, invalidFields.keySet());
        } else {
            // passing the user service login functionality as a supplier to the database action method
            // which in this case will just perform some common database exception handling for controllers
            performDatabaseAction(() -> userService.login(usernameField.getText(), passwordField.getText()))
                    .ifPresent(validLogin -> handleLoginStatus(actionEvent, validLogin));
        }
    }

    private void handleLoginStatus(ActionEvent actionEvent, boolean validLogin) {
        if (validLogin) {
            showView(actionEvent, getClass(), "../view/main.fxml");
        } else {
            this.messageLabel.setText(getMessagingService().getInvalidLogin());
            displayAsRed(messageLabel);
        }
    }
}
