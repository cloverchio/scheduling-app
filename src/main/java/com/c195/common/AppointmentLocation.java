package com.c195.common;

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
                .orElseThrow(() -> new AppointmentException("Appointment location not found for " + name));
    }
}
