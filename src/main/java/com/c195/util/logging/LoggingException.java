package com.c195.util.logging;

public class LoggingException extends RuntimeException {

    public LoggingException(Throwable cause) {
        super(cause);
    }

    public LoggingException(String message, Throwable cause) {
        super(message, cause);
    }
}