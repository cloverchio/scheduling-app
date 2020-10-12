package com.c195.dao;

import com.c195.model.Customer;

import java.sql.*;
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
            "WHERE cu.customerId = ?";

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
            "(customerName, addressId, active, createDate, createdBy, lastUpdateBy) " +
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
            "DELETE cu, a " +
            "FROM customer cu " +
            "JOIN address a " +
            "ON cu.addressId = a.addressId " +
            "WHERE customerId = ?";

    private static CustomerDAO daoInstance;
    private final Connection connection;

    private CustomerDAO(Connection connection) {
        this.connection = connection;
    }

    public static CustomerDAO getInstance(Connection connection) {
        return Optional.ofNullable(daoInstance)
                .orElseGet(() -> {
                    daoInstance = new CustomerDAO(connection);
                    return daoInstance;
                });
    }

    public Optional<Customer> getCustomerById(int id) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(CUSTOMER_BY_ID_SQL)) {
            statement.setInt(1, id);
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toCustomer(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving a customer", e);
        }
    }

    public List<Customer> getAllCustomers() throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(ALL_CUSTOMERS_SQL)) {
            final List<Customer> customers = new ArrayList<>();
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                customers.add(toCustomer(resultSet));
            }
            return customers;
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving customers", e);
        }
    }

    public void saveCustomer(Customer customer) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(SAVE_CUSTOMER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, customer.getName().toLowerCase());
            statement.setInt(2, customer.getAddress().getId());
            statement.setBoolean(3, customer.isActive());
            statement.setTimestamp(4, Timestamp.from(customer.getMetadata().getCreatedDate()));
            statement.setString(5, customer.getMetadata().getCreatedBy().toLowerCase());
            statement.setString(6, customer.getMetadata().getUpdatedBy().toLowerCase());
            statement.executeUpdate();
            final ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                customer.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("there was an issue saving a customer", e);
        }
    }

    public void updateCustomer(Customer customer) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(UPDATE_CUSTOMER_SQL)) {
            statement.setString(1, customer.getName().toLowerCase());
            statement.setInt(2, customer.getAddress().getId());
            statement.setBoolean(3, customer.isActive());
            statement.setTimestamp(4, Timestamp.from(customer.getMetadata().getUpdatedDate()));
            statement.setString(5, customer.getMetadata().getUpdatedBy().toLowerCase());
            statement.setInt(6, customer.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue updating a customer", e);
        }
    }

    public void deleteCustomerById(int id) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(DELETE_CUSTOMER_BY_ID_SQL)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue removing a customer", e);
        }
    }

    public static Customer toCustomer(ResultSet resultSet) throws DAOException {
        try {
            final Customer customer = new Customer();
            customer.setId(resultSet.getInt("customerId"));
            customer.setName(resultSet.getString("customerName"));
            customer.setActive(resultSet.getBoolean("active"));
            customer.setAddress(AddressDAO.toAddress(resultSet));
            customer.setMetadata(MetadataDAO.toMetadata(resultSet));
            return customer;
        } catch (SQLException e) {
            throw new DAOException("there was an issue creating customer data", e);
        }
    }
}
