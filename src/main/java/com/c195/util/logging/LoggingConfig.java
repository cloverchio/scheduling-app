package com.c195.util.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoggingConfig {

    private static LoggingConfig configInstance;
    private final Properties loggingProperties;

    private LoggingConfig() throws LoggingConfigException {
        this.loggingProperties = new Properties();
        loadProperties();
    }

    public static LoggingConfig getInstance() throws LoggingConfigException {
        if (configInstance == null) {
            configInstance = new LoggingConfig();
        }
        return configInstance;
    }

    private void loadProperties() throws LoggingConfigException {
        try (InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("logging.properties")) {
            loggingProperties.load(inputStream);
        } catch (IOException e) {
            throw new LoggingConfigException("There was an issue loading logging properties", e);
        }
    }

    public String getDirectory() {
        return loggingProperties.getProperty("logging.directory");
    }
}
