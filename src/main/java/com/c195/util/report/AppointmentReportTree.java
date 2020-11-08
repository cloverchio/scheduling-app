package com.c195.util.report;

import com.c195.common.appointment.AppointmentDTO;
import com.c195.common.report.ReportAggregationDTO;
import javafx.scene.control.TreeItem;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentReportTree extends ReportTree {

    private final String rootLabel;
    private final ReportAggregationDTO<List<AppointmentDTO>> reportAggregation;

    public AppointmentReportTree(String rootLabel,
                                 ReportAggregationDTO<List<AppointmentDTO>> reportAggregation) {
        this.rootLabel = rootLabel;
        this.reportAggregation = reportAggregation;
    }

    public TreeItem<String> getTree() {
        return createNode(rootLabel, createAggregateNodes());
    }

    private List<TreeItem<String>> createAggregateNodes() {
        return reportAggregation.getData().entrySet()
                .stream()
                .map(entry -> createNode(entry.getKey(), createAppointmentNodes(entry.getValue())))
                .collect(Collectors.toList());
    }

    private static List<TreeItem<String>> createAppointmentNodes(List<AppointmentDTO> appointmentData) {
        return appointmentData
                .stream()
                .map(AppointmentReportTree::toAppointmentText)
                .map(text -> createNode(text, Collections.emptyList()))
                .collect(Collectors.toList());
    }

    private static String toAppointmentText(AppointmentDTO appointmentDTO) {
        return "Title: " +
                appointmentDTO.getTitle() +
                "\n" +
                "Location: " +
                appointmentDTO.getLocation().getName() +
                "\n" +
                "Start: " +
                appointmentDTO.getTime().getLocationStartISO() +
                "\n" +
                "End: " +
                appointmentDTO.getTime().getLocationEndISO() +
                "\n";
    }
}
