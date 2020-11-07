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
public class FormController<V extends TextInputControl> extends Controller implements Initializable {

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

    protected <T> Optional<T> formSubmitConfirmationHandler(InputForm<V> inputForm,
                                                            Alert confirmationAlert,
                                                            CheckedSupplier<T> formSupplier) {
        return confirmationHandler(confirmationAlert, formSupplier, confirmationSupplier -> formSubmitHandler(inputForm, formSupplier));
    }

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
}
