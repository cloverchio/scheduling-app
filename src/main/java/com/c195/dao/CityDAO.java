package com.c195.dao;

import com.c195.model.City;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CityDAO {

    private static final String CITY_BY_ID_SQL = "" +
            "SELECT * " +
            "FROM city ci " +
            "JOIN country co " +
            "on ci.countryId = co.countryId " +
            "WHERE ci.cityId = ?";

    private static final String ALL_CITIES_SQL = "" +
            "SELECT * " +
            "FROM city ci " +
            "JOIN country co " +
            "on ci.countryId = co.countryId";

    private static final String SAVE_CITY_SQL = "" +
            "INSERT INTO city " +
            "(city, countryId, createDate, createdBy, lastUpdateBy) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_CITY_SQL = "" +
            "UPDATE city " +
            "SET city = ?, " +
            "countryId = ?, " +
            "lastUpdate = ?, " +
            "lastUpdateBy = ? " +
            "WHERE cityId = ?";

    private static final String DELETE_CITY_BY_ID_SQL = "" +
            "DELETE FROM city " +
            "WHERE cityId = ?";

    private final Connection connection;

    public CityDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<City> getCityById(int id) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CITY_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(toCity(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving a city", e);
        }
    }

    public List<City> getAllCities() throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(ALL_CITIES_SQL)) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<City> cities = new ArrayList<>();
            while (resultSet.next()) {
                cities.add(toCity(resultSet));
            }
            return cities;
        } catch (SQLException e) {
            throw new DAOException("there was an issue retrieving cities", e);
        }
    }

    public void saveCity(City city) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(SAVE_CITY_SQL)) {
            preparedStatement.setString(1, city.getCity());
            preparedStatement.setInt(2, city.getCountry().getId());
            preparedStatement.setTimestamp(3, Timestamp.from(city.getMetadata().getCreatedDate()));
            preparedStatement.setString(4, city.getMetadata().getCreatedBy());
            preparedStatement.setString(5, city.getMetadata().getUpdatedBy());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue saving a city", e);
        }
    }

    public void updateCity(City city) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CITY_SQL)) {
            preparedStatement.setString(1, city.getCity());
            preparedStatement.setInt(2, city.getCountry().getId());
            preparedStatement.setTimestamp(3, Timestamp.from(city.getMetadata().getUpdatedDate()));
            preparedStatement.setString(4, city.getMetadata().getUpdatedBy());
            preparedStatement.setInt(5, city.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue updating a city", e);
        }
    }

    public void deleteCityById(int id) throws DAOException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CITY_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("there was an issue removing a city", e);
        }
    }

    public static City toCity(ResultSet resultSet) throws DAOException {
        try {
            return new City.Builder()
                    .withId(resultSet.getInt("cityId"))
                    .withCity(resultSet.getString("city"))
                    .withCountry(CountryDAO.toCountry(resultSet))
                    .withMetadata(MetadataDAO.toMetadata(resultSet))
                    .build();
        } catch (SQLException e) {
            throw new DAOException("there was an issue creating city data", e);
        }
    }
}
