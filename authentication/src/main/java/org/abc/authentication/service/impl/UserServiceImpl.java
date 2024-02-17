package org.abc.authentication.service.impl;

import org.abc.authentication.model.User;
import org.abc.authentication.service.UserService;
import org.abc.validation.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * Provides the service for the User.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class UserServiceImpl implements UserService {

    private static UserServiceImpl userService;
    private final Map<String, User> users;

    /**
     * <p>
     * Default constructor of the UserServiceImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private UserServiceImpl() {
        users = new HashMap<>();
    }

    /**
     * <p>
     * Creates a single object of UserServiceImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of UserServiceImpl Class.
     */
    public static UserService getInstance() {
        return Objects.isNull(userService) ? userService = new UserServiceImpl() : userService;
    }

    /**
     * <p>
     * Checks if the user already exists. if not then creates a new user.
     * </p>
     *
     * @return true if the user created or false if user already exists.
     * @param user Refers the {@link User}to be created.
     */
    @Override
    public boolean createUser(final User user) {
        if (users.containsKey(user.getMobileNumber())) {
            return false;
        } else {
            users.put(user.getMobileNumber(), user);

            return true;
        }
    }

    /**
     * <p>
     * Gets the user by the given credentials.
     * </p>
     *
     * @param emailIdOrMobileNumber Refers the mobile number or email id of the user.
     * @param password Refers the password of the user.
     * @return {@link User} if the credentials are correct and the user exists or null otherwise.
     */
    @Override
    public User getUser(final String emailIdOrMobileNumber, final String password) {
        User user;

        if (Validator.getInstance().isValidEmail(emailIdOrMobileNumber)) {
            user = users.values().stream().filter(existingUser -> existingUser.getEmailId().equals(emailIdOrMobileNumber)).findFirst().orElse(null);
        } else {
            user = users.get(emailIdOrMobileNumber);
        }

        return Objects.nonNull(user) && (user.getPassword().equals(password)) ? user : null;
    }

    /**
     * <p>
     * Updates the details of the user.
     * </p>
     *
     * @param user Refers the current {@link User}.
     */
    @Override
    public void updateDetails(final User user) {
        users.put(user.getMobileNumber(), user);
    }

    /**
     * <p>
     * Gets the user by id.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return {@link User}.
     */
    @Override
    public User getUserById(final int userId) {
        return users.values().stream().filter(user -> user.getId() == userId).findFirst().orElse(null);
    }
}