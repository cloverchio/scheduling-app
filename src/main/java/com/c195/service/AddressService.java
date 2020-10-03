package com.c195.service;

import com.c195.dao.AddressDAO;
import com.c195.dao.CityDAO;
import com.c195.dao.CountryDAO;

import java.util.Optional;

public class AddressService {

    private static AddressService serviceInstance;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;
    private final AddressDAO addressDAO;

    private AddressService(CityDAO cityDAO, CountryDAO countryDAO, AddressDAO addressDAO) {
        this.cityDAO = cityDAO;
        this.countryDAO = countryDAO;
        this.addressDAO = addressDAO;
    }

    public static AddressService getInstance(CityDAO cityDAO, CountryDAO countryDAO, AddressDAO addressDAO) {
        return Optional.ofNullable(serviceInstance)
                .orElseGet(() -> {
                    serviceInstance = new AddressService(cityDAO, countryDAO, addressDAO);
                    return serviceInstance;
                });
    }
}
