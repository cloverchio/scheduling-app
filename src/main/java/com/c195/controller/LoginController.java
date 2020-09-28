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
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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

    private Map<String, String> encodedMessages;
    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final ResourceBundle messageBundle = ResourceBundle.getBundle("message", Locale.getDefault());
        this.encodedMessages = getEncodedMessageBundle(messageBundle);
        messageLabel.setText(encodedMessages.get("login.message"));
        usernameLabel.setText(encodedMessages.get("username"));
        passwordLabel.setText(encodedMessages.get("password"));
        loginButton.setText(encodedMessages.get("login"));
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
            messageLabel.setText(encodedMessages.get("login.empty.username"));
        } else if (password == null || password.isEmpty()) {
            messageLabel.setText(encodedMessages.get("login.empty.password"));
        } else {
            login(actionEvent, username, password);
        }
    }

    private void login(ActionEvent actionEvent, String username, String password) throws IOException {
        try {
            if (userService.verifyLogin(username, password)) {
                getMainView(actionEvent);
            } else {
                messageLabel.setText(encodedMessages.get("login.invalid"));
            }
        } catch (DAOException e) {
            messageLabel.setText(encodedMessages.get("db.login.error"));
        }
    }

    /**
     * G. Write two or more lambda expressions to make your program more efficient,
     * justifying the use of each lambda expression with an in-line comment.
     *
     * @param resourceBundle in which to extract message bundle from
     * @return map of message bundle keys to utf-8 equivalent
     */
    private static Map<String, String> getEncodedMessageBundle(ResourceBundle resourceBundle) {
        // caches utf-8 version of locale specific messaging
        // lambda expression facilitates the key mapping and value transformation
        return resourceBundle.keySet()
                .stream()
                .collect(Collectors.toMap(k -> k, v -> toUTF8(resourceBundle.getString(v))));
    }

    private static String toUTF8(String message) {
        return new String(message.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    private void showDatabaseAlert() {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(encodedMessages.get("db.error.title"));
        alert.setHeaderText(encodedMessages.get("db.error.header"));
        alert.setContentText(encodedMessages.get("db.error.content"));
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
