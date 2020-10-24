package com.c195.controller;

import com.c195.common.CheckedSupplier;
import com.c195.dao.UserDAO;
import com.c195.service.UserService;
import com.c195.util.ControllerUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Functionality specific to forms and submission.
 */
public class FormController extends Controller implements Initializable {

    @FXML
    private Label validationField;

    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        getDatabaseConnection()
                .ifPresent(connection -> userService = UserService.getInstance(UserDAO.getInstance(connection)));
    }

    public UserService getUserService() {
        return userService;
    }

    public Label getValidationField() {
        return validationField;
    }

    public void setValidationField(String validationFieldText) {
        ControllerUtils.displayAsDefault(getValidationField());
        validationField.setText(validationFieldText);
    }

    public void setValidationFieldError(String validationFieldText) {
        ControllerUtils.displayAsRed(getValidationField());
        validationField.setText(validationFieldText);
    }

    /**
     * Performs a form submission if the confirmation is accepted the user.
     *
     * @param formFields        fields to perform validation on.
     * @param confirmationAlert alert to display while waiting for confirmation.
     * @param submitAction      database action to be performed if no invalid fields.
     * @param <T>               the return type expected from the database operation.
     * @return optional of the submit operation's return type.
     */
    protected <T> Optional<T> formFieldSubmitActionWithConfirmation(Map<Label, TextInputControl> formFields,
                                                                    Alert confirmationAlert,
                                                                    CheckedSupplier<T> submitAction) {
        final Optional<ButtonType> buttonResponse = confirmationAlert.showAndWait()
                .filter(buttonType -> buttonType == ButtonType.OK);
        if (buttonResponse.isPresent()) {
            return formFieldSubmitAction(formFields, submitAction);
        }
        return Optional.empty();
    }

    /**
     * This is probably crossing the line but the result of an idea I had to reuse the form field
     * validation in conjunction with a database operation. Which are things we will most likely
     * want to use on every form submission within the project.
     *
     * @param formFields         fields to perform validation on.
     * @param formDatabaseAction database action to be performed if no invalid fields.
     * @param <T>                the return type expected from the database operation.
     * @return optional of the database operation's expected return type.
     */
    protected <T> Optional<T> formFieldSubmitAction(Map<Label, TextInputControl> formFields,
                                                    CheckedSupplier<T> formDatabaseAction) {
        final Map<Label, TextInputControl> invalidFields = ControllerUtils.getInvalidTextFields(formFields);
        if (!invalidFields.isEmpty()) {
            ControllerUtils.displayAsRed(validationField);
            showRequiredFieldMessage(invalidFields.keySet());
            return Optional.empty();
        } else {
            ControllerUtils.displayAsDefault(validationField);
            return performDatabaseAction(formDatabaseAction);
        }
    }

    private void showRequiredFieldMessage(Set<Label> invalidLabels) {
        final String requiredFields = invalidLabels.stream()
                .map(Labeled::getText)
                .collect(Collectors.joining(", "));
        ControllerUtils.displayAsRed(validationField);
        validationField.setText(getMessagingService().getRequiredFields() + ": " + requiredFields);
    }
}
