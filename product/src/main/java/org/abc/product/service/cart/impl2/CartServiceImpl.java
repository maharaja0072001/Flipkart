package org.abc.product.service.cart.impl2;

import org.abc.product.ProductCategory;
import org.abc.product.dao.cart.CartDAO;
import org.abc.product.dao.cart.impl.CartDAOImpl;
import org.abc.product.model.cart.Cart;
import org.abc.product.service.cart.CartService;
import org.abc.product.service.cart.CartServiceREST;

import java.util.Objects;

/**
 * <p>
 * Provides the service for the Cart of the user.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class CartServiceImpl implements CartServiceREST, CartService {

    private static CartServiceImpl cartService;
    private final CartDAO cartDAO;

    /**
     * <p>
     * Default constructor of the CartServiceImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private CartServiceImpl() {
        cartDAO = CartDAOImpl.getInstance();
    }

    /**
     * <p>
     * Creates a single object of CartServiceImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of CartServiceImpl Class.
     */
    public static CartServiceREST getInstance() {
        return Objects.isNull(cartService) ? cartService = new CartServiceImpl() : cartService;
    }

    /**
     * <p>
     * Adds the product to the cart of the user.
     * </p>
     *
     * @param productId Refers the id of the product to be added to the cart.
     * @param userId Refers the user id.
     * @param productCategory Refers the product category.
     * @return true if product added to the cart
     */
    @Override
    public boolean addItem(final int productId, final int userId, final ProductCategory productCategory) {
        return cartDAO.addItem(productId, userId);
    }

    /**
     * <p>
     * Removes the specific product from the cart.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be removed from the cart.
     */
    @Override
    public void removeItem(final int productId, final int userId) {
        cartDAO.removeItem(productId, userId);
    }

    /**
     * <p>
     * Gets the cart of the current user and returns it.
     * </p>
     *
     * @param userId Refers the user id.
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @return the {@link Cart} of the user.
     */
    @Override
    public Cart getCart(final int userId, final int page, final int limit) {
        return cartDAO.getCart(userId, page, limit);
    }

    /**
     * <p>
     * Gets the cart of the current user and returns it.
     * </p>
     *
     * @param userId Refers the user id.
     * @return the {@link Cart} of the user.
     */
    @Override
    public Cart getCart(int userId) {
        return cartDAO.getCart(userId);
    }
}
