package com.c195.model;

public class Country {

    private final int id;
    private final String country;
    private final Metadata metadata;

    public Country(Builder builder) {
        this.id = builder.id;
        this.country = builder.country;
        this.metadata = builder.metadata;
    }

    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public static class Builder {

        private int id;
        private String country;
        private Metadata metadata;

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder withMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Country build() {
            return new Country(this);
        }
    }
}
