package com.c195.controller.report;

import com.c195.common.AppointmentDTO;
import com.c195.common.ReportType;
import com.c195.controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Arrays;
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
    private TableView<AppointmentDTO> reportTable;
    @FXML
    private Label totalLabel;

    @FXML
    private ComboBox<String> reportTypeComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reportTypeComboBox.setItems(getReportTypes());
        super.initialize(url, resourceBundle);
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/main.fxml");
    }

    private static ObservableList<String> getReportTypes() {
        return FXCollections.observableList(
                Arrays.stream(ReportType.values())
                        .map(ReportType::getName)
                        .collect(Collectors.toList()));
    }
}
