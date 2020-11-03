package com.c195.controller.report;

import com.c195.common.CheckedSupplier;
import com.c195.common.report.ReportAggregationDTO;
import com.c195.common.report.ReportType;
import com.c195.controller.Controller;
import com.c195.dao.AppointmentDAO;
import com.c195.service.AppointmentService;
import com.c195.service.ReportService;
import com.c195.util.ReportUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Report functionality.
 * <p>
 * Addresses the following task requirements:
 * <p>
 * I. Provide the ability to generate each  of the following reports:
 * <p>
 * • number of appointment types by month
 * <p>
 * • the schedule for each consultant
 * <p>
 * • one additional report of your choice
 */
public class ReportController extends Controller implements Initializable {

    @FXML
    private TreeView<String> reportTree;

    @FXML
    private ComboBox<String> reportTypeComboBox;

    private ReportService reportService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reportTypeComboBox.setItems(getReportTypes());
        super.initialize(url, resourceBundle);
        getDatabaseConnection()
                .ifPresent(connection -> {
                    final AppointmentService appointmentService =
                            AppointmentService.getInstance(AppointmentDAO.getInstance(connection));
                    reportService = ReportService.getInstance(appointmentService);
                    updateReportByTypeSelection();
                });
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/main.fxml");
    }

    private void updateReportByTypeSelection() {
        reportTypeComboBox.setOnAction(actionEvent -> setReportTreeByTypeSelection());
    }

    private void setReportTreeByTypeSelection() {
        final String selectedView = reportTypeComboBox.getSelectionModel().getSelectedItem();
        if (selectedView.equals(ReportType.APPOINTMENT_TYPES_BY_MONTH.getName())) {
            createAppointmentTypeCountByMonthTree();
        } else if (selectedView.equals(ReportType.APPOINTMENT_TYPES_BY_CUSTOMER.getName())) {
            createAppointmentTypeCountByCustomerTree();
        }
    }

    private void createAppointmentTypeCountByMonthTree() {
        createCountTree("Months", "Appointment Types", () -> reportService.getAppointmentTypeCountByMonth());
    }

    private void createAppointmentTypeCountByCustomerTree() {
        createCountTree("Customers", "Appointment Types", () -> reportService.getAppointmentTypeCountByCustomer());
    }

    private void createCountTree(String rootKey, String subKey, CheckedSupplier<ReportAggregationDTO<Map<String, Long>>> reportSupplier) {
        performDatabaseAction(reportSupplier)
                .map(reportData -> ReportUtils.createReportNodes(rootKey, subKey, reportData.getData()))
                .ifPresent(root -> reportTree.setRoot(root));
    }

    private static ObservableList<String> getReportTypes() {
        return FXCollections.observableList(
                Arrays.stream(ReportType.values())
                        .map(ReportType::getName)
                        .collect(Collectors.toList()));
    }
}
