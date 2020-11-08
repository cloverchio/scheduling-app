package com.c195.dao;

import com.c195.model.Appointment;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    private static final String ALL_APPOINTMENTS_SQL = "" +
            "SELECT * " +
            "FROM appointment ap " +
            "JOIN customer cu " +
            "ON ap.customerId = cu.customerId " +
            "JOIN address a " +
            "ON cu.addressId = a.addressId " +
            "JOIN city ci " +
            "ON a.cityId = ci.cityId " +
            "JOIN country co " +
            "ON ci.countryId = co.countryId " +
            "JOIN user us " +
            "ON ap.userId = us.userId";

    private static final String APPOINTMENTS_BY_USER_BETWEEN_SQL = "" +
            "SELECT * " +
            "FROM appointment ap " +
            "JOIN customer cu " +
            "ON ap.customerId = cu.customerId " +
            "JOIN address a " +
            "ON cu.addressId = a.addressId " +
            "JOIN city ci " +
            "ON a.cityId = ci.cityId " +
            "JOIN country co " +
            "on ci.countryId = co.countryId " +
            "JOIN user us " +
            "ON ap.userId = us.userId " +
            "WHERE ap.userId = ? " +
            "AND ap.start BETWEEN ? AND ?";

    private static final String APPOINTMENTS_OVERLAP_BY_USER_SQL = "" +
            "SELECT * " +
            "FROM appointment ap " +
            "JOIN customer cu " +
            "ON ap.customerId = cu.customerId " +
            "JOIN address a " +
            "ON cu.addressId = a.addressId " +
            "JOIN city ci " +
            "ON a.cityId = ci.cityId " +
            "JOIN country co " +
            "on ci.countryId = co.countryId " +
            "JOIN user us " +
            "ON ap.userId = us.userId " +
            "WHERE ap.userId = ? " +
            "AND (ap.start BETWEEN ? AND ? " +
            "OR ap.end BETWEEN ? AND ? " +
            "OR ? BETWEEN ap.start AND ap.end)";

    private static final String APPOINTMENTS_BY_USER_AFTER_SQL = "" +
            "SELECT * " +
            "FROM appointment ap " +
            "JOIN customer cu " +
            "ON ap.customerId = cu.customerId " +
            "JOIN address a " +
            "ON cu.addressId = a.addressId " +
            "JOIN city ci " +
            "ON a.cityId = ci.cityId " +
            "JOIN country co " +
            "on ci.countryId = co.countryId " +
            "JOIN user us " +
            "ON ap.userId = us.userId " +
            "WHERE ap.userId = ? " +
            "AND ap.start >= ?";

    private static final String SAVE_APPOINTMENTS_SQL = "" +
            "INSERT INTO appointment " +
            "(customerId, userId, title, description, location, " +
            "contact, type, url, start, end, createDate, createdBy, lastUpdateBy) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

    public List<Appointment> getAllAppointments() throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(ALL_APPOINTMENTS_SQL)) {
            final List<Appointment> appointments = new ArrayList<>();
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                appointments.add(toAppointment(resultSet));
            }
            return appointments;
        } catch (SQLException e) {
            throw new DAOException("There was an issue retrieving appointments", e);
        }
    }

    public List<Appointment> getAppointmentsByUserBetween(int userId, Instant start, Instant end) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(APPOINTMENTS_BY_USER_BETWEEN_SQL)) {
            statement.setInt(1, userId);
            statement.setTimestamp(2, Timestamp.from(start));
            statement.setTimestamp(3, Timestamp.from(end));
            final List<Appointment> appointments = new ArrayList<>();
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                appointments.add(toAppointment(resultSet));
            }
            return appointments;
        } catch (SQLException e) {
            throw new DAOException("There was an issue retrieving appointments", e);
        }
    }

    public List<Appointment> getOverlappingAppointmentsByUser(int userId, Instant start, Instant end) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(APPOINTMENTS_OVERLAP_BY_USER_SQL)) {
            final Timestamp startTime = Timestamp.from(start);
            final Timestamp endTime = Timestamp.from(end);
            statement.setInt(1, userId);
            statement.setTimestamp(2, startTime);
            statement.setTimestamp(3, endTime);
            statement.setTimestamp(4, startTime);
            statement.setTimestamp(5, endTime);
            statement.setTimestamp(6, startTime);
            final List<Appointment> appointments = new ArrayList<>();
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                appointments.add(toAppointment(resultSet));
            }
            return appointments;
        } catch (SQLException e) {
            throw new DAOException("There was an issue retrieving appointments", e);
        }
    }

    public List<Appointment> getAppointmentsByUserAfter(int userId, Instant start) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(APPOINTMENTS_BY_USER_AFTER_SQL)) {
            statement.setInt(1, userId);
            statement.setTimestamp(2, Timestamp.from(start));
            final List<Appointment> appointments = new ArrayList<>();
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                appointments.add(toAppointment(resultSet));
            }
            return appointments;
        } catch (SQLException e) {
            throw new DAOException("There was an issue retrieving appointments", e);
        }
    }

    public void saveAppointment(Appointment appointment) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(SAVE_APPOINTMENTS_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, appointment.getCustomer().getId());
            statement.setInt(2, appointment.getUser().getId());
            statement.setString(3, appointment.getTitle());
            statement.setString(4, appointment.getDescription());
            statement.setString(5, appointment.getLocation());
            statement.setString(6, appointment.getContact());
            statement.setString(7, appointment.getType());
            statement.setString(8, appointment.getUrl());
            statement.setTimestamp(9, Timestamp.from(appointment.getStart()));
            statement.setTimestamp(10, Timestamp.from(appointment.getEnd()));
            statement.setTimestamp(11, Timestamp.from(appointment.getMetadata().getCreatedDate()));
            statement.setString(12, appointment.getMetadata().getCreatedBy().toLowerCase());
            statement.setString(13, appointment.getMetadata().getUpdatedBy().toLowerCase());
            statement.executeUpdate();
            final ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                appointment.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("There was an issue saving an appointment", e);
        }
    }

    public void updateAppointment(Appointment appointment) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(UPDATE_APPOINTMENTS_SQL)) {
            statement.setInt(1, appointment.getCustomer().getId());
            statement.setInt(2, appointment.getUser().getId());
            statement.setString(3, appointment.getTitle());
            statement.setString(4, appointment.getDescription());
            statement.setString(5, appointment.getLocation());
            statement.setString(6, appointment.getContact());
            statement.setString(7, appointment.getType());
            statement.setString(8, appointment.getUrl());
            statement.setTimestamp(9, Timestamp.from(appointment.getStart()));
            statement.setTimestamp(10, Timestamp.from(appointment.getEnd()));
            statement.setTimestamp(11, Timestamp.from(appointment.getMetadata().getUpdatedDate()));
            statement.setString(12, appointment.getMetadata().getUpdatedBy().toLowerCase());
            statement.setInt(13, appointment.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("There was an issue updating an appointment", e);
        }
    }

    public void deleteAppointmentById(int id) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(DELETE_APPOINTMENT_BY_ID_SQL)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("There was an issue removing an appointment", e);
        }
    }

    public static Appointment toAppointment(ResultSet resultSet) throws DAOException {
        try {
            final Appointment appointment = new Appointment();
            appointment.setId(resultSet.getInt("appointmentId"));
            appointment.setTitle(resultSet.getString("title"));
            appointment.setDescription(resultSet.getString("description"));
            appointment.setLocation(resultSet.getString("location"));
            appointment.setContact(resultSet.getString("contact"));
            appointment.setType(resultSet.getString("type"));
            appointment.setUrl(resultSet.getString("url"));
            appointment.setStart(resultSet.getTimestamp("start").toInstant());
            appointment.setEnd(resultSet.getTimestamp("end").toInstant());
            appointment.setCustomer(CustomerDAO.toCustomer(resultSet));
            appointment.setUser(UserDAO.toUser(resultSet));
            appointment.setMetadata(MetadataDAO.toMetadata(resultSet));
            return appointment;
        } catch (SQLException e) {
            throw new DAOException("There was an issue creating user data", e);
        }
    }
}
