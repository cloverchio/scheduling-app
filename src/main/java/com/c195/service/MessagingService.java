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
 * all of the messages up front and caching the encoded version in memory.
 * <p>
 * Addresses the following task requirements:
 * <p>
 * G. Write two or more lambda expressions to make your program more efficient, justifying
 * the use of each lambda expression with an in-line comment.
 */
public class MessagingService {

    private static MessagingService serviceInstance;
    private final Map<String, String> messaging;

    private MessagingService() {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("message", Locale.getDefault());
        this.messaging = getEncodedMessageBundle(resourceBundle);
    }

    public static MessagingService getInstance() {
        // lazy singleton initialization leveraging a lambda expression
        return Optional.ofNullable(serviceInstance)
                .orElseGet(() -> {
                    serviceInstance = new MessagingService();
                    return serviceInstance;
                });
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

    public String getEmptyUsername() {
        return messaging.get("login.empty.username");
    }

    public String getEmptyPassword() {
        return messaging.get("login.empty.password");
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

    public String getDatabaseLoginError() {
        return messaging.get("db.login.error");
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

    public String getAppointmentReminderTitle() {
        return messaging.get("appointment.reminder.title");
    }

    public String getAppointmentReminderHeader() {
        return messaging.get("appointment.reminder.header");
    }

    public String getAppointmentReminderContent() {
        return messaging.get("appointment.reminder.content");
    }

    private static Map<String, String> getEncodedMessageBundle(ResourceBundle resourceBundle) {
        // caches utf-8 version of locale specific messaging
        // lambda expression facilitates the key mapping and value transformation
        return resourceBundle.keySet().stream()
                .collect(Collectors.toMap(k -> k, v -> toUTF8(resourceBundle.getString(v))));
    }

    private static String toUTF8(String message) {
        return new String(message.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
