package com.c195.common;

import com.c195.service.AppointmentException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class AppointmentTime {

    private static final ZoneId utcZoneId = ZoneId.of("UTC");

    private final LocalDateTime start;
    private final LocalDateTime end;
    private final ZonedDateTime zonedStart;
    private final ZonedDateTime zonedEnd;
    private final Instant utcStart;
    private final Instant utcEnd;

    public AppointmentTime(LocalDateTime start, LocalDateTime end) throws AppointmentException {
        this.start = start;
        this.end = end;
        this.zonedStart = start.atZone(ZoneId.systemDefault());
        this.zonedEnd = end.atZone(ZoneId.systemDefault());
        this.utcStart = zonedStart.withZoneSameInstant(utcZoneId).toInstant();
        this.utcEnd = zonedEnd.withZoneSameInstant(utcZoneId).toInstant();
        validateAppointmentTime(start, end);
    }

    public AppointmentTime(Instant utcStart, Instant utcEnd) throws AppointmentException {
        this.utcStart = utcStart;
        this.utcEnd = utcEnd;
        this.zonedStart = utcStart.atZone(ZoneId.systemDefault());
        this.zonedEnd = utcEnd.atZone(ZoneId.systemDefault());
        this.start = zonedStart.toLocalDateTime();
        this.end = zonedEnd.toLocalDateTime();
        validateAppointmentTime(start, end);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public ZonedDateTime getZonedStart() {
        return zonedStart;
    }

    public ZonedDateTime getZonedEnd() {
        return zonedEnd;
    }

    public Instant getUtcStart() {
        return utcStart;
    }

    public Instant getUtcEnd() {
        return utcEnd;
    }

    private static void validateAppointmentTime(LocalDateTime start, LocalDateTime end) throws AppointmentException {
        if (end.isBefore(start)) {
            throw new AppointmentException("Appointment end is before start");
        }
        if (start.isAfter(end)) {
            throw new AppointmentException("Appointment start is after end");
        }
        if (start.getHour() < 9 || end.getHour() > 17) {
            throw new AppointmentException("Appointment is outside of business hours (9-5)");
        }
    }
}
