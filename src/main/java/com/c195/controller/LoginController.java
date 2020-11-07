package com.c195.controller;

import com.c195.common.CheckedSupplier;
import com.c195.common.UserDTO;
import com.c195.service.MessagingService;
import com.c195.service.ServiceResolver;
import com.c195.service.UserService;
import com.c195.util.form.InputForm;
import com.c195.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
 * <p>
 * J. Provide the ability to track user activity by recording timestamps for user log-ins
 * in a .txt file. Each new record should be appended to the log file,
 * if the file already exists.
 */
public class LoginController extends FormController<TextField> {

    private static final Logger logger = Logger.getLogger(LoginController.class);

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

    private InputForm<TextField> inputForm;
    private MessagingService messagingService;
    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.messagingService = ServiceResolver.getMessagingService();
        this.userService = serviceResolver().getUserService();

        usernameLabel.setText(messagingService.getUsername());
        passwordLabel.setText(messagingService.getPassword());
        loginButton.setText(messagingService.getLogin());
        inputForm = createInputForm();

        setDefaultOutput(messagingService.getNewLogin());
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        // passing the user service login functionality as a supplier to the form submit handler method
        // which in this case will perform some validation and exception handling under the hood
        final CheckedSupplier<Boolean> formSupplier = () -> userService.login(usernameField.getText(), passwordField.getText());
        formSubmitHandler(inputForm, formSupplier)
                .ifPresent(validLogin -> handleLoginStatus(actionEvent, validLogin));
    }

    private void handleLoginStatus(ActionEvent actionEvent, boolean validLogin) {
        if (validLogin) {
            logger.log(String.format("successful login for user: %s", getUserId()));
            eventViewHandler(actionEvent, getClass(), "../view/main.fxml");
        } else {
            setRedOutput(messagingService.getInvalidLogin());
        }
    }

    private String getUserId() {
        return userService.getCurrentUser()
                .map(UserDTO::getId)
                .map(String::valueOf)
                .orElse(null);
    }

    private InputForm<TextField> createInputForm() {
        final Map<String, TextField> fields = new HashMap<String, TextField>() {
            {
                put(usernameLabel.getText(), usernameField);
                put(passwordLabel.getText(), passwordField);
            }
        };
        return new InputForm<>(fields);
    }
}
