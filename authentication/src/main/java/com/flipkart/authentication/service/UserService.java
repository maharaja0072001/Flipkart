package com.flipkart.authentication.service;

import com.flipkart.authentication.model.User;

import java.util.Optional;

/**
 * <p>
 * Provides the service for the User.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public interface UserService {

    /**
     * <p>
     * Checks if the user already exists. If not then creates a new user.
     * </p>
     *
     * @return true if the user created or false if user already exists.
     * @param user Refers the {@link User}to be created.
     */
    boolean createUser(final User user);

    /**
     * <p>
     * Gets the existing user by the given credentials.
     * </p>
     *
     * @param emailIdOrMobileNumber Refers the email id or mobile number of the user
     * @param password Refers the password of the user.
     * @return Optional of {@link User} if the credentials are correct or null otherwise.
     */
    Optional<User> getUser(final String emailIdOrMobileNumber, final String password);

    /**
     * <p>
     * Updates the details of the user.
     * </p>
     *
     * @param user Refers the current {@link User}.
     */
    void updateUser(final User user);

    /**
     * <p>
     * Gets the user by id.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return Optional of {@link User}.
     */
    Optional<User> getUserById(final int userId);
}
