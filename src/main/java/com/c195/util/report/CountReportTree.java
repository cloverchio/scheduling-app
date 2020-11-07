package com.c195.util.report;

import com.c195.common.report.ReportAggregationDTO;
import javafx.scene.control.TreeItem;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CountReportTree extends ReportTree {

    private final String rootLabel;
    private final String subLabel;
    private final ReportAggregationDTO<Map<String, Long>> reportAggregation;

    public CountReportTree(String rootLabel,
                           String subLabel,
                           ReportAggregationDTO<Map<String, Long>> reportAggregation) {
        this.rootLabel = rootLabel;
        this.subLabel = subLabel;
        this.reportAggregation = reportAggregation;
    }

    public TreeItem<String> getTree() {
        final List<TreeItem<String>> treeNodes = createAggregateNodes().entrySet()
                .stream()
                .map(entry -> createNode(entry.getKey(), Collections.singletonList(entry.getValue())))
                .collect(Collectors.toList());
        return createNode(rootLabel, treeNodes);
    }

    private Map<String, TreeItem<String>> createAggregateNodes() {
        return reportAggregation.getData().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> createCountNodes(entry.getValue())));
    }

    private TreeItem<String> createCountNodes(Map<String, Long> countData) {
        final List<TreeItem<String>> countNodes = countData.entrySet()
                .stream()
                .map(entry -> createNode(entry.getKey(), Collections.singletonList(createCountNode(entry.getValue()))))
                .collect(Collectors.toList());
        return createNode(subLabel, countNodes);
    }

    private TreeItem<String> createCountNode(Long count) {
        return createNode("Count: " + count, Collections.emptyList());
    }
}
