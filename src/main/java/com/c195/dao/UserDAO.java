package com.c195.dao;

import com.c195.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDAO {

    private static final String USER_BY_USERNAME_AND_PASSWORD_SQL = "" +
            "SELECT * " +
            "FROM user " +
            "WHERE userName = ? " +
            "AND password = ?";

    private static UserDAO daoInstance;
    private final Connection connection;

    private UserDAO(Connection connection) {
        this.connection = connection;
    }

    public static UserDAO getInstance(Connection connection) {
        if (daoInstance == null) {
            daoInstance = new UserDAO(connection);
        }
        return daoInstance;
    }

    public Optional<User> getUserByUsernameAndPassword(String username, String password) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(USER_BY_USERNAME_AND_PASSWORD_SQL)) {
            statement.setString(1, username);
            statement.setString(2, password);
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toUser(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("There was an issue retrieving a user", e);
        }
    }

    public static User toUser(ResultSet resultSet) throws DAOException {
        try {
            final User user = new User();
            user.setId(resultSet.getInt("userId"));
            user.setUsername(resultSet.getString("userName"));
            user.setPassword(resultSet.getString("password"));
            user.setActive(resultSet.getBoolean("active"));
            user.setMetadata(MetadataDAO.toMetadata(resultSet));
            return user;
        } catch (SQLException e) {
            throw new DAOException("There was an issue creating user data", e);
        }
    }
}
