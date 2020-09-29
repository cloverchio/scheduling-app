package com.c195.model;

import java.time.Instant;

public class Appointment {

    private final int id;
    private final Customer customer;
    private final User user;
    private final String title;
    private final String description;
    private final String location;
    private final String contact;
    private final String type;
    private final String url;
    private final Instant start;
    private final Instant end;
    private final Metadata metadata;

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    private Appointment(Builder builder) {
        this.id = builder.id;
        this.customer = builder.customer;
        this.user = builder.user;
        this.title = builder.title;
        this.description = builder.description;
        this.location = builder.location;
        this.contact = builder.contact;
        this.type = builder.type;
        this.url = builder.url;
        this.start = builder.start;
        this.end = builder.end;
        this.metadata = builder.metadata;
    }

    public static class Builder {

        private int id;
        private Customer customer;
        private User user;
        private String title;
        private String description;
        private String location;
        private String contact;
        private String type;
        private String url;
        private Instant start;
        private Instant end;
        private Metadata metadata;

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withCustomer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder withContact(String contact) {
            this.contact = contact;
            return this;
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withStart(Instant start) {
            this.start = start;
            return this;
        }

        public Builder withEnd(Instant end) {
            this.end = end;
            return this;
        }

        public Builder withMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Appointment build() {
            return new Appointment(this);
        }
    }
}
