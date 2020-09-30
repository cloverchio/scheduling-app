package com.c195.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
        return resourceBundle.keySet().stream()
                .collect(Collectors.toMap(k -> k, v -> toUTF8(resourceBundle.getString(v))));
    }

    private static String toUTF8(String message) {
        return new String(message.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
