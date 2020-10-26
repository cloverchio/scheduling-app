package com.c195.dao;

import java.sql.Connection;
import java.util.Optional;

public class ReportDAO {

    private static ReportDAO daoInstance;
    private final Connection connection;

    private ReportDAO(Connection connection) {
        this.connection = connection;
    }

    public static ReportDAO getInstance(Connection connection) {
        return Optional.ofNullable(daoInstance)
                .orElseGet(() -> {
                    daoInstance = new ReportDAO(connection);
                    return daoInstance;
                });
    }
}
