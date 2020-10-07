package com.c195.service;

import com.c195.common.CustomerDTO;
import com.c195.dao.CustomerDAO;
import com.c195.dao.DAOException;
import com.c195.dao.MetadataDAO;
import com.c195.model.Customer;

import java.util.Optional;

public class CustomerService {

    private static CustomerService serviceInstance;
    private final CustomerDAO customerDAO;
    private final AddressService addressService;

    private CustomerService(CustomerDAO customerDAO, AddressService addressService) {
        this.customerDAO = customerDAO;
        this.addressService = addressService;
    }

    public static CustomerService getInstance(CustomerDAO customerDAO, AddressService addressService) {
        return Optional.ofNullable(serviceInstance)
                .orElseGet(() -> {
                    serviceInstance = new CustomerService(customerDAO, addressService);
                    return serviceInstance;
                });
    }

    /**
     * Saves the customer and associated address data.
     *
     * @param customerDTO customer information in which to save.
     * @param currentUser the user initiating the save.
     * @throws DAOException if there are issues saving the customer to the db.
     */
    public void saveCustomer(CustomerDTO customerDTO, String currentUser) throws DAOException {
        addressService.saveAddress(customerDTO.getAddressDTO(), currentUser);
        final Customer customer = toCustomer(customerDTO);
        customer.setMetadata(MetadataDAO.getSaveMetadata(currentUser));
        customerDAO.saveCustomer(customer);
    }

    /**
     * Updates the customer and associated address data.
     *
     * @param customerDTO customer information in which to update.
     * @param currentUser the user initiating the update.
     * @throws DAOException if there are issues updating the customer in the db.
     */
    public void updateCustomer(CustomerDTO customerDTO, String currentUser) throws DAOException {
        addressService.updateAddress(customerDTO.getAddressDTO(), currentUser);
        final Customer customer = toCustomer(customerDTO);
        customer.setMetadata(MetadataDAO.getUpdateMetadata(currentUser));
        customerDAO.updateCustomer(customer);
    }

    /**
     * Deletes customer information.
     * <p>
     * Performs a cascading delete within the query itself so there's
     * no need to invoke the address service beforehand.
     *
     * @param customerId corresponding to the customer to be deleted.
     * @throws DAOException if there are issues deleting the customer from the db.
     */
    public void deleteCustomer(int customerId) throws DAOException {
        customerDAO.deleteCustomerById(customerId);
    }

    public static Customer toCustomer(CustomerDTO customerDTO) {
        final Customer customer = new Customer();
        customer.setId(customerDTO.getId());
        customer.setName(customerDTO.getName());
        customer.setActive(customerDTO.isActive());
        customer.setAddress(AddressService.toAddress(customerDTO.getAddressDTO()));
        return customer;
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
