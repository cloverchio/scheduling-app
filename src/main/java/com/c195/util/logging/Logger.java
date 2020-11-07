package com.c195.util.logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class Logger {

    private final Class<?> clazz;
    private final LoggingConfig loggingConfig;
    private final Clock clock;

    private Logger(Class<?> clazz,
                   LoggingConfig loggingConfig,
                   Clock clock) {
        this.clazz = clazz;
        this.loggingConfig = loggingConfig;
        this.clock = clock;
    }

    public static Logger getLogger(Class<?> clazz) {
        try {
            return new Logger(clazz, LoggingConfig.getInstance(), Clock.systemUTC());
        } catch (LoggingConfigException e) {
            throw new LoggingException("there was an issue generating logging", e);
        }
    }

    public void log(String content) throws LoggingException {
        final Path path = getFilePath().toAbsolutePath();
        try {
            final StandardOpenOption openOption = Files.exists(path)
                    ? StandardOpenOption.APPEND
                    : StandardOpenOption.CREATE;
            Files.write(path, Collections.singletonList(toLogMessage(content)), StandardCharsets.UTF_8, openOption);
        } catch (IOException e) {
            throw new LoggingException(e);
        }
    }

    private Path getFilePath() {
        final String fileName = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now(clock));
        return Paths.get(String.format("%s/%s.log", loggingConfig.getDirectory(), fileName));
    }

    private String toLogMessage(String content) {
        final String timestamp = DateTimeFormatter.ISO_INSTANT.format(clock.instant());
        return String.format("%s %s: %s\n", timestamp, clazz.getName(), content);
    }
}
