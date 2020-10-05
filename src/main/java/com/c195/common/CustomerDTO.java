package com.c195.common;

public class CustomerDTO {

    private final int id;
    private final String name;
    private final boolean isActive;
    private final AddressDTO addressDTO;

    private CustomerDTO(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.isActive = builder.isActive;
        this.addressDTO = builder.addressDTO;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public AddressDTO getAddressDTO() {
        return addressDTO;
    }

    public static class Builder {

        private int id;
        private String name;
        private boolean isActive;
        private AddressDTO addressDTO;

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder withAddressDTO(AddressDTO addressDTO) {
            this.addressDTO = addressDTO;
            return this;
        }

        public CustomerDTO build() {
            return new CustomerDTO(this);
        }
    }
}
