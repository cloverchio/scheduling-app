package com.c195.dao;

import com.c195.model.Country;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CountryDAO {

    private static final String COUNTRY_BY_ID_SQL = "" +
            "SELECT * " +
            "FROM country " +
            "WHERE countryId = ?";

    private static final String ALL_COUNTRIES_SQL = "" +
            "SELECT * " +
            "FROM country";

    private static final String SAVE_COUNTRY_SQL = "" +
            "INSERT INTO country " +
            "(countryId, country, createDate, createdBy) " +
            "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_COUNTRY_SQL = "" +
            "UPDATE country " +
            "SET country = ?, " +
            "lastUpdate = ?, " +
            "lastUpdateBy = ? " +
            "WHERE countryId = ?";

    private static final String DELETE_COUNTRY_BY_ID_SQL = "" +
            "DELETE FROM country " +
            "WHERE countryId = ?";

    private final Connection connection;

    public CountryDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<Country> getCountryById(int id) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(COUNTRY_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toCountry(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving a country", e);
        }
    }

    public List<Country> getAllCountries() throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(ALL_COUNTRIES_SQL)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<Country> countries = new ArrayList<>();
            while (resultSet.next()) {
                countries.add(toCountry(resultSet));
            }
            return countries;
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving countries", e);
        }
    }

    public void saveCountry(Country country) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(SAVE_COUNTRY_SQL)) {
            preparedStatement.setInt(1, country.getId());
            preparedStatement.setString(2, country.getCountry());
            preparedStatement.setTimestamp(3, country.getMetadata().getCreatedDate());
            preparedStatement.setString(4, country.getMetadata().getCreatedBy());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue saving a country", e);
        }
    }

    public void updateCountry(Country country) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_COUNTRY_SQL)) {
            preparedStatement.setString(1, country.getCountry());
            preparedStatement.setTimestamp(2, country.getMetadata().getUpdatedDate());
            preparedStatement.setString(3, country.getMetadata().getUpdatedBy());
            preparedStatement.setInt(4, country.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue updating a country", e);
        }
    }

    public void deleteCountryById(int id) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_COUNTRY_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue removing a country", e);
        }
    }

    public static Country toCountry(ResultSet resultSet) throws DAOException {
        try {
            return new Country.Builder()
                    .withId(resultSet.getInt("countryId"))
                    .withCountry(resultSet.getString("country"))
                    .withMetadata(MetadataDAO.toMetadata(resultSet))
                    .build();
        } catch (SQLException e) {
            throw new DAOException("there was an issue creating country data", e);
        }
    }
}
