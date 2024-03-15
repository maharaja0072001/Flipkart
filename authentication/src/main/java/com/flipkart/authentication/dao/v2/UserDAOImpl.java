package com.flipkart.authentication.dao.v2;

import com.flipkart.authentication.exceptions.UpdateActionFailedException;
import com.flipkart.authentication.exceptions.UserCheckFailedException;
import com.flipkart.authentication.exceptions.UserCreationFailedException;
import com.flipkart.authentication.exceptions.UserNotFoundException;
import com.flipkart.authentication.model.User;
import com.flipkart.authentication.dao.UserDAO;

import jakarta.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private final SessionFactory sessionFactory;

    /**
     * <p>
     * Default constructor of the UserDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private UserDAOImpl() {
        sessionFactory = new Configuration().addAnnotatedClass(User.class).buildSessionFactory();
    }

    /**
     * <p>
     * Creates a single instance of UserDAOImpl2 class.
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
        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();

            return true;
        } catch (Exception exception) {
            throw new UserCreationFailedException(exception.getMessage());
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
    public Optional<User> getUser(final String emailIdOrMobileNumber, final String password, final String hqlQuery) {
        try (final Session session = sessionFactory.openSession()) {
            final Query query = session.createQuery(hqlQuery, User.class);

            query.setParameter("username", emailIdOrMobileNumber);
            query.setParameter("password", password);

            return  Optional.ofNullable((User) query.getSingleResult());
        } catch (Exception exception) {
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
        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        } catch (Exception exception) {
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
    public Optional<User> getUserById(final int userId) {
        try (final Session session = sessionFactory.openSession()) {

            return Optional.ofNullable(session.get(User.class, userId));
        } catch (Exception exception) {
            throw new UserNotFoundException(exception.getMessage());
        }
    }

    @Override
    public boolean isUserExist(final String emailId, final String mobile_number) {
        try (final Session session = sessionFactory.openSession()) {
            final Query query = session.createNativeQuery(
                    "select count(id) from users where users.emailId = :emailId or users.mobile_number = :mobileNumber", Integer.class);
            query.setParameter("emailId", emailId);
            query.setParameter("mobileNumber", mobile_number);
            final Integer count = (Integer) query.getSingleResult();

            return count > 0;
        } catch (Exception exception) {
            throw new UserCheckFailedException(exception.getMessage());
        }
    }
}

