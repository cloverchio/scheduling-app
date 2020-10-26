package com.c195.common;

import java.util.List;
import java.util.Map;

public class ReportAggregationDTO<T> {

    private final Integer count;
    private final Double average;
    private final Map<String, List<T>> data;

    private ReportAggregationDTO(Builder<T> builder) {
        this.count = builder.count;
        this.average = builder.average;
        this.data = builder.data;
    }

    public Integer getCount() {
        return count;
    }

    public Double getAverage() {
        return average;
    }

    public Map<String, List<T>> getData() {
        return data;
    }

    public static class Builder<T> {

        private Integer count;
        private Double average;
        private Map<String, List<T>> data;

        public Builder<T> withCount(Integer count) {
            this.count = count;
            return this;
        }

        public Builder<T> withAverage(Double average) {
            this.average = average;
            return this;
        }

        public Builder<T> withData(Map<String, List<T>> data) {
            this.data = data;
            return this;
        }

        public ReportAggregationDTO<T> build() {
            return new ReportAggregationDTO<>(this);
        }
    }
}
