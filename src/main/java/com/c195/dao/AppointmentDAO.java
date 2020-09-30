package com.c195.dao;

import com.c195.model.Appointment;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentDAO {

    private static final String TIME_ZONE = "UTC";

    private static final String APPOINTMENT_BY_ID_SQL = "" +
            "SELECT * " +
            "FROM appointment ap " +
            "JOIN customer cu " +
            "ON ap.customerId = ap.customerId " +
            "JOIN user us " +
            "ON ap.userId = us.userId " +
            "WHERE ap.appointmentId = ?";

    private static final String ALL_APPOINTMENTS_SQL = "" +
            "SELECT * " +
            "FROM appointment ap " +
            "JOIN customer cu " +
            "ON ap.customerId = ap.customerId " +
            "JOIN user us " +
            "ON ap.userId = us.userId";

    private static final String APPOINTMENTS_BY_USER_BETWEEN_SQL = "" +
            "SELECT * " +
            "FROM appointment ap " +
            "JOIN customer cu " +
            "ON ap.customerId = ap.customerId " +
            "JOIN user us " +
            "ON ap.userId = us.userId " +
            "WHERE ap.userId = ? " +
            "AND ap.start BETWEEN ? AND ?";

    private static final String SAVE_APPOINTMENTS_SQL = "" +
            "INSERT INTO appointment " +
            "(customerId, userId, title, description, location, " +
            "contact, type, url, start, end, createDate, createdBy, lastUpdateBy) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_APPOINTMENTS_SQL = "" +
            "UPDATE appointment " +
            "SET customerId = ?, " +
            "userId = ?, " +
            "title = ?, " +
            "description = ?, " +
            "location = ?, " +
            "contact = ?, " +
            "type = ?, " +
            "url = ?, " +
            "start = ?, " +
            "end = ?, " +
            "lastUpdate = ?, " +
            "lastUpdateBy = ? " +
            "WHERE appointmentId = ?";

    private static final String DELETE_APPOINTMENT_BY_ID_SQL = "" +
            "DELETE FROM appointment " +
            "WHERE appointmentId = ?";

    private static AppointmentDAO daoInstance;
    private final Connection connection;

    private AppointmentDAO(Connection connection) {
        this.connection = connection;
    }

    public static AppointmentDAO getInstance(Connection connection) {
        if (daoInstance == null) {
            daoInstance = new AppointmentDAO(connection);
        }
        return daoInstance;
    }

    public Optional<Appointment> getAppointmentById(int id) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(APPOINTMENT_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toAppointment(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving an appointment", e);
        }
    }

    public List<Appointment> getAllAppointments() throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(ALL_APPOINTMENTS_SQL)) {
            final List<Appointment> appointments = new ArrayList<>();
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                appointments.add(toAppointment(resultSet));
            }
            return appointments;
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving appointments", e);
        }
    }

    public List<Appointment> getAppointmentsByUserBetween(int userId, Instant start, Instant end) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(APPOINTMENTS_BY_USER_BETWEEN_SQL)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setTimestamp(2, Timestamp.from(start));
            preparedStatement.setTimestamp(3, Timestamp.from(end));
            final List<Appointment> appointments = new ArrayList<>();
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                appointments.add(toAppointment(resultSet));
            }
            return appointments;
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving appointments", e);
        }
    }

    public void saveAppointment(Appointment appointment) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(SAVE_APPOINTMENTS_SQL)) {
            preparedStatement.setInt(1, appointment.getCustomer().getId());
            preparedStatement.setInt(2, appointment.getUser().getId());
            preparedStatement.setString(3, appointment.getTitle());
            preparedStatement.setString(4, appointment.getDescription());
            preparedStatement.setString(5, appointment.getLocation());
            preparedStatement.setString(6, appointment.getContact());
            preparedStatement.setString(7, appointment.getType());
            preparedStatement.setString(8, appointment.getUrl());
            preparedStatement.setObject(9, appointment.getStart());
            preparedStatement.setTimestamp(10, Timestamp.from(appointment.getEnd()));
            preparedStatement.setTimestamp(11, Timestamp.from(appointment.getMetadata().getCreatedDate()));
            preparedStatement.setString(12, appointment.getMetadata().getCreatedBy());
            preparedStatement.setString(13, appointment.getMetadata().getUpdatedBy());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue saving an appointment", e);
        }
    }

    public void updateAppointment(Appointment appointment) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_APPOINTMENTS_SQL)) {
            preparedStatement.setInt(1, appointment.getCustomer().getId());
            preparedStatement.setInt(2, appointment.getUser().getId());
            preparedStatement.setString(3, appointment.getTitle());
            preparedStatement.setString(4, appointment.getDescription());
            preparedStatement.setString(5, appointment.getLocation());
            preparedStatement.setString(6, appointment.getContact());
            preparedStatement.setString(7, appointment.getType());
            preparedStatement.setString(8, appointment.getUrl());
            preparedStatement.setTimestamp(9, Timestamp.from(appointment.getStart()));
            preparedStatement.setTimestamp(10, Timestamp.from(appointment.getEnd()));
            preparedStatement.setTimestamp(11, Timestamp.from(appointment.getMetadata().getUpdatedDate()));
            preparedStatement.setString(12, appointment.getMetadata().getUpdatedBy());
            preparedStatement.setInt(13, appointment.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue updating an appointment", e);
        }
    }

    public void deleteAppointmentById(int id) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_APPOINTMENT_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue removing an appointment", e);
        }
    }

    public static Appointment toAppointment(ResultSet resultSet) throws DAOException {
        try {
            return new Appointment.Builder()
                    .withId(resultSet.getInt("appointmentId"))
                    .withTitle(resultSet.getString("title"))
                    .withDescription(resultSet.getString("description"))
                    .withLocation(resultSet.getString("location"))
                    .withContact(resultSet.getString("contact"))
                    .withType(resultSet.getString("type"))
                    .withUrl(resultSet.getString("url"))
                    .withStart(resultSet.getTimestamp("start").toInstant())
                    .withEnd(resultSet.getTimestamp("end").toInstant())
                    .withCustomer(CustomerDAO.toCustomer(resultSet))
                    .withUser(UserDAO.toUser(resultSet))
                    .withMetadata(MetadataDAO.toMetadata(resultSet))
                    .build();
        } catch (SQLException e) {
            throw new DAOException("there was an issue creating user data", e);
        }
    }
}
