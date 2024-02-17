package org.abc.authentication.dao.impl;

import jakarta.persistence.Query;

import org.abc.authentication.dao.UserDAO;
import org.abc.authentication.exceptions.UpdateActionFailedException;
import org.abc.authentication.exceptions.UserNotFoundException;
import org.abc.authentication.model.User;
import org.abc.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Objects;

public class UserDAOImpl2 implements UserDAO {

    private static UserDAOImpl2 userDAO;
    private final SessionFactory sessionFactory;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of the UserDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private UserDAOImpl2() {
        sessionFactory = new Configuration().addAnnotatedClass(User.class).buildSessionFactory();
        logger = LogManager.getLogger(UserDAOImpl2.class);
    }

    /**
     * <p>
     * Creates a single object of UserDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of UserDAOImpl class.
     */
    public static UserDAOImpl2 getInstance() {
        return Objects.isNull(userDAO) ? userDAO = new UserDAOImpl2() : userDAO;
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
    public boolean createNewUser(final User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
            logger.info("User account created successfully");

            return true;
        } catch (Exception exception) {
            logger.info("User account Already exists");

            return false;
        }
    }

    /**
     * <p>
     * Gets the existing user by the given credentials.
     * </p>
     *
     * @param emailIdOrMobileNumber Refers the mobile number or email id of the user
     * @param password Refers the password of the user.
     * @return {@link User} if the credentials are correct or null otherwise.
     */
    @Override
    public User getUser(final String emailIdOrMobileNumber, final String password) {
        try (Session session = sessionFactory.openSession()) {
            final String hqlQuery = Validator.getInstance().isValidEmail(emailIdOrMobileNumber)
                    ? "From users where mobileNumber=:username and password=:password"
                    : "From users where emailId=:username and password=:password";
            final Query query = session.createQuery(hqlQuery, User.class);

            query.setParameter("username", emailIdOrMobileNumber);
            query.setParameter("password", password);

            return  (User) query.getSingleResult();
        } catch (Exception exception){
            logger.warn("User not found");
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
    public void updateDetails(final User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
            logger.info(String.format("User : id - %d - User details updated", user.getId()));
        } catch (Exception exception){
            logger.warn(String.format("User update failed for the given id: %d", user.getId()));
            throw new UpdateActionFailedException(exception.getMessage());
        }
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
        try (Session session = sessionFactory.openSession()) {

            return session.get(User.class, userId);
        } catch (Exception exception){
            logger.warn(String.format("User not found for the given id: %d", userId));
            throw new UserNotFoundException(exception.getMessage());
        }
    }
}

