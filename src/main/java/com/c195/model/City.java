package com.c195.model;

public class City {

    private final int id;
    private final String city;
    private final Country country;
    private final Metadata metadata;

    private City(Builder builder) {
        this.id = builder.id;
        this.city = builder.city;
        this.country = builder.country;
        this.metadata = builder.metadata;
    }

    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public Country getCountry() {
        return country;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public static class Builder {

        private int id;
        private String city;
        private Country country;
        private Metadata metadata;

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withCountry(Country country) {
            this.country = country;
            return this;
        }

        public Builder withMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public City build() {
            return new City(this);
        }
    }
}
