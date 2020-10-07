package com.c195.common;

public class UserDTO {

    private final Integer id;
    private final String username;
    private final boolean isActive;

    private UserDTO(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.isActive = builder.isActive;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isActive() {
        return isActive;
    }

    public static class Builder {

        private Integer id;
        private String username;
        private boolean isActive;

        public Builder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withActive(boolean active) {
            this.isActive = active;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(this);
        }
    }
}
