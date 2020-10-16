package com.c195.util;

import java.time.*;

public final class AppointmentUtils {

    public static LocalDateTime toLocalDateTime(LocalDate date, String time) {
        final String[] timeSplit = time.split(":");
        return date.atTime(Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]));
    }

    public static Instant toUTCInstant(LocalDateTime localDateTime) {
        final ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toInstant();
    }
}
