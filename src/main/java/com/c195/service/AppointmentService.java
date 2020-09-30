package com.c195.service;

import com.c195.dao.AppointmentDAO;
import com.c195.dao.DAOException;
import com.c195.model.Appointment;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class AppointmentService {

    private static AppointmentService serviceInstance;
    private final AppointmentDAO appointmentDAO;

    private AppointmentService(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    public static AppointmentService getInstance(AppointmentDAO appointmentDAO) {
        return Optional.ofNullable(serviceInstance)
                .orElseGet(() -> {
                    serviceInstance = new AppointmentService(appointmentDAO);
                    return serviceInstance;
                });
    }

    /**
     * Gets a list of appointments to occur within the next 15 minutes for
     * a given user.
     *
     * @param userId in which to retrieve appointments for.
     * @return list of appointments.
     * @throws DAOException if there are issues retrieving appointments from the db.
     */
    public List<Appointment> getReminderAppointmentsByUser(int userId) throws DAOException {
        final Instant start = Instant.now();
        return appointmentDAO.getAppointmentsByUserBetween(userId, start, start.plus(15L, ChronoUnit.MINUTES));
    }
}
