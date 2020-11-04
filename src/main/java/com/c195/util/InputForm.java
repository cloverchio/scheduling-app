package com.c195.util;

import javafx.scene.control.TextInputControl;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InputForm<V extends TextInputControl> {

    private final Map<String, V> fields;

    public InputForm(Map<String, V> fields) {
        this.fields = fields;
    }

    public Map<String, V> getAllFields() {
        return fields;
    }

    public Map<String, V> getValidFields() {
        final Map<String, V> invalidFields = getInvalidFields();
        return fields.entrySet()
                .stream()
                .filter(entry -> !invalidFields.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, V> getInvalidFields() {
        return fields.entrySet()
                .stream()
                .filter(entry -> hasValidText().negate().test(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Predicate<V> hasValidText() {
        return textField -> {
            final String text = textField.getText();
            return text != null && !text.isEmpty();
        };
    }
}
