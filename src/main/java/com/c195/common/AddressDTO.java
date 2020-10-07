package com.c195.common;

public class AddressDTO {

    private final Integer id;
    private final String address;
    private final String address2;
    private final String city;
    private final String country;
    private final String postalCode;
    private final String phone;

    private AddressDTO(Builder builder) {
        this.id = builder.id;
        this.address = builder.address;
        this.address2 = builder.address2;
        this.city = builder.city;
        this.country = builder.country;
        this.postalCode = builder.postalCode;
        this.phone = builder.phone;
    }

    public Integer getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public static class Builder {

        private Integer id;
        private String address;
        private String address2;
        private String city;
        private String country;
        private String postalCode;
        private String phone;

        public Builder withId(Integer id) {
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

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
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

        public AddressDTO build() {
            return new AddressDTO(this);
        }
    }
}
