package com.c195.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AppointmentUtils {

    public static LocalDateTime toLocalDateTime(LocalDate date, String time) {
        final String[] timeSplit = time.split(":");
        return date.atTime(Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]));
    }

    public static String getTimeField(int hour, int minute) {
        return String.format("%d:%d", hour, minute);
    }
}
