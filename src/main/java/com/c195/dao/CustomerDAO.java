package com.c195.dao;

import com.c195.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDAO {

    private static final String CUSTOMER_BY_ID_SQL = "" +
            "SELECT * " +
            "FROM customer cu" +
            "JOIN address a " +
            "ON cu.addressId = a.addressId " +
            "JOIN city ci " +
            "ON a.cityId = ci.cityId " +
            "JOIN country co " +
            "on ci.countryId = co.countryId " +
            "WHERE customerId = ?";

    private static final String ALL_CUSTOMERS_SQL = "" +
            "SELECT * " +
            "FROM customer cu" +
            "JOIN address a " +
            "ON cu.addressId = a.addressId " +
            "JOIN city ci " +
            "ON a.cityId = ci.cityId " +
            "JOIN country co " +
            "on ci.countryId = co.countryId";

    private static final String SAVE_CUSTOMER_SQL = "" +
            "INSERT INTO customer " +
            "(customerId, customerName, addressId, active, createDate, createdBy) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_CUSTOMER_SQL = "" +
            "UPDATE customer " +
            "SET customerName = ?, " +
            "addressId = ?, " +
            "active = ?, " +
            "lastUpdate = ?, " +
            "lastUpdateBy = ? " +
            "WHERE customerId = ?";

    private static final String DELETE_CUSTOMER_BY_ID_SQL = "" +
            "DELETE FROM customer " +
            "WHERE customerId = ?";

    private final Connection connection;

    public CustomerDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<Customer> getCustomerById(int id) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CUSTOMER_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toCustomer(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving a customer", e);
        }
    }

    public List<Customer> getAllCustomers() throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(ALL_CUSTOMERS_SQL)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<Customer> customers = new ArrayList<>();
            while (resultSet.next()) {
                customers.add(toCustomer(resultSet));
            }
            return customers;
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving customers", e);
        }
    }

    public void saveCustomer(Customer customer) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(SAVE_CUSTOMER_SQL)) {
            preparedStatement.setInt(1, customer.getId());
            preparedStatement.setString(2, customer.getName());
            preparedStatement.setInt(3, customer.getAddress().getId());
            preparedStatement.setBoolean(4, customer.isActive());
            preparedStatement.setTimestamp(5, customer.getMetadata().getCreatedDate());
            preparedStatement.setString(6, customer.getMetadata().getCreatedBy());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue saving a customer", e);
        }
    }

    public void updateCustomer(Customer customer) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CUSTOMER_SQL)) {
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setInt(2, customer.getAddress().getId());
            preparedStatement.setBoolean(3, customer.isActive());
            preparedStatement.setTimestamp(4, customer.getMetadata().getUpdatedDate());
            preparedStatement.setString(5, customer.getMetadata().getUpdatedBy());
            preparedStatement.setInt(6, customer.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue updating a customer", e);
        }
    }

    public void deleteCustomerById(int id) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CUSTOMER_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue removing a customer", e);
        }
    }

    public static Customer toCustomer(ResultSet resultSet) throws DAOException {
        try {
            return new Customer.Builder()
                    .withId(resultSet.getInt("customerId"))
                    .withName(resultSet.getString("customerName"))
                    .withActive(resultSet.getBoolean("active"))
                    .withAddress(AddressDAO.toAddress(resultSet))
                    .withMetadata(MetadataDAO.toMetadata(resultSet))
                    .build();
        } catch (SQLException e) {
            throw new DAOException("there was an issue creating customer data", e);
        }
    }
}
