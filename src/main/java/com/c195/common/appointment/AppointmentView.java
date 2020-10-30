package com.c195.common.appointment;

public enum AppointmentView {

    ALL("All"),
    WEEK("Week"),
    MONTH("Month");

    private final String name;

    AppointmentView(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
