package com.c195.util;

import javafx.scene.control.TreeItem;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ReportUtils {

    public static TreeItem<String> createReportNodes(String rootKey, String subKey, Map<String, Map<String, Long>> reportData) {
        final List<TreeItem<String>> treeNodes = createAggregateNodes(subKey, reportData).entrySet()
                .stream()
                .map(entry -> getNode(entry.getKey(), Collections.singletonList(entry.getValue())))
                .collect(Collectors.toList());
        return getNode(rootKey, treeNodes);
    }

    private static Map<String, TreeItem<String>> createAggregateNodes(String subKey, Map<String, Map<String, Long>> reportData) {
        return reportData.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> createCountNodes(subKey, entry.getValue())));
    }

    private static TreeItem<String> createCountNodes(String subLabel, Map<String, Long> countData) {
        final List<TreeItem<String>> countNodes = countData.entrySet()
                .stream()
                .map(entry -> createCountNode(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return getNode(subLabel, countNodes);
    }

    private static TreeItem<String> createCountNode(String value, Long count) {
        return getNode(value, Collections.singletonList(getNode("Count: " + count, Collections.emptyList())));
    }

    private static TreeItem<String> getNode(String key, List<TreeItem<String>> children) {
        final TreeItem<String> node = new TreeItem<>(key);
        node.setExpanded(true);
        node.getChildren().setAll(children);
        return node;
    }
}
