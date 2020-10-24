package com.c195.controller;

import com.c195.util.ControllerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
public class LoginController extends FormController {

    @FXML
    private Label usernameLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private Map<Label, TextInputControl> formFields;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.usernameLabel.setText(getMessagingService().getUsername());
        this.passwordLabel.setText(getMessagingService().getPassword());
        this.loginButton.setText(getMessagingService().getLogin());
        this.formFields = new HashMap<>();
        this.formFields.put(usernameLabel, usernameField);
        this.formFields.put(passwordLabel, passwordField);
        setValidationField(getMessagingService().getNewLogin());
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        // passing the user service login functionality as a supplier to the form action method
        // which in this case will perform some validation and exception handling under the hood
        formFieldSubmitAction(formFields, () -> getUserService().login(usernameField.getText(), passwordField.getText()))
                .ifPresent(validLogin -> handleLoginStatus(actionEvent, validLogin));
    }

    private void handleLoginStatus(ActionEvent actionEvent, boolean validLogin) {
        if (validLogin) {
            showView(actionEvent, getClass(), "../view/main.fxml");
        } else {
            ControllerUtils.displayAsRed(getValidationField());
            setValidationField(getMessagingService().getInvalidLogin());
        }
    }
}
