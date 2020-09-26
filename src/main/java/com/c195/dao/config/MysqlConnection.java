package com.c195.dao.config;

import com.c195.dao.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class MysqlConnection {

    private static Connection connection;

    private static void connect(MysqlConfig mysqlConfig) throws DAOException {
        final String jdbcURL =
                String.format("%s:%s/%s", mysqlConfig.getURL(), mysqlConfig.getPort(), mysqlConfig.getName());
        try {
            Class.forName(mysqlConfig.getDriver());
            connection = DriverManager.getConnection(jdbcURL, mysqlConfig.getUser(), mysqlConfig.getPass());
        } catch (ClassNotFoundException | SQLException e) {
            throw new DAOException("there was an issue connecting to the db", e);
        }
    }

    public static Connection getInstance(MysqlConfig mysqlConfig) throws DAOException {
        if (connection == null) {
            connect(mysqlConfig);
        }
        return connection;
    }

    public static void close() throws DAOException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DAOException("there was an issue closing the db connection", e);
            }
        }
    }
}
