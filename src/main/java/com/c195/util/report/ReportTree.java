package com.c195.util.report;

import javafx.scene.control.TreeItem;

import java.util.List;

public class ReportTree {

    protected static TreeItem<String> createNode(String key, List<TreeItem<String>> children) {
        final TreeItem<String> node = new TreeItem<>(key);
        node.setExpanded(true);
        node.getChildren().setAll(children);
        return node;
    }
}
