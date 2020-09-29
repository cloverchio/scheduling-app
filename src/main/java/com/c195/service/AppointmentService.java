package com.c195.service;

import com.c195.dao.AppointmentDAO;
import com.c195.dao.DAOException;
import com.c195.model.Appointment;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AppointmentService {

    private static AppointmentService serviceInstance;
    private final AppointmentDAO appointmentDAO;

    private AppointmentService(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    public static AppointmentService getInstance(AppointmentDAO appointmentDAO) {
        if (serviceInstance == null) {
            serviceInstance = new AppointmentService(appointmentDAO);
        }
        return serviceInstance;
    }

    public List<Appointment> getReminderAppointmentsByUser(int userId) throws DAOException {
        final Instant start = Instant.now();
        final Instant end = start.plus(15L, ChronoUnit.MINUTES);
        return appointmentDAO.getAppointmentsByUserBetween(userId, start, end);
    }
}
