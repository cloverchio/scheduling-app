package com.c195.service;

import com.c195.common.appointment.AppointmentDTO;
import com.c195.common.appointment.AppointmentTime;
import com.c195.common.report.ReportAggregationDTO;
import com.c195.dao.DAOException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Just going to perform these aggregations in memory since the dataset will likely be small.
 */
public class ReportService {

    private static ReportService serviceInstance;
    private final AppointmentService appointmentService;

    private ReportService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    public static ReportService getInstance(AppointmentService appointmentService) {
        if (serviceInstance == null) {
            serviceInstance = new ReportService(appointmentService);
        }
        return serviceInstance;
    }

    /**
     * Retrieves number of report types by month.
     *
     * @return a {@link ReportAggregationDTO} containing the number of appointment types
     * grouped by month.
     * @throws DAOException if there are issues retrieving appointments from the db.
     */
    public ReportAggregationDTO<Map<String, Long>> getAppointmentTypeCountByMonth() throws DAOException {
        final Map<String, Map<String, Long>> data = appointmentService.getAllAppointments()
                .stream()
                .collect(Collectors.groupingBy(appointment -> toMonth(appointment.getTime()),
                        Collectors.groupingBy(appointment -> appointment.getType().getName(), Collectors.counting())));
        return new ReportAggregationDTO.Builder<Map<String, Long>>()
                .withData(data)
                .build();
    }

    /**
     * Retrieves number of report types by customer.
     *
     * @return a {@link ReportAggregationDTO} containing the number of appointment types
     * grouped by customer.
     * @throws DAOException if there are issues retrieving appointments from the db.
     */
    public ReportAggregationDTO<Map<String, Long>> getAppointmentTypeCountByCustomer() throws DAOException {
        final Map<String, Map<String, Long>> data = appointmentService.getAllAppointments()
                .stream()
                .collect(Collectors.groupingBy(appointment -> appointment.getCustomerDTO().getName(),
                        Collectors.groupingBy(appointment -> appointment.getType().getName(), Collectors.counting())));
        return new ReportAggregationDTO.Builder<Map<String, Long>>()
                .withData(data)
                .build();
    }

    /**
     * Retrieves schedule by consultant. (Assuming consultant is contact?)
     *
     * @return a {@link ReportAggregationDTO} containing appointments grouped by the contact.
     * @throws DAOException if there are issues retrieving appointments from the db.
     */
    public ReportAggregationDTO<List<AppointmentDTO>> getAppointmentsByContact() throws DAOException {
        return new ReportAggregationDTO.Builder<List<AppointmentDTO>>()
                .withData(appointmentService.getAllAppointments()
                        .stream()
                        .collect(Collectors.groupingBy(AppointmentDTO::getContact)))
                .build();
    }

    private static String toMonth(AppointmentTime appointmentTime) {
        return appointmentTime.getLocationStart()
                .getMonth()
                .name();
    }
}
