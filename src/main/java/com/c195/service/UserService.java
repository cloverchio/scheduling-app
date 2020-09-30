package com.c195.service;

import com.c195.dao.DAOException;
import com.c195.dao.UserDAO;
import com.c195.model.User;

import java.util.Optional;

public class UserService {

    private static UserService serviceInstance;
    private final UserDAO userDAO;

    private User currentUser;

    private UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static UserService getInstance(UserDAO userDAO) {
        return Optional.ofNullable(serviceInstance)
                .orElseGet(() -> {
                    serviceInstance = new UserService(userDAO);
                    return serviceInstance;
                });
    }

    /**
     * Retrieves the current user if one is available.
     *
     * @return optional representing the current user.
     */
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    /**
     * Validates the existence of a given username and password
     * for login purposes.
     *
     * @param username in which to validate for login.
     * @param password in which to validate for login.
     * @return boolean representing the login status.
     * @throws DAOException if there are issues retrieving users from the db.
     */
    public boolean login(String username, String password) throws DAOException {
        userDAO.getUserByUsernameAndPassword(username, password).ifPresent(user -> this.currentUser = user);
        return getCurrentUser().isPresent();
    }

    /**
     * Removes the current user in preparation for logout.
     */
    public void logout() {
        getCurrentUser().ifPresent(currentUser -> this.currentUser = null);
    }
}
