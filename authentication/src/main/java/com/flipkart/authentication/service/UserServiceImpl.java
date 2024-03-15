package com.flipkart.authentication.service;

import com.flipkart.authentication.dao.UserDAO;
import com.flipkart.authentication.dao.v1.UserDAOImpl;
import com.flipkart.authentication.model.User;

import java.util.Optional;

/**
 * <p>
 * Provides the implementation for the USerService.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    /**
     * <p>
     * Default constructor of the UserServiceImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private UserServiceImpl() {
        userDAO = UserDAOImpl.getInstance();
    }

    /**
     * <p>
     * Creates a single instance of UserServiceImpl2 class.
     * </p>
     */
    private static class InstanceHolder {

        private static final UserServiceImpl USER_SERVICE_IMPL = new UserServiceImpl();
    }

    /**
     * <p>
     * Gets a single object of UserServiceImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of UserServiceImpl Class.
     */
    public static UserService getInstance() {
        return InstanceHolder.USER_SERVICE_IMPL;
    }

    /**
     * <p>
     * Checks if the user already exists, if not then creates a new user.
     * </p>
     *
     * @return true if the user created or false if user already exists.
     * @param user Refers the {@link User}to be created.
     */
    @Override
    public boolean createUser(final User user) {
        return !userDAO.isUserExist(user.getEmailId(), user.getMobileNumber()) && userDAO.createUser(user);
    }

    /**
     * <p>
     * Gets the existing user by the given credentials.
     * </p>
     *
     * @param emailIdOrMobileNumber Refers the mobile number or email id of the user.
     * @param password Refers the password of the user.
     * @return Optional of {@link User} if the credentials are correct and the user exists or null otherwise.
     */
    @Override
    public Optional<User> getUser(final String emailIdOrMobileNumber, final String password) {
        final String query = emailIdOrMobileNumber.matches("^[1-9]\d*$")
                ? "select id, name, mobile_number, email, password from users where mobile_number=? and password=?"
                : "select id, name, mobile_number, email, password from users where email=? and password=?";

        return userDAO.getUser(emailIdOrMobileNumber, password, query);
    }

    /**
     * <p>
     * Updates the details of the user.
     * </p>
     *
     * @param user Refers the current {@link User}.
     */
    @Override
    public void updateUser(final User user) {
        userDAO.updateUser(user);
    }

    /**
     * <p>
     * Gets the user by id.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return Optional of {@link User}.
     */
    @Override
    public Optional<User> getUserById(final int userId) {
        return userDAO.getUserById(userId);
    }
}
