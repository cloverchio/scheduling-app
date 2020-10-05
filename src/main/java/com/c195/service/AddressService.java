package com.c195.service;

import com.c195.common.AddressDTO;
import com.c195.dao.AddressDAO;
import com.c195.model.Address;

import java.util.Optional;

public class AddressService {

    private static AddressService serviceInstance;
    private final AddressDAO addressDAO;

    private AddressService(AddressDAO addressDAO) {
        this.addressDAO = addressDAO;
    }

    public static AddressService getInstance(AddressDAO addressDAO) {
        return Optional.ofNullable(serviceInstance)
                .orElseGet(() -> {
                    serviceInstance = new AddressService(addressDAO);
                    return serviceInstance;
                });
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
