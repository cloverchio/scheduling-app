package com.c195.common;

import java.util.Arrays;

public enum ReportType {

    APPOINTMENTS_BY_MONTH("Appointments by Month"),
    APPOINTMENTS_BY_CUSTOMER("Appointments by Customer"),
    SCHEDULE_BY_CONSULTANT("Schedule by Consultant");

    private final String name;

    ReportType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ReportType fromName(String name) {
        return Arrays.stream(ReportType.values())
                .filter(location -> location.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ReportException("Report type not found for " + name));
    }

}
