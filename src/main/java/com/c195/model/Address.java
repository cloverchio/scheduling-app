package com.c195.model;

public class Address {

    private final int id;
    private final String address;
    private final String address2;
    private final City city;
    private final String postalCode;
    private final String phone;
    private final Metadata metadata;

    private Address(Builder builder) {
        this.id = builder.id;
        this.address = builder.address;
        this.address2 = builder.address2;
        this.city = builder.city;
        this.postalCode = builder.postalCode;
        this.phone = builder.phone;
        this.metadata = builder.metadata;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress2() {
        return address2;
    }

    public City getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public static class Builder {

        private int id;
        private String address;
        private String address2;
        private City city;
        private String postalCode;
        private String phone;
        private Metadata metadata;

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder withAddress2(String address2) {
            this.address2 = address2;
            return this;
        }

        public Builder withCity(City city) {
            this.city = city;
            return this;
        }

        public Builder withPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder withMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }
}
