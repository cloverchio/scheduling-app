package com.c195.common.appointment;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class AppointmentTime {

    private static final String TIME_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    private static final ZoneId utcZoneId = ZoneId.of("UTC");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    private final ZonedDateTime locationStart;
    private final ZonedDateTime locationEnd;
    private final ZonedDateTime userStart;
    private final ZonedDateTime userEnd;
    private final Instant utcStart;
    private final Instant utcEnd;

    public AppointmentTime(LocalDate locationStartDate,
                           String locationStartTime,
                           LocalDate locationEndDate,
                           String locationEndTime,
                           String locationZoneId) throws AppointmentException {
        validateDate(locationStartDate, locationEndDate);
        validateTimeFormat(locationStartTime, locationEndTime);
        this.locationStart = toLocationTime(locationStartDate, locationStartTime, locationZoneId);
        this.locationEnd = toLocationTime(locationEndDate, locationEndTime, locationZoneId);
        this.userStart = locationStart.withZoneSameInstant(ZoneId.systemDefault());
        this.userEnd = locationEnd.withZoneSameInstant(ZoneId.systemDefault());
        this.utcStart = locationStart.withZoneSameInstant(utcZoneId).toInstant();
        this.utcEnd = locationEnd.withZoneSameInstant(utcZoneId).toInstant();
        validateDateTime(locationStart, locationEnd);
    }

    public AppointmentTime(Instant utcStart,
                           Instant utcEnd,
                           String locationZoneId) throws AppointmentException {
        this.utcStart = utcStart;
        this.utcEnd = utcEnd;
        this.locationStart = utcStart.atZone(ZoneId.of(locationZoneId));
        this.locationEnd = utcEnd.atZone(ZoneId.of(locationZoneId));
        this.userStart = utcStart.atZone(ZoneId.systemDefault());
        this.userEnd = utcEnd.atZone(ZoneId.systemDefault());
    }

    public ZonedDateTime getLocationStart() {
        return locationStart;
    }

    public String getLocationStartISO() {
        return locationStart.format(formatter);
    }

    public ZonedDateTime getLocationEnd() {
        return locationEnd;
    }

    public String getLocationEndISO() {
        return locationEnd.format(formatter);
    }

    public ZonedDateTime getUserStart() {
        return userStart;
    }

    public String getUserStartISO() {
        return userStart.format(formatter);
    }

    public ZonedDateTime getUserEnd() {
        return userEnd;
    }

    public String getUserEndISO() {
        return userEnd.format(formatter);
    }

    public Instant getUtcStart() {
        return utcStart;
    }

    public Instant getUtcEnd() {
        return utcEnd;
    }

    private static void validateDate(LocalDate startDate, LocalDate endDate) throws AppointmentException {
        if (startDate == null) {
            throw new AppointmentException("Appointment start date is required");
        }
        if (endDate == null) {
            throw new AppointmentException("Appointment end date is required");
        }
    }

    private static void validateTimeFormat(String startTime, String endTime) throws AppointmentException {
        final Pattern pattern = Pattern.compile(TIME_PATTERN);
        final boolean validStart = pattern.matcher(startTime).matches();
        final boolean validEnd = pattern.matcher(endTime).matches();
        if (!validStart) {
            throw new AppointmentException("Appointment start format is invalid");
        }
        if (!validEnd) {
            throw new AppointmentException("Appointment end format is invalid");
        }
    }

    private static void validateDateTime(ZonedDateTime locationStart, ZonedDateTime locationEnd) throws AppointmentException {
        if (locationEnd.isBefore(locationStart)) {
            throw new AppointmentException("Appointment end is before start");
        }
        if (locationStart.isAfter(locationEnd)) {
            throw new AppointmentException("Appointment start is after end");
        }
        if (locationStart.getHour() < 9 || locationEnd.getHour() > 17) {
            throw new AppointmentException("Appointment is outside of business hours (9-5)");
        }
    }

    private static ZonedDateTime toLocationTime(LocalDate date, String time, String zoneId) {
        final String[] timeSplit = time.split(":");
        return date.atTime(Integer.parseInt(timeSplit[0]), Integer.parseInt(timeSplit[1]))
                .atZone(ZoneId.of(zoneId));
    }
}
