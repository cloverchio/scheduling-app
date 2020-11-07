package com.c195.util.logging;

import java.time.Clock;

public class Logger {

    private final LoggingConfig loggingConfig;
    private final Clock clock;

    public Logger(LoggingConfig loggingConfig, Clock clock) {
        this.loggingConfig = loggingConfig;
        this.clock = clock;
    }
}
