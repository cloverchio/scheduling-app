package com.c195.dao;

import com.c195.model.City;

import java.sql.*;
import java.util.Optional;

public class CityDAO {

    private static final String CITY_BY_NAME_SQL = "" +
            "SELECT * " +
            "FROM city ci " +
            "JOIN country co " +
            "ON ci.countryId = co.countryId " +
            "WHERE ci.city = ?";

    private static final String SAVE_CITY_SQL = "" +
            "INSERT INTO city " +
            "(city, countryId, createDate, createdBy, lastUpdateBy) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static CityDAO daoInstance;
    private final Connection connection;

    private CityDAO(Connection connection) {
        this.connection = connection;
    }

    public static CityDAO getInstance(Connection connection) {
        if (daoInstance == null) {
            daoInstance = new CityDAO(connection);
        }
        return daoInstance;
    }

    public Optional<City> getCityByName(String cityName) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(CITY_BY_NAME_SQL)) {
            statement.setString(1, cityName.toLowerCase());
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toCity(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("There was an issue retrieving a city", e);
        }
    }

    public void saveCity(City city) throws DAOException {
        try (final PreparedStatement statement = connection.prepareStatement(SAVE_CITY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, city.getCity().toLowerCase());
            statement.setInt(2, city.getCountry().getId());
            statement.setTimestamp(3, Timestamp.from(city.getMetadata().getCreatedDate()));
            statement.setString(4, city.getMetadata().getCreatedBy().toLowerCase());
            statement.setString(5, city.getMetadata().getUpdatedBy().toLowerCase());
            statement.executeUpdate();
            final ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                city.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("There was an issue saving a city", e);
        }
    }

    public static City toCity(ResultSet resultSet) throws DAOException {
        try {
            final City city = new City();
            city.setId(resultSet.getInt("cityId"));
            city.setCity(resultSet.getString("city"));
            city.setCountry(CountryDAO.toCountry(resultSet));
            city.setMetadata(MetadataDAO.toMetadata(resultSet));
            return city;
        } catch (SQLException e) {
            throw new DAOException("There was an issue creating city data", e);
        }
    }
}
