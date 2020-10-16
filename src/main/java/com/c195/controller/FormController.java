package com.c195.controller;

import com.c195.common.CheckedSupplier;
import com.c195.dao.UserDAO;
import com.c195.service.UserService;
import com.c195.util.ControllerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Functionality specific to forms and submission.
 */
public class FormController extends Controller implements ServiceInitializable {

    @FXML
    private Label messagingField;

    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        getDatabaseConnection().ifPresent(this::initializeServices);
    }

    @Override
    public void initializeServices(Connection connection) {
        userService = UserService.getInstance(UserDAO.getInstance(connection));
    }

    public UserService getUserService() {
        return userService;
    }

    public Label getMessagingField() {
        return messagingField;
    }

    public void setMessagingField(String messagingFieldText) {
        messagingField.setText(messagingFieldText);
    }

    /**
     * This is probably crossing the line but the result of an idea I had to reuse the form field
     * validation in conjunction with a database operation. Which are things we will most likely
     * want to do on every form submission across the project.
     *
     * @param formFields         fields to perform validation on.
     * @param formDatabaseAction database action to be performed if no invalid fields.
     * @param <T>                the return type expected from the database operation.
     * @return optional of the database operation's expected return type.
     */
    public <T> Optional<T> formFieldSubmitAction(Map<Label, TextField> formFields,
                                                 CheckedSupplier<T> formDatabaseAction) {
        final Map<Label, TextField> invalidFields = ControllerUtils.getInvalidTextFields(formFields);
        if (!invalidFields.isEmpty()) {
            showRequiredFieldMessage(invalidFields.keySet());
            ControllerUtils.displayAsRed(messagingField);
            return Optional.empty();
        } else {
            ControllerUtils.displayAsDefault(messagingField);
            return performDatabaseAction(formDatabaseAction);
        }
    }

    private void showRequiredFieldMessage(Set<Label> invalidLabels) {
        final String requiredFields = invalidLabels.stream()
                .map(Labeled::getText)
                .collect(Collectors.joining(", "));
        messagingField.setText(getMessagingService().getRequiredFields() + ": " + requiredFields);
        ControllerUtils.displayAsRed(messagingField);
    }
}
