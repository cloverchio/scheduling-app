package com.c195.common.appointment;

import java.util.Arrays;

public enum AppointmentType {

    SUPPORT("Support"),
    SALES("Sales");

    private final String name;

    AppointmentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AppointmentType fromName(String name) throws AppointmentException {
        if (name != null) {
            return Arrays.stream(AppointmentType.values())
                    .filter(location -> location.getName().equals(name))
                    .findFirst()
                    .orElseThrow(() -> new AppointmentException("Appointment type not found for " + name));
        }
        throw new AppointmentException("Appointment type is required");
    }
}
