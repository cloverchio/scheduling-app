package com.c195.dao;

import com.c195.model.Address;

import java.sql.*;
import java.util.Optional;

public class AddressDAO {

    private static final String ADDRESS_SQL = "" +
            "SELECT * " +
            "FROM address a " +
            "JOIN city ci " +
            "ON a.cityId = ci.cityId " +
            "JOIN country co " +
            "ON ci.countryId = co.countryId " +
            "WHERE address = ?";

    private static final String SAVE_ADDRESS_SQL = "" +
            "INSERT INTO address " +
            "(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_ADDRESS_SQL = "" +
            "UPDATE address " +
            "SET address = ?, " +
            "address2 = ?, " +
            "cityId = ?, " +
            "postalCode = ?, " +
            "phone = ?, " +
            "lastUpdate = ?, " +
            "lastUpdateBy = ? " +
            "WHERE addressId = ?";

    private static AddressDAO daoInstance;
    private final Connection connection;

    private AddressDAO(Connection connection) {
        this.connection = connection;
    }

    public static AddressDAO getInstance(Connection connection) {
        if (daoInstance == null) {
            daoInstance = new AddressDAO(connection);
        }
        return daoInstance;
    }

    public Optional<Address> getAddress(String address) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(ADDRESS_SQL)) {
            statement.setString(1, address);
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toAddress(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("There was an issue retrieving an address", e);
        }
    }

    public void saveAddress(Address address) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(SAVE_ADDRESS_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, address.getAddress().toLowerCase());
            statement.setString(2, address.getAddress2().toLowerCase());
            statement.setInt(3, address.getCity().getId());
            statement.setString(4, address.getPostalCode().toUpperCase());
            statement.setString(5, address.getPhone().toLowerCase());
            statement.setTimestamp(6, Timestamp.from(address.getMetadata().getCreatedDate()));
            statement.setString(7, address.getMetadata().getCreatedBy().toLowerCase());
            statement.setString(8, address.getMetadata().getUpdatedBy().toLowerCase());
            statement.executeUpdate();
            final ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                address.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("There was an issue saving an address", e);
        }
    }

    public void updateAddress(Address address) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(UPDATE_ADDRESS_SQL)) {
            statement.setString(1, address.getAddress());
            statement.setString(2, address.getAddress2());
            statement.setInt(3, address.getCity().getId());
            statement.setString(4, address.getPostalCode());
            statement.setString(5, address.getPhone());
            statement.setTimestamp(6, Timestamp.from(address.getMetadata().getUpdatedDate()));
            statement.setString(7, address.getMetadata().getUpdatedBy().toLowerCase());
            statement.setInt(8, address.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("There was an issue updating an address", e);
        }
    }

    public static Address toAddress(ResultSet resultSet) throws DAOException {
        try {
            final Address address = new Address();
            address.setId(resultSet.getInt("addressId"));
            address.setAddress(resultSet.getString("address"));
            address.setAddress2(resultSet.getString("address2"));
            address.setPostalCode(resultSet.getString("postalCode"));
            address.setPhone(resultSet.getString("phone"));
            address.setCity(CityDAO.toCity(resultSet));
            address.setMetadata(MetadataDAO.toMetadata(resultSet));
            return address;
        } catch (SQLException e) {
            throw new DAOException("There was an issue creating address data", e);
        }
    }
}
