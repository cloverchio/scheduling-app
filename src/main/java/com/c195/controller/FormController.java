package com.c195.controller;

import com.c195.common.CheckedSupplier;
import com.c195.service.ServiceResolver;
import com.c195.util.form.InputForm;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Functionality specific to forms and submission.
 */
public abstract class FormController<V extends TextInputControl> extends Controller implements Initializable {

    @FXML
    private Label outputLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    public void setDefaultOutput(String outputText) {
        outputLabel.setStyle("-fx-text-fill: #000000;");
        outputLabel.setText(outputText);
    }

    public void setRedOutput(String outputText) {
        outputLabel.setStyle("-fx-text-fill: #FF0000;");
        outputLabel.setText(outputText);
    }

    /**
     * Provides a reusable way to prompt a confirmation before content is saved.
     *
     * @param inputForm         the structure containing the input field mapping and validation.
     * @param confirmationAlert the alert to be prompted.
     * @param formSupplier      the service operation to be performed as a supplier.
     * @param <T>               the return type expected from the service operation.
     * @return optional value of what is expected from the service operation.
     */
    protected <T> Optional<T> formSubmitConfirmationHandler(InputForm<V> inputForm,
                                                            Alert confirmationAlert,
                                                            CheckedSupplier<T> formSupplier) {
        return confirmationHandler(confirmationAlert, formSupplier, confirmationSupplier ->
                formSubmitHandler(inputForm, formSupplier));
    }

    /**
     * Provides a reusable way to validate input/text fields before the content is saved.
     *
     * @param inputForm    the structure containing the input field mapping and validation.
     * @param formSupplier the service operation to be performed as a supplier.
     * @param <T>          the return type expected from the service operation.
     * @return optional value of what is expected from the service operation.
     */
    protected <T> Optional<T> formSubmitHandler(InputForm<V> inputForm, CheckedSupplier<T> formSupplier) {
        final Map<String, V> invalidFields = inputForm.getInvalidFields();
        if (!inputForm.getInvalidFields().isEmpty()) {
            setRedOutput(ServiceResolver.getMessagingService().getRequiredFields() +
                    ": " + String.join(", ", invalidFields.keySet()));
            return Optional.empty();
        } else {
            return serviceRequestHandler(formSupplier);
        }
    }

    /**
     * Used to ensure that form based controllers
     * define the appropriate structure used for validation.
     *
     * @return a {@link InputForm} used for field mapping and validation.
     */
    protected abstract InputForm<V> createInputForm();
}
