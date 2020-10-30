package com.c195.controller.appointment;

import com.c195.common.appointment.AppointmentDTO;
import com.c195.common.appointment.AppointmentView;
import com.c195.common.CheckedFunction;
import com.c195.common.UserDTO;
import com.c195.controller.Controller;
import com.c195.dao.AppointmentDAO;
import com.c195.dao.UserDAO;
import com.c195.service.AppointmentService;
import com.c195.service.UserService;
import com.c195.util.ControllerUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Appointment management functionality.
 * <p>
 * Addresses the following task requirements:
 * <p>
 * C. Provide the ability to add, update, and delete appointments, capturing the type of appointment
 * and a link to the specific customer record in the database.
 * <p>
 * D. Provide the ability to view the calendar by month and by week.
 * <p>
 * E. Provide the ability to automatically adjust appointment times based on user time zones
 * and daylight saving time.
 */
public class AppointmentController extends Controller implements Initializable {

    @FXML
    private TableView<AppointmentDTO> appointmentTable;
    @FXML
    private TableColumn<AppointmentDTO, String> titleColumn;
    @FXML
    private TableColumn<AppointmentDTO, String> locationColumn;
    @FXML
    private TableColumn<AppointmentDTO, String> customerNameColumn;
    @FXML
    private TableColumn<AppointmentDTO, String> typeColumn;
    @FXML
    private TableColumn<AppointmentDTO, String> startColumn;
    @FXML
    private TableColumn<AppointmentDTO, String> endColumn;
    @FXML
    private ComboBox<String> appointmentViewComboBox;

    private UserService userService;
    private AppointmentService appointmentService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appointmentViewComboBox.setItems(getAppointmentViews());
        appointmentViewComboBox.getSelectionModel().selectFirst();
        super.initialize(url, resourceBundle);
        getDatabaseConnection()
                .ifPresent(connection -> {
                    userService = UserService.getInstance(UserDAO.getInstance(connection));
                    appointmentService = AppointmentService.getInstance(AppointmentDAO.getInstance(connection));
                    createAppointmentTable();
                    updateAppointmentsByViewSelection();
                });
    }

    @FXML
    public void cancel(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/main.fxml");
    }

    @FXML
    public void create(ActionEvent actionEvent) {
        showView(actionEvent, getClass(), "../../view/appointment/create.fxml");
    }

    @FXML
    public void update(ActionEvent actionEvent) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../view/appointment/update.fxml"));
        final Parent parent = fxmlLoader.load();
        Optional.ofNullable(appointmentTable.getSelectionModel().getSelectedItem())
                .ifPresent(selectedAppointment -> {
                    final AppointmentUpdateController appointmentUpdateController = fxmlLoader.getController();
                    appointmentUpdateController.setAppointmentId(selectedAppointment.getId());
                    appointmentUpdateController.setFields(selectedAppointment);
                    ControllerUtils.setEventStage(actionEvent, parent);
                });
    }

    @FXML
    public void delete() {
        Optional.ofNullable(appointmentTable.getSelectionModel().getSelectedItem())
                .map(AppointmentDTO::getId)
                .ifPresent(selectedAppointmentId -> showConfirmation(() -> {
                    appointmentService.deleteAppointment(selectedAppointmentId);
                    createAppointmentTable();
                    return null;
                }));
    }

    private void createAppointmentTable() {
        appointmentTable.setItems(getAppointmentsByViewSelection());
        titleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        locationColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocation().getName()));
        customerNameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomerDTO().getName()));
        typeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType().getName()));
        startColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTime().getUserStartISO()));
        endColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTime().getUserEndISO()));
    }

    private void updateAppointmentsByViewSelection() {
        appointmentViewComboBox.setOnAction(actionEvent -> appointmentTable.setItems(getAppointmentsByViewSelection()));
    }

    private ObservableList<AppointmentDTO> getAppointmentsByViewSelection() {
        final String selectedView = appointmentViewComboBox.getSelectionModel().getSelectedItem();
        if (selectedView.equals(AppointmentView.WEEK.getName())) {
            return getUpcomingAppointmentsByWeek();
        } else if (selectedView.equals(AppointmentView.MONTH.getName())) {
            return getUpcomingAppointmentsByMonth();
        }
        return getAllUpcomingAppointments();
    }

    private ObservableList<AppointmentDTO> getAllUpcomingAppointments() {
        return getUpcomingAppointments(userId -> appointmentService.getUpcomingAppointmentsByUser(userId));
    }

    private ObservableList<AppointmentDTO> getUpcomingAppointmentsByWeek() {
        return getUpcomingAppointments(userId -> appointmentService.getUpcomingAppointmentsByUserWeek(userId));
    }

    private ObservableList<AppointmentDTO> getUpcomingAppointmentsByMonth() {
        return getUpcomingAppointments(userId -> appointmentService.getUpcomingAppointmentsByUserMonth(userId));
    }

    private ObservableList<AppointmentDTO> getUpcomingAppointments(CheckedFunction<Integer, List<AppointmentDTO>> appointmentFunction) {
        return userService.getCurrentUser()
                .map(UserDTO::getId)
                .map(currentUserId ->
                        performDatabaseAction(() -> appointmentFunction.applyWithIO(currentUserId))
                                .orElseGet(Collections::emptyList))
                .map(FXCollections::observableList)
                .orElseGet(FXCollections::emptyObservableList);
    }

    private static ObservableList<String> getAppointmentViews() {
        return FXCollections.observableList(
                Arrays.stream(AppointmentView.values())
                        .map(AppointmentView::getName)
                        .collect(Collectors.toList()));
    }
}
