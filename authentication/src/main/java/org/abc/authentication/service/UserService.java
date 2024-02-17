package org.abc.authentication.service;

import org.abc.authentication.model.User;

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
     * @return {@link User} if the credentials are correct or null otherwise.
     */
    User getUser(final String emailIdOrMobileNumber, final String password);

    /**
     * <p>
     * Updates the details of the user.
     * </p>
     *
     * @param user Refers the current {@link User}.
     */
    void updateDetails(final User user);

    /**
     * <p>
     * Gets the user by id.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return {@link User}.
     */
    User getUserById(final int userId);
}
