package com.c195.model;

import java.time.Instant;

public class Metadata {

    private final Instant createdDate;
    private final String createdBy;
    private final Instant updatedDate;
    private final String updatedBy;

    private Metadata(Builder builder) {
        this.createdDate = builder.createdDate;
        this.createdBy = builder.createdBy;
        this.updatedDate = builder.updatedDate;
        this.updatedBy = builder.updatedBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public static class Builder {

        private Instant createdDate;
        private String createdBy;
        private Instant updatedDate;
        private String updatedBy;

        public Builder withCreatedDate(Instant createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder withUpdatedDate(Instant updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        public Builder withUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public Metadata build() {
            return new Metadata(this);
        }
    }
}
