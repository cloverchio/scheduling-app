package com.c195.service;

import com.c195.common.customer.AddressDTO;
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
     * Retrieves address information by the associated address field.
     *
     * @param address in which to retrieve information for.
     * @return address data associated with the given address.
     * @throws DAOException if there are issues retrieving the address from the db.
     */
    public Optional<AddressDTO> getAddress(String address) throws DAOException {
        return addressDAO.getAddress(address)
                .map(AddressService::toAddressDTO);
    }

    /**
     * Saves the address.
     * <p>
     * Country has a one to many relationship with City and City has a one to many relationship
     * with Address, which makes things a little tricky. Under normal circumstances
     * where I'm in control of the table I could opt for unique keys on the country and city name
     * columns, allowing me to perform a INSERT/UPDATE IF EXISTS query in cases where values for those columns
     * already exist. Unfortunately the underlying tables for this project only use auto-incrementing id keys,
     * which means that the country/city/address tables can have as many duplicate values as they want because they
     * will just get assigned new ids. For example this allows you to have several USA's in the country table,
     * or several Phoenixs in the city table.
     * <p>
     * I'll attempt to avoid that by performing a couple of simple but ugly checks. When saving the address I'll
     * first query the city name to see if it already exists. If it already exists, I can set the city id within the
     * address to that and move on. In the event that the city doesn't already exist, then I'll do the same exact
     * thing for country. If the country doesn't already exist, the country will be saved, followed by the city.
     * <p>
     * This avoids having duplicate values in the table, despite the rows having unique keys.
     *
     * @param addressDTO  address information in which to be saved.
     * @param currentUser the user initiating the save.
     * @return the id of the saved address.
     * @throws DAOException if there are issues saving the address to the db.
     */
    public Integer saveAddress(AddressDTO addressDTO, String currentUser) throws DAOException {
        final Address address = toAddress(addressDTO);
        address.setMetadata(MetadataDAO.getSaveMetadata(currentUser));
        setCity(address, currentUser);
        addressDAO.saveAddress(address);
        return address.getId();
    }

    /**
     * Updates the address.
     * <p>
     * Faces the same duplication issue as {@link #saveAddress(AddressDTO, String)}
     * in cases where the user may want to update the city or country of
     * an existing address.
     *
     * @param addressDTO  address information in which to be updated.
     * @param currentUser the user initiating the update.
     * @return the id of the updated address.
     * @throws DAOException if there are issues updating the address in the db.
     */
    @SuppressWarnings("UnusedReturnValue")
    public Integer updateAddress(AddressDTO addressDTO, String currentUser) throws DAOException {
        final Address address = toAddress(addressDTO);
        address.setMetadata(MetadataDAO.getUpdateMetadata(currentUser));
        setCity(address, currentUser);
        addressDAO.updateAddress(address);
        return address.getId();
    }

    private void setCity(Address address, String currentUser) throws DAOException {
        final City city = address.getCity();
        final Optional<City> existingCity = cityDAO.getCityByName(city.getCity());
        if (existingCity.isPresent()) {
            address.setCity(existingCity.get());
        } else {
            setCountry(city, currentUser);
            city.setMetadata(MetadataDAO.getSaveMetadata(currentUser));
            cityDAO.saveCity(city);
        }
    }

    private void setCountry(City city, String currentUser) throws DAOException {
        final Optional<Country> existingCountry = countryDAO.getCountryByName(city.getCountry().getCountry());
        if (existingCountry.isPresent()) {
            city.setCountry(existingCountry.get());
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
