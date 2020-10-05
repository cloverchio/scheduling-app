package com.c195.common;

import java.time.Instant;

public class AppointmentDTO {

    private final int id;
    private final CustomerDTO customerDTO;
    private final int userId;
    private final String title;
    private final String description;
    private final String location;
    private final String contact;
    private final String type;
    private final String url;
    private final Instant start;
    private final Instant end;

    private AppointmentDTO(Builder builder) {
        this.id = builder.id;
        this.customerDTO = builder.customerDTO;
        this.userId = builder.userId;
        this.title = builder.title;
        this.description = builder.description;
        this.location = builder.location;
        this.contact = builder.contact;
        this.type = builder.type;
        this.url = builder.url;
        this.start = builder.start;
        this.end = builder.end;
    }

    public int getId() {
        return id;
    }

    public CustomerDTO getCustomerDTO() {
        return customerDTO;
    }

    public int getUserId() {
        return userId;
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

    public static class Builder {

        private int id;
        private CustomerDTO customerDTO;
        private int userId;
        private String title;
        private String description;
        private String location;
        private String contact;
        private String type;
        private String url;
        private Instant start;
        private Instant end;

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withCustomerDTO(CustomerDTO customerDTO) {
            this.customerDTO = customerDTO;
            return this;
        }

        public Builder withUserId(int userId) {
            this.userId = userId;
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

        public AppointmentDTO build() {
            return new AppointmentDTO(this);
        }
    }
}
