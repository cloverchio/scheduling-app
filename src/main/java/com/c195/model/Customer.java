package com.c195.model;

public class Customer {

    private final int id;
    private final String name;
    private final Address address;
    private final boolean isActive;
    private final Metadata metadata;

    private Customer(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.address = builder.address;
        this.isActive = builder.isActive;
        this.metadata = builder.metadata;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isActive() {
        return isActive;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public static class Builder {

        private int id;
        private String name;
        private Address address;
        private boolean isActive;
        private Metadata metadata;

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAddress(Address address) {
            this.address = address;
            return this;
        }

        public Builder withActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder withMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
