package com.c195.util;

import java.time.*;

public final class AppointmentUtils {

    public static Instant toUTCInstant(LocalDate date, String time) {
        final LocalDateTime localDateTime = AppointmentUtils.toLocalDateTime(date, time);
        return AppointmentUtils.toUTCInstant(localDateTime);
    }

    public static Instant toUTCInstant(LocalDateTime localDateTime) {
        final ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toInstant();
    }

    public static LocalDateTime toLocalDateTime(Instant utcInstant) {
        return utcInstant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime toLocalDateTime(LocalDate date, String time) {
        final String[] timeSplit = time.split(":");
        return date.atTime(Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]));
    }

    public static String getTime(int hour, int minute) {
        return String.format("%d:%d", hour, minute);
    }
}
