package com.c195.service;

import com.c195.common.AddressDTO;
import com.c195.dao.*;
import com.c195.model.Address;
import com.c195.model.City;
import com.c195.model.Country;

import java.util.Optional;

public class AddressService {

    private static AddressService serviceInstance;
    private final AddressDAO addressDAO;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;

    private AddressService(AddressDAO addressDAO, CityDAO cityDAO, CountryDAO countryDAO) {
        this.addressDAO = addressDAO;
        this.cityDAO = cityDAO;
        this.countryDAO = countryDAO;
    }

    public static AddressService getInstance(AddressDAO addressDAO, CityDAO cityDAO, CountryDAO countryDAO) {
        return Optional.ofNullable(serviceInstance)
                .orElseGet(() -> {
                    serviceInstance = new AddressService(addressDAO, cityDAO, countryDAO);
                    return serviceInstance;
                });
    }

    /**
     * Saves address data.
     * <p>
     * This ended up being uglier than I would of liked...
     * Country has a one to many relationship with City and City has a one to many relationship
     * with Address, which makes things a little tricky. Under normal circumstances
     * where I'm in control of the table I could opt for unique keys on the country and city name
     * columns allowing me to perform a INSERT/UPDATE IF EXISTS query in cases where country or city
     * already exist. Unfortunately the underlying tables for this project only have auto-incrementing id keys,
     * which means the country/city/address tables can have as many duplicates as they want. They'll just get
     * new ids...
     * <p>
     * To work around this, I guess I have to perform a series of checks. When saving the address
     * I first take a look at the city and see if that already exists by name. If so I set the city id
     * in the address to that and move on. In the event that the city doesn't already exist, a similar
     * check for country is performed. If the country does not exist, we'll manually cascade. Saving country,
     * followed by the city and then the address.
     * <p>
     * This at the very least should help avoid duplicates.
     *
     * @param addressDTO  address information in which to be saved.
     * @param currentUser the user initiating the save.
     * @throws DAOException if there are issues saving the address to the db.
     */
    public void saveAddress(AddressDTO addressDTO, String currentUser) throws DAOException {
        final Address address = toAddress(addressDTO);
        address.setMetadata(MetadataDAO.getSaveMetadata(currentUser));
        setCity(address, currentUser);
        addressDAO.saveAddress(address);
    }

    /**
     * Updates address data.
     * <p>
     * Faces the same duplication issue as {@link #saveAddress(AddressDTO, String)}
     * in cases where the user may want to update the city or country of
     * an existing address.
     *
     * @param addressDTO  address information in which to be updated.
     * @param currentUser the user initiating the update.
     * @throws DAOException if there are issues updating the address in the db.
     */
    public void updateAddress(AddressDTO addressDTO, String currentUser) throws DAOException {
        final Address address = toAddress(addressDTO);
        address.setMetadata(MetadataDAO.getUpdateMetadata(currentUser));
        setCity(address, currentUser);
        addressDAO.updateAddress(address);
    }

    private void setCity(Address address, String currentUser) throws DAOException {
        final City city = address.getCity();
        final Optional<City> existingCity = cityDAO.getCityByName(city.getCity());
        if (existingCity.isPresent()) {
            address.getCity().setId(existingCity.get().getId());
        } else {
            setCountry(city, currentUser);
            cityDAO.saveCity(city);
        }
    }

    private void setCountry(City city, String currentUser) throws DAOException {
        final Optional<Country> existingCountry = countryDAO.getCountryByName(city.getCountry().getCountry());
        if (existingCountry.isPresent()) {
            city.setId(existingCountry.get().getId());
        } else {
            final Country country = city.getCountry();
            country.setMetadata(MetadataDAO.getSaveMetadata(currentUser));
            countryDAO.saveCountry(city.getCountry());
        }
    }

    public static Address toAddress(AddressDTO addressDTO) {
        final Country country = new Country();
        country.setCountry(addressDTO.getCountry());

        final City city = new City();
        city.setCity(addressDTO.getCity());
        city.setCountry(country);

        final Address address = new Address();
        address.setId(addressDTO.getId());
        address.setAddress(addressDTO.getAddress());
        address.setAddress2(addressDTO.getAddress2());
        address.setCity(city);
        address.setPostalCode(addressDTO.getPostalCode());
        address.setPhone(addressDTO.getPhone());
        return address;
    }

    public static AddressDTO toAddressDTO(Address address) {
        return new AddressDTO.Builder()
                .withId(address.getId())
                .withAddress(address.getAddress())
                .withAddress2(address.getAddress2())
                .withPostalCode(address.getPostalCode())
                .withPhone(address.getPhone())
                .withCity(address.getCity().getCity())
                .withCountry(address.getCity().getCountry().getCountry())
                .build();
    }
}
