package org.abc.product.dao.wishlist.impl;

import jakarta.persistence.Query;

import org.abc.product.dao.wishlist.WishlistDAO;
import org.abc.product.exceptions.ItemNotFoundException;
import org.abc.product.exceptions.ItemRemovalFailedException;
import org.abc.product.model.product.Clothes;
import org.abc.product.model.product.Laptop;
import org.abc.product.model.product.Mobile;
import org.abc.product.model.wishlist.Wishlist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Provides DAO for wishlist.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class WishlistDAOImpl2 implements WishlistDAO {

    private static WishlistDAOImpl2 wishlistDAO;
    private final SessionFactory sessionFactory;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of the WishlistDAOImpl2 class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private WishlistDAOImpl2() {
        sessionFactory = new Configuration().buildSessionFactory();
        logger = LogManager.getLogger(WishlistDAOImpl2.class);
    }

    /**
     * <p>
     * Creates a single object of WishlistDAOImpl2 Class and returns it.
     * </p>
     *
     * @return returns the single instance of WishlistDAOImpl2 Class.
     */
    public static WishlistDAOImpl2 getInstance() {
        return Objects.isNull(wishlistDAO) ? wishlistDAO = new WishlistDAOImpl2() : wishlistDAO;
    }

    /**
     * <p>
     * Adds the product to the wishlist of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be added
     * @param userId Refers the user id.
     * @return the wishlist of the user.
     */
    @Override
    public boolean addItem(int productId, int userId) {
        try (Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery("insert into wishlist (user_id , product_id) values(?,?)");

            session.beginTransaction();
            query.setParameter(1, userId);
            query.setParameter(2, productId);
            final int updatedRows = query.executeUpdate();

            session.getTransaction().commit();
            session.close();

            return updatedRows > 0;
        } catch (Exception exception) {
            logger.info(String.format("User id :%d Product Id :%d - Item is already in the wishlist", userId, productId));

            return false;
        }
    }

    /**
     * <p>
     * Removes the product from the wishlist of the specified user.
     * * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId Refers the user id.
     */
    @Override
    public void removeItem(final int productId, final int userId) {
        try (Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery("delete from wishlist where user_id =? and product_id =?");

            session.beginTransaction();
            query.setParameter(1, userId);
            query.setParameter(2, productId);
            query.executeUpdate();
            session.getTransaction().commit();
            logger.info(String.format("User id :%d Product Id :%d - Item removed from the wishlist", userId, productId));
        } catch (Exception exception) {
            logger.info(String.format("User id :%d Product Id :%d - Item can't be removed from the wishlist", userId, productId));
            throw new ItemRemovalFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the wishlist of the specified user id and returns it.
     * </p>
     *
     * @param limit Refers the limit of data to show.
     * @param page Refers the page number.
     * @param userId Refers the id of the user who owns the cart.
     * @return the {@link Wishlist} of the user.
     */
    @Override
    public Wishlist getWishlist(int userId, int page, int limit) {
        try (Session session = sessionFactory.openSession()) {
            final String sqlQuery = String.join(" ", "select w.product_id,",
                    "p.product_category_id, e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand ,p.quantity",
                    "from wishlist w join product p on w.product_id=p.id left join electronics_inventory e on",
                    "w.product_id = e.product_id left join clothes_inventory c on p.id=c.product_id where w.user_id = ?");
            final List<Object[]> resultList = session.createNativeQuery(sqlQuery, Object[].class).setParameter(1, userId).setFirstResult(page).setMaxResults(limit)
                    .getResultList();

            return getWishlistFromResultList(resultList);
        } catch (Exception exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }

    }

    /**
     * <p>
     * Gets the wishlist of the specified user id and returns it.
     * </p>
     *
     * @param userId Refers the id of the user who owns the cart.
     * @return the wishlist of the user.
     */
    @Override
    public Wishlist getWishlist(final int userId) {
        try (Session session = sessionFactory.openSession()) {
            final String sqlQuery = String.join(" ", "select w.product_id,",
                    "p.product_category_id, e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand ,p.quantity",
                    "from wishlist w join product p on w.product_id=p.id left join electronics_inventory e on",
                    "w.product_id = e.product_id left join clothes_inventory c on p.id=c.product_id where w.user_id = ?");
            final List<Object[]> resultList = session
                    .createNativeQuery(sqlQuery, Object[].class).setParameter("userId", userId)
                    .getResultList();

            return getWishlistFromResultList(resultList);
        } catch (Exception exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the wishlist from the provided result list and returns it.
     * </p>
     *
     * @param resultList Refers the result list.
     * @return {@link Wishlist} of the user.
     */
    private Wishlist getWishlistFromResultList(final List<Object[]> resultList) {
        final Wishlist wishlist = new Wishlist();

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
                case 1 -> wishlist.addItem(new Mobile(brand, model, price, quantity, (int) productId));
                case 2 -> wishlist.addItem(new Laptop(brand, model, price, quantity, (int) productId));
                case 3 -> wishlist.addItem(new Clothes(clothesType, clothesBrand, gender, size, price, quantity, (int) productId));
            }
        }

        return wishlist;
    }
}