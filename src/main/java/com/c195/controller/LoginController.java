package com.c195.controller;

import com.c195.dao.UserDAO;
import com.c195.service.MessagingService;
import com.c195.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Facilitates the login interactions.
 * <p>
 * Addresses the following task requirements:
 * <p>
 * A. Create a log-in form that can determine the user’s location and translate log-in and error control messages
 * (e.g., “The username and password did not match.”) into two languages.
 */
public class LoginController implements Initializable {

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

    private MessagingService messagingService;
    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.messagingService = MessagingService.getInstance();
        initializeLabelText(messagingService);
        Controller.getDatabaseConnection(messagingService)
                .ifPresent(connection -> this.userService = UserService.getInstance(UserDAO.getInstance(connection)));
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        final String username = usernameField.getText();
        final String password = passwordField.getText();
        if (username == null || username.isEmpty()) {
            messageLabel.setText(messagingService.getEmptyUsername());
        } else if (password == null || password.isEmpty()) {
            messageLabel.setText(messagingService.getEmptyPassword());
        } else {
            login(actionEvent, username, password);
        }
    }

    private void login(ActionEvent actionEvent, String username, String password) {
        Controller.performDatabaseAction(() -> userService.login(username, password), messagingService)
                .ifPresent(validLogin -> handleLoginStatus(actionEvent, validLogin));
    }

    private void handleLoginStatus(ActionEvent actionEvent, boolean validLogin) {
        if (validLogin) {
            Controller.showView(actionEvent, getClass(), "../view/main.fxml", messagingService);
        } else {
            messageLabel.setText(messagingService.getInvalidLogin());
        }
    }

    private void initializeLabelText(MessagingService messagingService) {
        messageLabel.setText(messagingService.getNewLogin());
        usernameLabel.setText(messagingService.getUsername());
        passwordLabel.setText(messagingService.getPassword());
        loginButton.setText(messagingService.getLogin());
    }
}
