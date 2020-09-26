package com.c195.dao.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class MysqlConnection {

    private static Connection connection;

    private static void connect(MysqlConfig mysqlConfig) {
        final String jdbcURL =
                String.format("%s:%s/%s", mysqlConfig.getURL(), mysqlConfig.getPort(), mysqlConfig.getName());
        try {
            Class.forName(mysqlConfig.getDriver());
            connection = DriverManager.getConnection(jdbcURL, mysqlConfig.getUser(), mysqlConfig.getPass());
        } catch (ClassNotFoundException e) {
            System.out.println("jdbc driver class not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("there was an issue establishing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getInstance(MysqlConfig mysqlConfig) {
        if (connection == null) {
            connect(mysqlConfig);
        }
        return connection;
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("there was an issue disconnecting from database: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
