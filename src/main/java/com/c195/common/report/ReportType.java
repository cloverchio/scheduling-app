package com.c195.common.report;

import java.util.Arrays;

public enum ReportType {

    APPOINTMENT_TYPES_BY_MONTH("Appointments Types by Month"),
    APPOINTMENT_TYPES_BY_CUSTOMER("Appointments Types by Customer"),
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
