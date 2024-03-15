package com.flipkart.product.dao.cart.v2;

import com.flipkart.product.exceptions.ItemNotFoundException;
import com.flipkart.product.model.cart.Cart;
import com.flipkart.product.model.product.Clothes;
import com.flipkart.product.model.product.Laptop;
import com.flipkart.product.model.product.Mobile;
import com.flipkart.authentication.exceptions.UserCheckFailedException;
import com.flipkart.product.dao.cart.CartDAO;
import com.flipkart.product.exceptions.ItemAdditionFailedException;
import com.flipkart.product.exceptions.ItemRemovalFailedException;

import jakarta.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Stores all the cart details in the database.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class CartDAOImpl implements CartDAO {

    private final SessionFactory sessionFactory;

    /**
     * <p>
     * Default constructor of the CartDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private CartDAOImpl() {
        sessionFactory = new Configuration().buildSessionFactory();
    }

    /**
     * <p>
     * Creates a single instance of CartDAOImpl2 class.
     * </p>
     */
    private static class InstanceHolder {

        private static final CartDAOImpl CART_DAO = new CartDAOImpl();
    }

    /**
     * <p>
     * Gets a single object of CartDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of CartDAOImpl Class.
     */
    public static CartDAOImpl getInstance() {
        return InstanceHolder.CART_DAO;
    }

    /**
     * <p>
     * Adds the product to the cart in the database.
     * </p>
     *
     * @param productId Refers the id of the product to be added.
     * @param userId    Refers the user id.
     * @return true if the product is added to the cart.
     */
    @Override
    public boolean addProduct(final int productId, final int userId) {
        try (final Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery("insert into cart (user_id , product_id) values(?,?)");

            session.beginTransaction();
            query.setParameter(1, userId);
            query.setParameter(2, productId);
            final int updatedRows = query.executeUpdate();

            session.getTransaction().commit();

            return updatedRows > 0;
        } catch (Exception exception) {
            throw new ItemAdditionFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Removes the product from the cart in the database.
     * </p>
     *
     * @param productId Refers the id of the product to be removed from the cart.
     * @param userId    Refers the user id.
     * @return true if item removed
     */
    @Override
    public boolean removeProduct(final int productId, final int userId) {
        try (final Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery("delete from cart where user_id =? and product_id =?");

            session.beginTransaction();
            query.setParameter(1, userId);
            query.setParameter(2, productId);
            final int updatedRows = query.executeUpdate();

            session.getTransaction().commit();

            return updatedRows > 0;
        } catch (Exception exception) {
            throw new ItemRemovalFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the cart to the user from the database.
     * </p>
     *
     * @param userId Refers the user id.
     * @param page   Refers the page number.
     * @return Optional of {@link Cart} of the user.
     */
    @Override
    public Optional<Cart> getCart(final int userId, final int page) {
        final int limit = 5;

        try (final Session session = sessionFactory.openSession()) {
            final String sqlQuery = String.join(" ", "select cart.product_id, p.product_category_id,",
                    "e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand, p.quantity from cart join product p",
                    "on cart.product_id=p.id left join electronics_inventory e on cart.product_id = e.product_id",
                    "left join clothes_inventory c on p.id=c.product_id where cart.user_id = :userId");
            final List<Object[]> resultList = session.createNativeQuery(sqlQuery, Object[].class)
                    .setParameter("userId", userId).setFirstResult(page).setMaxResults(limit).getResultList();

            return getCartFromResultList(resultList);
        } catch (Exception exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
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
                    "select count(id) from cart where cart.user_id = :userId or cart.product_id = :productId", Integer.class);

            query.setParameter("userId", userId);
            query.setParameter("productId", productId);
            final Integer count = (Integer) query.getSingleResult();

            return count > 0;
        } catch (Exception exception) {
            throw new UserCheckFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the cart from the provided result list and returns it.
     * </p>
     *
     * @param resultList Refers the result list.
     * @return Optional of {@link Cart} of the user.
     */
    private Optional<Cart> getCartFromResultList(final List<Object[]> resultList) {
        if (resultList.isEmpty()) {
            return Optional.empty();
        }
        final Cart cart = new Cart();

        for (final Object[] row : resultList) {
            final long productId = (Long) row[0];
            final int productCategoryId = (Integer) row[1];
            final String brand = (String) row[2];
            final String model = (String) row[3];
            final double price = (double) row[4];
            final String clothesType = (String) row[5];
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

        return Optional.of(cart);
    }
}
