package com.c195.service;

import com.c195.common.AppointmentDTO;
import com.c195.common.AppointmentType;
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
     * Gets a list of all future appointments for a given user.
     *
     * @param userId in which to retrieve future appoints for.
     * @return list of appointments that have a start date greater than now.
     * @throws DAOException if there are issues retrieving appointments from the db.
     */
    public List<AppointmentDTO> getAllUpcomingAppointmentsByUser(int userId) throws DAOException {
        return appointmentDAO.getAppointmentsByUserAfter(userId, Instant.now()).stream()
                .map(AppointmentService::toAppointmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of appointments that will occur within the week for a given user.
     *
     * @param userId in which to retrieve weekly appointments for.
     * @return list of appointments within the week.
     * @throws DAOException if there are issues retrieving appointments from the db.
     */
    public List<AppointmentDTO> getWeeklyAppointmentsByUser(int userId) throws DAOException {
        final Instant start = Instant.now();
        final int dayOfTheWeek = start.atZone(ZoneId.systemDefault()).getDayOfWeek().getValue();
        return appointmentDAO.getAppointmentsByUserBetween(userId, start, start.plus(7 - dayOfTheWeek, ChronoUnit.DAYS)).stream()
                .map(AppointmentService::toAppointmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of appointments that occur with the month for a given user.
     *
     * @param userId in which to retrieve monthly appointments for.
     * @return list of appointments within the month.
     * @throws DAOException if there are issues retrieving appointments from the db.
     */
    public List<AppointmentDTO> getMonthlyAppointmentsByUser(int userId) throws DAOException {
        final Instant start = Instant.now();
        final ZonedDateTime zonedStart = start.atZone(ZoneId.systemDefault());
        final int dayOfTheMonth = zonedStart.getDayOfMonth();
        final int lengthOfMonth = Month.from(zonedStart).length(zonedStart.toLocalDate().isLeapYear());
        return appointmentDAO.getAppointmentsByUserBetween(userId, start, start.plus(lengthOfMonth - dayOfTheMonth, ChronoUnit.DAYS)).stream()
                .map(AppointmentService::toAppointmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of appointments that will occur within the next 15 minutes for
     * a given user.
     *
     * @param userId in which to retrieve upcoming appointments for.
     * @return list of appointments that are quickly approaching...
     * @throws DAOException if there are issues retrieving appointments from the db.
     */
    public List<AppointmentDTO> getReminderAppointmentsByUser(int userId) throws DAOException {
        final Instant start = Instant.now();
        return appointmentDAO.getAppointmentsByUserBetween(userId, start, start.plus(15L, ChronoUnit.MINUTES)).stream()
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
        final Appointment appointment = new Appointment();
        appointment.setId(appointmentDTO.getId());
        appointment.setStart(appointmentDTO.getStart());
        appointment.setEnd(appointmentDTO.getEnd());
        appointment.setLocation(appointmentDTO.getLocation());
        appointment.setTitle(appointmentDTO.getTitle());
        appointment.setDescription(appointmentDTO.getDescription());
        appointment.setContact(appointmentDTO.getContact());
        appointment.setType(appointmentDTO.getType().toString());
        appointment.setCustomer(CustomerService.toCustomer(appointmentDTO.getCustomerDTO()));
        appointment.setUser(UserService.toUser(appointmentDTO.getUserDTO()));
        return appointment;
    }

    public static AppointmentDTO toAppointmentDTO(Appointment appointment) {
        return new AppointmentDTO.Builder()
                .withId(appointment.getId())
                .withDescription(appointment.getDescription())
                .withLocation(appointment.getLocation())
                .withTitle(appointment.getTitle())
                .withType(AppointmentType.valueOf(appointment.getType()))
                .withUrl(appointment.getUrl())
                .withStart(appointment.getStart())
                .withEnd(appointment.getEnd())
                .withContact(appointment.getContact())
                .withCustomerDTO(CustomerService.toCustomerDTO(appointment.getCustomer()))
                .withUserDTO(UserService.toUserDTO(appointment.getUser()))
                .build();
    }
}
