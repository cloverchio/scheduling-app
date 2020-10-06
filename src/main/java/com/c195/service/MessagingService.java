package com.c195.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * The message content is not utf-8 encoded when loaded directly from the resource bundle
 * causing special characters to not be rendered correctly. This addresses that by encoding
 * all of the messages up front and caching the encoded versions in memory.
 */
public class MessagingService {

    private static MessagingService serviceInstance;
    private final Map<String, String> messaging;

    private MessagingService() {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("message", Locale.getDefault());
        this.messaging = getEncodedMessageBundle(resourceBundle);
    }

    public static MessagingService getInstance() {
        return Optional.ofNullable(serviceInstance)
                .orElseGet(() -> {
                    serviceInstance = new MessagingService();
                    return serviceInstance;
                });
    }

    private static Map<String, String> getEncodedMessageBundle(ResourceBundle resourceBundle) {
        return resourceBundle.keySet().stream()
                .collect(Collectors.toMap(key -> key, value -> toUTF8(resourceBundle.getString(value))));
    }

    private static String toUTF8(String message) {
        return new String(message.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    public String getUsername() {
        return messaging.get("username");
    }

    public String getPassword() {
        return messaging.get("password");
    }

    public String getLogin() {
        return messaging.get("login");
    }

    public String getNewLogin() {
        return messaging.get("login.message");
    }

    public String getInvalidLogin() {
        return messaging.get("login.invalid");
    }

    public String getRequiredFields() {
        return messaging.get("required.input.field");
    }

    public String getDatabaseErrorTitle() {
        return messaging.get("db.error.title");
    }

    public String getDatabaseErrorHeader() {
        return messaging.get("db.error.header");
    }

    public String getDatabaseErrorContent() {
        return messaging.get("db.error.content");
    }

    public String getUnexpectedErrorTitle() {
        return messaging.get("unexpected.error.title");
    }

    public String getUnexpectedErrorHeader() {
        return messaging.get("unexpected.error.header");
    }

    public String getUnexpectedErrorContent() {
        return messaging.get("unexpected.error.content");
    }
}
