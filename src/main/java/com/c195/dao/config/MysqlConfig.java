package com.c195.dao.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class MysqlConfig {

    private static MysqlConfig configInstance;
    private final Properties mysqlProperties;

    private MysqlConfig() {
        this.mysqlProperties = new Properties();
        loadProperties();
    }

    public static MysqlConfig getInstance() {
        if (configInstance == null) {
            configInstance = new MysqlConfig();
        }
        return configInstance;
    }

    private void loadProperties() {
        final InputStream inputStream = this.getClass().getResourceAsStream("mysqlconfig.properties");
        try {
            mysqlProperties.load(inputStream);
        } catch (IOException e) {
            System.out.println("there was an issue loading database configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getName() {
        return mysqlProperties.getProperty("mysql.name");
    }

    public String getUser() {
        return mysqlProperties.getProperty("mysql.user");
    }

    public String getPass() {
        return mysqlProperties.getProperty("mysql.pass");
    }

    public String getPort() {
        return mysqlProperties.getProperty("mysql.port");
    }

    public String getURL() {
        return mysqlProperties.getProperty("mysql.url");
    }

    public String getDriver() {
        return mysqlProperties.getProperty("mysql.driver");
    }
}
