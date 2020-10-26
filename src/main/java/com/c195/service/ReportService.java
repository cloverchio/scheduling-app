package com.c195.service;

import com.c195.dao.ReportDAO;

import java.util.Optional;

public class ReportService {

    private static ReportService serviceInstance;
    private ReportDAO reportDAO;

    private ReportService(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    public static ReportService getInstance(ReportDAO reportDAO) {
        return Optional.ofNullable(serviceInstance)
                .orElseGet(() -> {
                    serviceInstance = new ReportService(reportDAO);
                    return serviceInstance;
                });
    }
}
