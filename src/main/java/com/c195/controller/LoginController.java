package com.c195.controller;

import com.c195.dao.DAOException;
import com.c195.dao.UserDAO;
import com.c195.dao.config.DAOConfigException;
import com.c195.dao.config.MysqlConfig;
import com.c195.dao.config.MysqlConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.c195.service.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

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

    private ResourceBundle messageBundle;
    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.messageBundle = ResourceBundle.getBundle("message", Locale.getDefault());
        messageLabel.setText(messageBundle.getString("login.message"));
        usernameLabel.setText(messageBundle.getString("username"));
        passwordLabel.setText(messageBundle.getString("password"));
        loginButton.setText(messageBundle.getString("login"));
        try {
            final MysqlConfig config = MysqlConfig.getInstance();
            final UserDAO userDAO = UserDAO.getInstance(MysqlConnection.getInstance(config));
            this.userService = UserService.getInstance(userDAO);
        } catch (DAOConfigException e) {
            showDatabaseAlert();
        }
    }

    @FXML
    public void login(ActionEvent actionEvent) throws IOException {
        final String username = usernameField.getText();
        final String password = passwordField.getText();
        if (username == null || username.isEmpty()) {
            messageLabel.setText(messageBundle.getString("login.empty.username"));
        } else if (password == null || password.isEmpty()) {
            messageLabel.setText(messageBundle.getString("login.empty.password"));
        } else {
            login(actionEvent, username, password);
        }
    }

    private void login(ActionEvent actionEvent, String username, String password) throws IOException {
        try {
            if (userService.verifyLogin(username, password)) {
                getMainView(actionEvent);
            } else {
                messageLabel.setText(messageBundle.getString("login.invalid"));
            }
        } catch (DAOException e) {
            messageLabel.setText(messageBundle.getString("db.login.error"));
        }
    }

    private void showDatabaseAlert() {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(messageBundle.getString("db.error.title"));
        alert.setHeaderText(messageBundle.getString("db.error.header"));
        alert.setContentText(messageBundle.getString("db.error.content"));
        alert.showAndWait();
    }

    private void getMainView(ActionEvent actionEvent) throws IOException {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        final Stage stage = new Stage();
        final Parent root = FXMLLoader.load(getClass().getResource("views/main.fxml"));
        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
