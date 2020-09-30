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
        if (serviceInstance == null) {
            serviceInstance = new UserService(userDAO);
        }
        return serviceInstance;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public boolean login(String username, String password) throws DAOException {
        userDAO.getUserByUsernameAndPassword(username, password)
                .ifPresent(user -> currentUser = user);
        return getCurrentUser().isPresent();
    }

    public void logout() {
        getCurrentUser().ifPresent(currentUser -> this.currentUser = null);
    }
}
