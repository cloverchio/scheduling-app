package com.c195.common.report;

import java.util.Map;

public class ReportAggregationDTO<T> {

    private final Map<String, T> data;

    private ReportAggregationDTO(Builder<T> builder) {
        this.data = builder.data;
    }

    public Map<String, T> getData() {
        return data;
    }

    public static class Builder<T> {

        private Map<String, T> data;

        public Builder<T> withData(Map<String, T> data) {
            this.data = data;
            return this;
        }

        public ReportAggregationDTO<T> build() {
            return new ReportAggregationDTO<>(this);
        }
    }
}
