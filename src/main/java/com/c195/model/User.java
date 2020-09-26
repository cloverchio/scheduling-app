package com.c195.model;

public class User {

    private final int id;
    private final String username;
    private final String password;
    private final boolean isActive;
    private final Metadata metadata;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActive() {
        return isActive;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.isActive = builder.isActive;
        this.metadata = builder.metadata;
    }

    public static class Builder {

        private int id;
        private String username;
        private String password;
        private boolean isActive;
        private Metadata metadata;

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
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

        public User build() {
            return new User(this);
        }
    }
}
