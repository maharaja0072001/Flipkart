package com.flipkart.product.dao.wishlist.v2;

import com.flipkart.product.dao.wishlist.WishlistDAO;
import com.flipkart.product.model.wishlist.Wishlist;
import com.flipkart.authentication.exceptions.UserCheckFailedException;
import com.flipkart.product.exceptions.ItemAdditionFailedException;
import com.flipkart.product.exceptions.ItemNotFoundException;
import com.flipkart.product.exceptions.ItemRemovalFailedException;
import com.flipkart.product.model.product.Clothes;
import com.flipkart.product.model.product.Laptop;
import com.flipkart.product.model.product.Mobile;

import jakarta.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Provides DAO for wishlist.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class WishlistDAOImpl implements WishlistDAO {

    private final SessionFactory sessionFactory;

    /**
     * <p>
     * Default constructor of the WishlistDAOImpl2 class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private WishlistDAOImpl() {
        sessionFactory = new Configuration().buildSessionFactory();
    }

    /**
     * <p>
     * Creates a single instance of WishlistDAOImpl2 class.
     * </p>
     */
    private static class InstanceHolder {

        private static final WishlistDAOImpl WISHLIST_DAO = new WishlistDAOImpl();
    }

    /**
     * <p>
     * Gets a single object of WishlistDAOImpl2 Class and returns it.
     * </p>
     *
     * @return returns the single instance of WishlistDAOImpl2 Class.
     */
    public static WishlistDAOImpl getInstance() {
        return InstanceHolder.WISHLIST_DAO;
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
    public boolean addProduct(final int productId, final int userId) {
        try (final Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery("insert into wishlist (user_id , product_id) values(?,?)");

            session.beginTransaction();
            query.setParameter(1, userId);
            query.setParameter(2, productId);
            final int updatedRows = query.executeUpdate();

            session.getTransaction().commit();
            session.close();

            return updatedRows > 0;
        } catch (Exception exception) {
            throw new ItemAdditionFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Removes the product from the wishlist of the specified user.
     * * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId Refers the user id.
     * @return true if item removed
     */
    @Override
    public boolean removeProduct(final int productId, final int userId) {
        try (final Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery("delete from wishlist where user_id =? and product_id =?");

            session.beginTransaction();
            query.setParameter(1, userId);
            query.setParameter(2, productId);
            final int affectedRows = query.executeUpdate();

            session.getTransaction().commit();

            return affectedRows > 0;
        } catch (Exception exception) {
            throw new ItemRemovalFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the wishlist of the specified user id and returns it.
     * </p>
     *
     * @param page Refers the page number.
     * @param userId Refers the id of the user who owns the cart.
     * @return Optional of {@link Wishlist} of the user.
     */
    @Override
    public Optional<Wishlist> getWishlist(final int userId, final int page) {
        final int limit = 5;

        try (final Session session = sessionFactory.openSession()) {
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
     * Gets the wishlist from the provided result list and returns it.
     * </p>
     *
     * @param resultList Refers the result list.
     * @return Optional of {@link Wishlist} of the user.
     */
    private Optional<Wishlist> getWishlistFromResultList(final List<Object[]> resultList) {
        if (resultList.isEmpty()) {
            return Optional.empty();
        }
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
                case 1 -> wishlist.addProduct(new Mobile(brand, model, price, quantity, (int) productId));
                case 2 -> wishlist.addProduct(new Laptop(brand, model, price, quantity, (int) productId));
                case 3 -> wishlist.addProduct(new Clothes(clothesType, clothesBrand, gender, size, price, quantity, (int) productId));
            }
        }

        return Optional.of(wishlist);
    }

    /**
     * <p>
     * Checks whether the product exists in wishlist or not.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @param productId Refers the product id.
     * @return true if the product already exists or false otherwise.
     */
    @Override
    public boolean isProductExist(final int userId, final int productId) {
        try (final Session session = sessionFactory.openSession()) {
            final Query query = session.createNativeQuery(
                    "select count(id) from cart wishlist wishlist.user_id = :userId or wishlist.product_id = :productId", Integer.class);

            query.setParameter("userId", userId);
            query.setParameter("productId", productId);
            final Integer count = (Integer) query.getSingleResult();

            return count > 0;
        } catch (Exception exception) {
            throw new UserCheckFailedException(exception.getMessage());
        }
    }
}