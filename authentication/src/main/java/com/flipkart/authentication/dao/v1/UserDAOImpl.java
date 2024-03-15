package com.flipkart.authentication.dao.v1;

import com.flipkart.authentication.exceptions.UpdateActionFailedException;
import com.flipkart.authentication.exceptions.UserCheckFailedException;
import com.flipkart.authentication.exceptions.UserNotFoundException;
import com.flipkart.authentication.model.User;
import com.flipkart.authentication.dao.UserDAO;
import com.flipkart.database.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * <p>
 * Stores and gets the user details from the database.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class UserDAOImpl implements UserDAO {

    private final Connection connection;

    /**
     * <p>
     * Default constructor of the UserDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private UserDAOImpl() {
        connection = DatabaseConnection.get();
    }

    /**
     * <p>
     * Creates a single instance of UserDAOImpl class.
     * </p>
     */
    private static class InstanceHolder {

        private static final UserDAOImpl USER_DAO = new UserDAOImpl();
    }

    /**
     * <p>
     * Gets a single object of UserDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of UserDAOImpl class.
     */
    public static UserDAOImpl getInstance() {
        return InstanceHolder.USER_DAO;
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
        try (final PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "insert into users(name, mobile_number, email, password)",
                "values(?, ?, ?, ?) returning id"))) {
            connection.setAutoCommit(true);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getMobileNumber());
            preparedStatement.setString(3, user.getEmailId());
            preparedStatement.setString(4, user.getPassword());
            final ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            user.setId(resultSet.getInt(1));

            return true;
        } catch (final SQLException exception) {
            throw new UserNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the existing user by the given credentials.
     * </p>
     *
     * @param emailIdOrMobileNumber Refers the mobile number or email id of the user
     * @param password Refers the password of the user.
     * @return Optional of {@link User} if the credentials are correct or null otherwise.
     */
    @Override
    public Optional<User> getUser(final String emailIdOrMobileNumber, final String password, final String query) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            connection.setAutoCommit(true);
            preparedStatement.setString(1, emailIdOrMobileNumber);
            preparedStatement.setString(2, password);

            return Optional.ofNullable(getUserObjectFromResultSet(preparedStatement.executeQuery()));
        } catch (final SQLException exception) {
            throw new UserNotFoundException(exception.getMessage());
        }
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
        try (final PreparedStatement preparedStatement = connection.prepareStatement(String.join(" ","update",
                "users set name=? ,email=?, password=?, mobile_number=? where id =?"))) {
            connection.setAutoCommit(true);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmailId());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getMobileNumber());
            preparedStatement.setInt(5, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new UpdateActionFailedException(exception.getMessage());
        }
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
    public Optional<User> getUserById(int userId) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(String.join(" ","select",
                "id, name, mobile_number, email, password from users where id =?"))) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, userId);

            return Optional.ofNullable(getUserObjectFromResultSet(preparedStatement.executeQuery()));
        } catch (final SQLException exception) {
            throw new UserNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the user object from the provided resultset.
     * </p>
     *
     * @param resultSet Refers the Resultset.
     * @return {@link User}.
     */
    private User getUserObjectFromResultSet(final ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        final User user = new User();

        user.setId(resultSet.getInt(1));
        user.setName(resultSet.getString(2));
        user.setMobileNumber(resultSet.getString(3));
        user.setEmailId(resultSet.getString(4));
        user.setPassword(resultSet.getString(5));

        return user;
    }

    /**
     * <p>
     * Checks whether the user exists or not.
     * </p>
     *
     * @param emailId Refers the email id of the user.
     * @param mobile_number Refers the mobile number of the user.
     * @return true if the user already exists or false otherwise.
     */
    @Override
    public boolean isUserExist(final String emailId, final String mobile_number) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("select count(id) from users where email = ? or mobile_number = ?")) {
            preparedStatement.setString(1, emailId);
            preparedStatement.setString(2, mobile_number);
            final ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();

            return resultSet.getInt(1) > 0;
        } catch (SQLException exception) {
            throw new UserCheckFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Checks whether the user exists or not.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return true if the user already exists or false otherwise.
     */
    public boolean isUserExist(final int userId) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("select count(id) from users where id = ?")) {
            preparedStatement.setInt(1, userId);
            final ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();

            return resultSet.getInt(1) > 0;
        } catch (SQLException exception) {
            throw new UserCheckFailedException(exception.getMessage());
        }
    }
}
