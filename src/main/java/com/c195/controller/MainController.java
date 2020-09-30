package com.c195.controller;

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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private MessagingService messagingService;
    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.messagingService = MessagingService.getInstance();
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
    public void logout(ActionEvent actionEvent) {
        try {
            userService.logout();
            getLoginView(actionEvent);
        } catch (IOException e) {
            Controller.showUnexpectedAlert(messagingService);
            e.printStackTrace();
        }
    }

    private void getLoginView(ActionEvent actionEvent) throws IOException {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        final Parent root = FXMLLoader.load(getClass().getResource("../view/login.fxml"));
        final Scene scene = new Scene(root);
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}
