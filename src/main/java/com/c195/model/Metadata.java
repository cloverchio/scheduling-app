package com.c195.model;

import java.sql.Timestamp;

public class Metadata {

    private final Timestamp createdDate;
    private final String createdBy;
    private final Timestamp updatedDate;
    private final String updatedBy;

    private Metadata(Builder builder) {
        this.createdDate = builder.createdDate;
        this.createdBy = builder.createdBy;
        this.updatedDate = builder.updatedDate;
        this.updatedBy = builder.updatedBy;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public static class Builder {

        private Timestamp createdDate;
        private String createdBy;
        private Timestamp updatedDate;
        private String updatedBy;

        public Builder withCreatedDate(Timestamp createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder withUpdatedDate(Timestamp updatedDate) {
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
