package com.c195.service;

import com.c195.common.CustomerDTO;
import com.c195.dao.CustomerDAO;
import com.c195.model.Customer;

import java.util.Optional;

public class CustomerService {

    private static CustomerService serviceInstance;
    private final CustomerDAO customerDAO;

    private CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public static CustomerService getInstance(CustomerDAO customerDAO) {
        return Optional.ofNullable(serviceInstance)
                .orElseGet(() -> {
                    serviceInstance = new CustomerService(customerDAO);
                    return serviceInstance;
                });
    }

    public static CustomerDTO toCustomerDTO(Customer customer) {
        return new CustomerDTO.Builder()
                .withId(customer.getId())
                .withName(customer.getName())
                .withActive(customer.isActive())
                .withAddressDTO(AddressService.toAddressDTO(customer.getAddress()))
                .build();
    }
}
