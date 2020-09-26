package com.c195.dao;

import com.c195.model.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddressDAO {

    private static final String ADDRESS_BY_ID_SQL = "" +
            "SELECT * " +
            "FROM address a " +
            "JOIN city ci " +
            "ON a.cityId = ci.cityId " +
            "JOIN country co " +
            "on ci.countryId = co.countryId " +
            "WHERE addressId = ?";

    private static final String ALL_ADDRESSES_SQL = "" +
            "SELECT * " +
            "FROM address a" +
            "JOIN city ci " +
            "ON a.cityId = ci.cityId " +
            "JOIN country co " +
            "on ci.countryId = co.countryId";

    private static final String SAVE_ADDRESS_SQL = "" +
            "INSERT INTO address " +
            "(addressId, address, address2, cityId, postalCode, phone, createDate, createdBy) " +
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

    private static final String DELETE_ADDRESS_BY_ID_SQL = "" +
            "DELETE FROM address " +
            "WHERE addressId = ?";

    private final Connection connection;

    public AddressDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<Address> getAddressById(int id) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(ADDRESS_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toAddress(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving an address", e);
        }
    }

    public List<Address> getAllAddresses() throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(ALL_ADDRESSES_SQL)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<Address> addresses = new ArrayList<>();
            while (resultSet.next()) {
                addresses.add(toAddress(resultSet));
            }
            return addresses;
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving addresses", e);
        }
    }

    public void saveAddress(Address address) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(SAVE_ADDRESS_SQL)) {
            preparedStatement.setInt(1, address.getId());
            preparedStatement.setString(2, address.getAddress());
            preparedStatement.setString(3, address.getAddress2());
            preparedStatement.setInt(4, address.getCity().getId());
            preparedStatement.setString(5, address.getPostalCode());
            preparedStatement.setString(6, address.getPhone());
            preparedStatement.setTimestamp(7, address.getMetadata().getCreatedDate());
            preparedStatement.setString(8, address.getMetadata().getCreatedBy());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue saving an address", e);
        }
    }

    public void updateAddress(Address address) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ADDRESS_SQL)) {
            preparedStatement.setString(1, address.getAddress());
            preparedStatement.setString(2, address.getAddress2());
            preparedStatement.setInt(3, address.getCity().getId());
            preparedStatement.setString(4, address.getPostalCode());
            preparedStatement.setString(5, address.getPhone());
            preparedStatement.setTimestamp(6, address.getMetadata().getUpdatedDate());
            preparedStatement.setString(7, address.getMetadata().getUpdatedBy());
            preparedStatement.setInt(8, address.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue updating an address", e);
        }
    }

    public void deleteAddressById(int id) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ADDRESS_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue removing an address", e);
        }
    }

    public static Address toAddress(ResultSet resultSet) throws DAOException {
        try {
            return new Address.Builder()
                    .withId(resultSet.getInt("addressId"))
                    .withAddress(resultSet.getString("address"))
                    .withAddress2(resultSet.getString("address2"))
                    .withPostalCode(resultSet.getString("postalCode"))
                    .withPhone(resultSet.getString("phone"))
                    .withCity(CityDAO.toCity(resultSet))
                    .withMetadata(MetadataDAO.toMetadata(resultSet))
                    .build();
        } catch (SQLException e) {
            throw new DAOException("there was an issue creating address data", e);
        }
    }
}
