package com.c195.common.appointment;

import com.c195.common.customer.CustomerDTO;
import com.c195.common.UserDTO;

public class AppointmentDTO {

    private final int id;
    private final String title;
    private final String description;
    private final String contact;
    private final String url;
    private final AppointmentType type;
    private final AppointmentLocation location;
    private final AppointmentTime time;
    private final CustomerDTO customerDTO;
    private final UserDTO userDTO;

    private AppointmentDTO(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.contact = builder.contact;
        this.url = builder.url;
        this.type = builder.type;
        this.location = builder.location;
        this.time = builder.time;
        this.customerDTO = builder.customerDTO;
        this.userDTO = builder.userDTO;
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

    public String getContact() {
        return contact;
    }

    public String getUrl() {
        return url;
    }

    public AppointmentType getType() {
        return type;
    }

    public AppointmentLocation getLocation() {
        return location;
    }

    public AppointmentTime getTime() {
        return time;
    }

    public CustomerDTO getCustomerDTO() {
        return customerDTO;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public static class Builder {

        private int id;
        private String title;
        private String description;
        private String contact;
        private String url;
        private AppointmentType type;
        private AppointmentLocation location;
        private AppointmentTime time;
        private CustomerDTO customerDTO;
        private UserDTO userDTO;

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

        public Builder withLocation(AppointmentLocation location) {
            this.location = location;
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

        public Builder withUserDTO(UserDTO userDTO) {
            this.userDTO = userDTO;
            return this;
        }

        public AppointmentDTO build() {
            return new AppointmentDTO(this);
        }
    }
}
