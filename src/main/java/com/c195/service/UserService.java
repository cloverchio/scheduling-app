package com.c195.service;

import com.c195.dao.DAOException;
import com.c195.dao.UserDAO;

public class UserService {

    private static UserService serviceInstance;
    private final UserDAO userDAO;

    private UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static UserService getInstance(UserDAO userDAO) {
        if (serviceInstance == null) {
            serviceInstance = new UserService(userDAO);
        }
        return serviceInstance;
    }

    public boolean verifyLogin(String username, String password) throws DAOException {
        return userDAO.getUserByUsernameAndPassword(username, password).isPresent();
    }
}
