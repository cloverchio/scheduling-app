package com.c195.common;

import com.c195.service.AppointmentException;

import java.util.Arrays;

public enum AppointmentLocation {

    PHOENIX("Phoenix", "America/Phoenix"),
    NEW_YORK("New York", "America/New_York"),
    LONDON("London", "Europe/London");

    private final String name;
    private final String zoneId;

    AppointmentLocation(String name, String zoneId) {
        this.name = name;
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }

    public String getZoneId() {
        return zoneId;
    }

    public static AppointmentLocation fromName(String name) {
        return Arrays.stream(AppointmentLocation.values())
                .filter(location -> location.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AppointmentException("Location not found for " + name));
    }
}
