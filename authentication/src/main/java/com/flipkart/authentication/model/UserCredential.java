package com.flipkart.authentication.model;

import jakarta.validation.constraints.NotNull;

/**
 * <p>
 * Represents the credentials of the user.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class UserCredential {

    @NotNull(message = "username can't be null")
    private String username;
    @NotNull(message = "password can't be null")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
