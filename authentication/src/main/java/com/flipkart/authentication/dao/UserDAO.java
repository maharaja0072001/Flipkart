package com.flipkart.authentication.dao;

import com.flipkart.authentication.model.User;

import java.util.Optional;

/**
 * <p>
 * Provides the abstraction for user DAO.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public interface UserDAO {

    /**
     * <p>
     * Checks if the user already exists, if not then creates a new user.
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
     * @param emailIdOrMobileNumber Refers the mobile number or email id of the user.
     * @param password Refers the password of the user.
     * @param query Refers the query in String.
     * @return Optional of {@link User} if the credentials are correct or null otherwise.
     */
    Optional<User> getUser(final String emailIdOrMobileNumber, final String password, final String query);

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

    /**
     * <p>
     * Checks whether the user exists or not.
     * </p>
     *
     * @param emailId Refers the email id of the user.
     * @param mobile_number Refers the mobile number of the user.
     * @return true if the user already exists or false otherwise.
     */
    boolean isUserExist(final String emailId, final String mobile_number);
}
