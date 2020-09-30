package com.c195.controller;

import com.c195.dao.DAOException;
import com.c195.dao.UserDAO;
import com.c195.dao.config.DAOConfigException;
import com.c195.dao.config.MysqlConfig;
import com.c195.dao.config.MysqlConnection;
import com.c195.service.MessagingService;
import com.c195.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
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
        messageLabel.setText(messagingService.getNewLogin());
        usernameLabel.setText(messagingService.getUsername());
        passwordLabel.setText(messagingService.getPassword());
        loginButton.setText(messagingService.getLogin());
        try {
            final MysqlConfig config = MysqlConfig.getInstance();
            final UserDAO userDAO = UserDAO.getInstance(MysqlConnection.getInstance(config));
            this.userService = UserService.getInstance(userDAO);
        } catch (DAOConfigException e) {
            Controller.showDatabaseAlert(messagingService);
            e.printStackTrace();
        }
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
        try {
            if (userService.login(username, password)) {
                getMainView(actionEvent);
            } else {
                messageLabel.setText(messagingService.getInvalidLogin());
            }
        } catch (DAOException e) {
            messageLabel.setText(messagingService.getDatabaseLoginError());
            e.printStackTrace();
        } catch (IOException e) {
            Controller.showUnexpectedAlert(messagingService);
            e.printStackTrace();
        }
    }

    private void getMainView(ActionEvent actionEvent) throws IOException {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        final Parent root = FXMLLoader.load(getClass().getResource("../view/main.fxml"));
        final Scene scene = new Scene(root);
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}
