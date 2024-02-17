package org.abc.product.dao.cart.impl;

import jakarta.persistence.Query;

import org.abc.product.dao.cart.CartDAO;
import org.abc.product.exceptions.ItemNotFoundException;
import org.abc.product.exceptions.ItemRemovalFailedException;
import org.abc.product.model.cart.Cart;
import org.abc.product.model.product.Clothes;
import org.abc.product.model.product.Laptop;
import org.abc.product.model.product.Mobile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Stores all the cart details in the database.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class CartDAOImpl2 implements CartDAO {

    private static CartDAOImpl2 cartDAO;
    private final SessionFactory sessionFactory;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of the CartDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private CartDAOImpl2() {
        sessionFactory = new Configuration().buildSessionFactory();
        logger = LogManager.getLogger(CartDAOImpl2.class);
    }

    /**
     * <p>
     * Creates a single object of CartDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of CartDAOImpl Class.
     */
    public static CartDAOImpl2 getInstance() {
        return Objects.isNull(cartDAO) ? cartDAO = new CartDAOImpl2() : cartDAO;
    }

    /**
     * <p>
     * Adds the product to the cart in the database.
     * </p>
     *
     * @param productId Refers the id of the product to be added.
     * @param userId Refers the user id.
     * @return true is the product is added to the cart.
     */
    @Override
    public boolean addItem(final int productId, final int userId) {
        try (Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery("insert into cart (user_id , product_id) values(?,?)");

            session.beginTransaction();
            query.setParameter(1, userId);
            query.setParameter(2, productId);
            final int updatedRows = query.executeUpdate();

            session.getTransaction().commit();

            return updatedRows > 0;
        } catch (Exception exception) {
            logger.info(String.format("User id :%d Product Id :%d - Item is already in the cart", userId, productId));

            return false;
        }
    }

    /**
     * <p>
     * Removes the product from the cart in the database.
     * </p>
     *
     * @param productId Refers the id of the product to be removed from the cart.
     * @param userId Refers the user id.
     */
    @Override
    public void removeItem(final int productId, final int userId) {
        try (Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery("delete from cart where user_id =? and product_id =?");

            session.beginTransaction();
            query.setParameter(1, userId);
            query.setParameter(2, productId);
            query.executeUpdate();
            session.getTransaction().commit();
            logger.info(String.format("User id :%d Product Id :%d - Item removed from the cart", userId, productId));
        } catch (Exception exception) {
            logger.info(String.format("User id :%d Product Id :%d - Item can't be removed", userId, productId));
            throw new ItemRemovalFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the cart to the user from the database.
     * </p>
     *
     * @param userId Refers the user id.
     * @return {@link Cart} of the user.
     */
    @Override
    public Cart getCart(final int userId) {
        try (Session session = sessionFactory.openSession()) {
            final String sqlQuery = String.join(" ", "select cart.product_id, p.product_category_id,",
                    "e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand, p.quantity from cart join product p",
                    "on cart.product_id=p.id left join electronics_inventory e on cart.product_id = e.product_id",
                    "left join clothes_inventory c on p.id=c.product_id where cart.user_id = ?");
            final List<Object[]> resultList = session
                    .createNativeQuery(sqlQuery, Object[].class).setParameter(1, userId)
                    .getResultList();

            return getCartFromResultList(resultList);
        } catch (Exception exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }


    }

    /**
     * <p>
     * Gets the cart to the user from the database.
     * </p>
     *
     * @param userId Refers the user id.
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @return {@link Cart} of the user.
     */
    @Override
    public Cart getCart(int userId, int page, int limit) {
        try (Session session = sessionFactory.openSession()) {
            final String sqlQuery = String.join(" ", "select cart.product_id, p.product_category_id,",
                    "e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand, p.quantity from cart join product p",
                    "on cart.product_id=p.id left join electronics_inventory e on cart.product_id = e.product_id",
                    "left join clothes_inventory c on p.id=c.product_id where cart.user_id = :userId");
            final List<Object[]> resultList = session.createNativeQuery(sqlQuery, Object[].class).setParameter("userId", userId).setFirstResult(page).setMaxResults(limit)
                    .getResultList();

            return getCartFromResultList(resultList);
        } catch (Exception exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the cart from the provided result list and returns it.
     * </p>
     *
     * @param resultList Refers the result list.
     * @return {@link Cart} of the user.
     */
    private Cart getCartFromResultList(final List<Object[]> resultList) {
        final Cart cart = new Cart();

        for (final Object[] row : resultList) {
            final long productId = (Long) row[0];
            final int productCategoryId = (Integer) row[1];
            final String brand = (String) row[2];
            final String model = (String) row[3];
            final double price = (double) row[4];
            final String clothesType =  (String) row[5];
            final String size = (String) row[6];
            final String gender = (String) row[7];
            final String clothesBrand = (String) row[8];
            final int quantity = (int) row[9];

            switch (productCategoryId) {
                case 1 -> cart.addItem(new Mobile(brand, model, price, quantity, (int) productId));
                case 2 -> cart.addItem(new Laptop(brand, model, price, quantity, (int) productId));
                case 3 -> cart.addItem(new Clothes(clothesType, clothesBrand, gender, size, price, quantity, (int) productId));
            }
        }

        return cart;
    }
}
