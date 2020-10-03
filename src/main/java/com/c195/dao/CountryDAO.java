package com.c195.dao;

import com.c195.model.Country;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CountryDAO {

    private static final String COUNTRY_BY_NAME_SQL = "" +
            "SELECT * " +
            "FROM country " +
            "WHERE country = ?";

    private static final String ALL_COUNTRIES_SQL = "" +
            "SELECT * " +
            "FROM country";

    private static final String SAVE_COUNTRY_SQL = "" +
            "INSERT INTO country " +
            "(country, createDate, createdBy, lastUpdateBy) " +
            "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_COUNTRY_SQL = "" +
            "UPDATE country " +
            "SET country = ?, " +
            "lastUpdate = ?, " +
            "lastUpdateBy = ? " +
            "WHERE countryId = ?";

    private static CountryDAO daoInstance;
    private final Connection connection;

    private CountryDAO(Connection connection) {
        this.connection = connection;
    }

    public static CountryDAO getInstance(Connection connection) {
        return Optional.ofNullable(daoInstance)
                .orElseGet(() -> {
                    daoInstance = new CountryDAO(connection);
                    return daoInstance;
                });
    }

    public Optional<Country> getCountryByName(String countryName) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(COUNTRY_BY_NAME_SQL)) {
            statement.setString(1, countryName.toLowerCase());
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toCountry(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving a country", e);
        }
    }

    public List<Country> getAllCountries() throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(ALL_COUNTRIES_SQL)) {
            final List<Country> countries = new ArrayList<>();
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                countries.add(toCountry(resultSet));
            }
            return countries;
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving countries", e);
        }
    }

    public void saveCountry(Country country) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(SAVE_COUNTRY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, country.getCountry().toLowerCase());
            statement.setTimestamp(2, Timestamp.from(country.getMetadata().getCreatedDate()));
            statement.setString(3, country.getMetadata().getCreatedBy().toLowerCase());
            statement.setString(4, country.getMetadata().getUpdatedBy().toLowerCase());
            statement.executeUpdate();
            final ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                country.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("there was an issue saving a country", e);
        }
    }

    public void updateCountry(Country country) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(UPDATE_COUNTRY_SQL)) {
            statement.setString(1, country.getCountry().toLowerCase());
            statement.setTimestamp(2, Timestamp.from(country.getMetadata().getUpdatedDate()));
            statement.setString(3, country.getMetadata().getUpdatedBy().toLowerCase());
            statement.setInt(4, country.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue updating a country", e);
        }
    }

    public static Country toCountry(ResultSet resultSet) throws DAOException {
        try {
            final Country country = new Country();
            country.setId(resultSet.getInt("countryId"));
            country.setCountry(resultSet.getString("country"));
            country.setMetadata(MetadataDAO.toMetadata(resultSet));
            return country;
        } catch (SQLException e) {
            throw new DAOException("there was an issue creating country data", e);
        }
    }
}
