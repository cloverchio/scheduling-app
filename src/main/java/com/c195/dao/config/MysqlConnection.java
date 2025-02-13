package com.c195.dao.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class MysqlConnection {

    private static Connection connection;

    public static Connection getInstance(MysqlConfig mysqlConfig) throws DAOConfigException {
        if (connection == null) {
            connect(mysqlConfig);
        }
        return connection;
    }

    private static void connect(MysqlConfig mysqlConfig) throws DAOConfigException {
        final String jdbcURL =
                String.format("%s:%s/%s?autoReconnect=true", mysqlConfig.getURL(), mysqlConfig.getPort(), mysqlConfig.getName());
        try {
            Class.forName(mysqlConfig.getDriver());
            connection = DriverManager.getConnection(jdbcURL, mysqlConfig.getUser(), mysqlConfig.getPass());
        } catch (ClassNotFoundException | SQLException e) {
            throw new DAOConfigException("There was an issue connecting to the db", e);
        }
    }

    public static void close() throws DAOConfigException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DAOConfigException("There was an issue closing the db connection", e);
            }
        }
    }
}
