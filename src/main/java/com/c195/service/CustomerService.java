package com.c195.service;

import com.c195.common.customer.AddressDTO;
import com.c195.common.customer.CustomerDTO;
import com.c195.common.customer.CustomerException;
import com.c195.common.UserDTO;
import com.c195.dao.CustomerDAO;
import com.c195.dao.DAOException;
import com.c195.dao.MetadataDAO;
import com.c195.model.Address;
import com.c195.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * Retrieves a customer by its corresponding id.
     *
     * @param id in which to retrieve the customer for.
     * @return the customer corresponding to the given id.
     * @throws DAOException if there are issues retrieving the customer from the db.
     * @throws CustomerException if the customer cannot be found
     */
    public CustomerDTO getCustomerById(int id) throws DAOException, CustomerException {
        return customerDAO.getCustomerById(id)
                .map(CustomerService::toCustomerDTO)
                .orElseThrow(() -> new CustomerException("Customer does not exist"));
    }

    /**
     * Retrieves all existing customers.
     *
     * @return list of {@link CustomerDTO}.
     * @throws DAOException if there are issues retrieving customers from the db.
     */
    public List<CustomerDTO> getAllCustomers() throws DAOException {
        return customerDAO.getAllCustomers()
                .stream()
                .map(CustomerService::toCustomerDTO)
                .collect(Collectors.toList());
    }

    /**
     * Saves the customer and its associated address data.
     *
     * @param customerDTO customer information in which to save.
     * @param currentUser the user initiating the save.
     * @return the id of the saved customer.
     * @throws DAOException if there are issues saving the customer to the db.
     */
    public Integer saveCustomer(CustomerDTO customerDTO, UserDTO currentUser) throws DAOException {
        final Customer customer = toCustomer(customerDTO);
        final String currentUsername = currentUser.getUsername();
        setAddress(customer, customerDTO.getAddressDTO(), currentUsername);
        customer.setMetadata(MetadataDAO.getSaveMetadata(currentUsername));
        customerDAO.saveCustomer(customer);
        return customer.getId();
    }

    /**
     * Updates the customer and its associated address data.
     *
     * @param customerDTO customer information in which to update.
     * @param currentUser the user initiating the update.
     * @return the id of the updated customer.
     * @throws DAOException if there are issues updating the customer in the db.
     */
    public Integer updateCustomer(CustomerDTO customerDTO, UserDTO currentUser) throws DAOException {
        final String currentUsername = currentUser.getUsername();
        addressService.updateAddress(customerDTO.getAddressDTO(), currentUsername);
        final Customer customer = toCustomer(customerDTO);
        customer.setMetadata(MetadataDAO.getUpdateMetadata(currentUsername));
        customerDAO.updateCustomer(customer);
        return customer.getId();
    }

    /**
     * Deletes the customer.
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

    /**
     * Performing the same existence check that {@link AddressService} is doing for its dependencies.
     * Also to save the sanctity of my service classes (not allowing database classes outside of them) the
     * original AddressDTO unfortunately needs to be passed here as well.
     *
     * @param customer    customer information in which to save.
     * @param addressDTO  address information associated with the customer.
     * @param currentUser user initiating the save.
     * @throws DAOException if there are issues saving address in the db.
     */
    private void setAddress(Customer customer, AddressDTO addressDTO, String currentUser) throws DAOException {
        final Optional<Address> existingAddress = addressService.getAddress(addressDTO.getAddress())
                .map(AddressService::toAddress);
        if (existingAddress.isPresent()) {
            customer.setAddress(existingAddress.get());
        } else {
            final int addressId = addressService.saveAddress(addressDTO, currentUser);
            customer.getAddress().setId(addressId);
        }
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
