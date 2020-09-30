package com.c195.controller;

import com.c195.service.MessagingService;
import javafx.scene.control.Alert;

public class Controller {

    public static void showDatabaseAlert(MessagingService messagingService) {
        errorAlert(messagingService.getDatabaseErrorTitle(), 
                messagingService.getDatabaseErrorHeader(), 
                messagingService.getDatabaseErrorContent())
                .showAndWait();
    }
    
    public static void showUnexpectedAlert(MessagingService messagingService) {
        errorAlert(messagingService.getUnexpectedErrorTitle(),
                messagingService.getUnexpectedErrorHeader(),
                messagingService.getUnexpectedErrorContent())
                .showAndWait();
    }
    
    private static Alert errorAlert(String title, String header, String content) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }
}
