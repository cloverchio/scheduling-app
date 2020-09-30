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
        return Optional.ofNullable(daoInstance)
                .orElseGet(() -> {
                    daoInstance = new UserDAO(connection);
                    return daoInstance;
                });
    }

    public Optional<User> getUserByUsernameAndPassword(String username, String password) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(USER_BY_USERNAME_AND_PASSWORD_SQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toUser(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving a user", e);
        }
    }

    public static User toUser(ResultSet resultSet) throws DAOException {
        try {
            return new User.Builder()
                    .withId(resultSet.getInt("userId"))
                    .withUsername(resultSet.getString("userName"))
                    .withPassword(resultSet.getString("password"))
                    .withActive(resultSet.getBoolean("active"))
                    .withMetadata(MetadataDAO.toMetadata(resultSet))
                    .build();
        } catch (SQLException e) {
            throw new DAOException("there was an issue creating user data", e);
        }
    }
}
