package com.c195.service;

import com.c195.common.*;
import com.c195.dao.AppointmentDAO;
import com.c195.dao.DAOException;
import com.c195.dao.MetadataDAO;
import com.c195.model.Appointment;

import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AppointmentService {

    private static final ZoneId zoneId = ZoneId.of("UTC");

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
     * Gets a list of appointments that will occur within the week for a given user.
     *
     * @param userId in which to retrieve weekly appointments for.
     * @return list of appointments within the week.
     * @throws DAOException if there are issues retrieving appointments from the db.
     * @throws AppointmentException if there are issues with the appointment time.
     */
    public List<AppointmentDTO> getWeeklyAppointmentsByUser(int userId) throws DAOException, AppointmentException {
        final Instant start = Instant.now();
        final int dayOfTheWeek = start.atZone(zoneId).getDayOfWeek().getValue();
        return getAppointmentsByUserBetween(userId, start, start.plus(7 - dayOfTheWeek, ChronoUnit.DAYS));
    }

    /**
     * Gets a list of appointments that occur with the month for a given user.
     *
     * @param userId in which to retrieve monthly appointments for.
     * @return list of appointments within the month.
     * @throws DAOException if there are issues retrieving appointments from the db.
     * @throws AppointmentException if there are issues with the appointment time.
     */
    public List<AppointmentDTO> getMonthlyAppointmentsByUser(int userId) throws DAOException, AppointmentException {
        final Instant start = Instant.now();
        final ZonedDateTime zonedStart = start.atZone(zoneId);
        final int dayOfTheMonth = zonedStart.getDayOfMonth();
        final int lengthOfMonth = Month.from(zonedStart).length(zonedStart.toLocalDate().isLeapYear());
        return getAppointmentsByUserBetween(userId, start, start.plus(lengthOfMonth - dayOfTheMonth, ChronoUnit.DAYS));
    }

    /**
     * Gets a list of appointments that will occur within the next 15 minutes for
     * a given user.
     *
     * @param userId in which to retrieve upcoming appointments for.
     * @return list of appointments that are quickly approaching...
     * @throws DAOException if there are issues retrieving appointments from the db.
     * @throws AppointmentException if there are issues with the appointment time.
     */
    public List<AppointmentDTO> getReminderAppointmentsByUser(int userId) throws DAOException, AppointmentException {
        final Instant start = Instant.now();
        return getAppointmentsByUserBetween(userId, start, start.plus(15L, ChronoUnit.MINUTES));
    }

    /**
     * Gets a list of all future appointments for a given user.
     *
     * @param userId in which to retrieve future appoints for.
     * @return list of appointments that have a start date greater than now.
     * @throws DAOException if there are issues retrieving appointments from the db.
     * @throws AppointmentException if there are issues with the appointment time.
     */
    public List<AppointmentDTO> getUpcomingAppointmentsByUser(int userId) throws DAOException, AppointmentException {
        return appointmentDAO.getAppointmentsByUserAfter(userId, Instant.now()).stream()
                .map(AppointmentService::toAppointmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of appointments for a given user that will occur within the interval.
     *
     * @param userId in which to retrieve appointments for.
     * @param start  the start of the interval.
     * @param end    the end of the interval.
     * @return a list of appointments between start and end for the given user.
     * @throws DAOException if there are issues retrieving appointments from the db.
     * @throws AppointmentException if there are issues with the appointment time.
     */
    public List<AppointmentDTO> getAppointmentsByUserBetween(int userId, Instant start, Instant end) throws DAOException, AppointmentException {
        return appointmentDAO.getAppointmentsByUserBetween(userId, start, end).stream()
                .map(AppointmentService::toAppointmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Saves the appointment.
     *
     * @param appointmentDTO appointment information in which to save.
     * @param currentUser    the user initiating the save.
     * @return the id of the saved appointment.
     * @throws DAOException if there are issues saving the appointment to the db.
     */
    public Integer saveAppointment(AppointmentDTO appointmentDTO, String currentUser) throws DAOException {
        final Appointment appointment = toAppointment(appointmentDTO);
        appointment.setMetadata(MetadataDAO.getSaveMetadata(currentUser));
        appointmentDAO.saveAppointment(appointment);
        return appointment.getId();
    }

    /**
     * Updates the appointment.
     *
     * @param appointmentDTO appointment information in which to update.
     * @param currentUser    the user initiating the update.
     * @return the id of the updated appointment.
     * @throws DAOException if there are issues updating the appointment in the db.
     */
    public Integer updateAppointment(AppointmentDTO appointmentDTO, String currentUser) throws DAOException {
        final Appointment appointment = toAppointment(appointmentDTO);
        appointment.setMetadata(MetadataDAO.getUpdateMetadata(currentUser));
        appointmentDAO.updateAppointment(appointment);
        return appointment.getId();
    }

    /**
     * Deletes the appointment.
     *
     * @param appointmentId corresponding to the appointment to be deleted.
     * @throws DAOException if there are issues deleting the appointment from the db.
     */
    public void deleteAppointment(int appointmentId) throws DAOException {
        appointmentDAO.deleteAppointmentById(appointmentId);
    }

    public static Appointment toAppointment(AppointmentDTO appointmentDTO) {
        final AppointmentTime appointmentTime = appointmentDTO.getTime();
        final Appointment appointment = new Appointment();
        appointment.setId(appointmentDTO.getId());
        appointment.setLocation(appointmentDTO.getLocation().getName());
        appointment.setTitle(appointmentDTO.getTitle());
        appointment.setDescription(appointmentDTO.getDescription());
        appointment.setContact(appointmentDTO.getContact());
        appointment.setStart(appointmentTime.getUtcStart());
        appointment.setEnd(appointmentTime.getUtcEnd());
        appointment.setType(appointmentDTO.getType().toString());
        appointment.setCustomer(CustomerService.toCustomer(appointmentDTO.getCustomerDTO()));
        return appointment;
    }

    public static AppointmentDTO toAppointmentDTO(Appointment appointment) throws AppointmentException {
        final AppointmentType type = AppointmentType.valueOf(appointment.getType());
        final AppointmentLocation location = AppointmentLocation.valueOf(appointment.getLocation());
        final AppointmentTime time = new AppointmentTime(appointment.getStart(), appointment.getEnd(), location.getZoneId());
        final CustomerDTO customer = CustomerService.toCustomerDTO(appointment.getCustomer());
        return new AppointmentDTO.Builder()
                .withId(appointment.getId())
                .withDescription(appointment.getDescription())
                .withTitle(appointment.getTitle())
                .withUrl(appointment.getUrl())
                .withContact(appointment.getContact())
                .withType(type)
                .withLocation(location)
                .withTime(time)
                .withCustomerDTO(customer)
                .build();
    }
}
