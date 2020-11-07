package com.c195.service;

import com.c195.dao.*;

import java.sql.Connection;
import java.time.Clock;

public class ServiceResolver {

    private final Connection connection;
    private final Clock clock;

    public ServiceResolver(Connection connection, Clock clock) {
        this.connection = connection;
        this.clock = clock;
    }

    public static MessagingService getMessagingService() {
        return MessagingService.getInstance();
    }

    public UserService getUserService() {
        return UserService.getInstance(UserDAO.getInstance(connection));
    }

    public ReportService getReportService() {
        return ReportService.getInstance(getAppointmentService());
    }

    public AppointmentService getAppointmentService() {
        return AppointmentService.getInstance(AppointmentDAO.getInstance(connection), clock);
    }

    public CustomerService getCustomerService() {
        return CustomerService.getInstance(CustomerDAO.getInstance(connection), getAddressService(), clock);
    }

    public AddressService getAddressService() {
        final AddressDAO addressDAO = AddressDAO.getInstance(connection);
        final CityDAO cityDAO = CityDAO.getInstance(connection);
        final CountryDAO countryDAO = CountryDAO.getInstance(connection);
        return AddressService.getInstance(addressDAO, cityDAO, countryDAO, clock);
    }
}
