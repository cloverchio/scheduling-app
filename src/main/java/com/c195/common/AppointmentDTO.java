package com.c195.common;

public class AppointmentDTO {

    private final int id;
    private final String title;
    private final String description;
    private final String location;
    private final String contact;
    private final String url;
    private final AppointmentType type;
    private final AppointmentTime time;
    private final CustomerDTO customerDTO;

    private AppointmentDTO(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.location = builder.location;
        this.contact = builder.contact;
        this.url = builder.url;
        this.type = builder.type;
        this.time = builder.time;
        this.customerDTO = builder.customerDTO;
    }

    public int getId() {
        return id;
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

    public String getUrl() {
        return url;
    }

    public AppointmentType getType() {
        return type;
    }

    public AppointmentTime getTime() {
        return time;
    }

    public CustomerDTO getCustomerDTO() {
        return customerDTO;
    }

    public static class Builder {

        private int id;
        private String title;
        private String description;
        private String location;
        private String contact;
        private String url;
        private AppointmentType type;
        private AppointmentTime time;
        private CustomerDTO customerDTO;

        public Builder withId(int id) {
            this.id = id;
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

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withType(AppointmentType type) {
            this.type = type;
            return this;
        }

        public Builder withTime(AppointmentTime time) {
            this.time = time;
            return this;
        }

        public Builder withCustomerDTO(CustomerDTO customerDTO) {
            this.customerDTO = customerDTO;
            return this;
        }

        public AppointmentDTO build() {
            return new AppointmentDTO(this);
        }
    }
}
