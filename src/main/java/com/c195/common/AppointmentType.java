package com.c195.common;

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
}
