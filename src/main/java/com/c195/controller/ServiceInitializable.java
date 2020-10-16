package com.c195.controller;

import javafx.fxml.Initializable;

import java.sql.Connection;

public interface ServiceInitializable extends Initializable {

    void initializeServices(Connection connection);
}
