package com.flipkart.product.service.cart;

import com.flipkart.authentication.dao.v1.UserDAOImpl;
import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.dao.cart.CartDAO;
import com.flipkart.product.dao.cart.v1.CartDAOImpl;
import com.flipkart.product.dao.inventory.v1.InventoryDAOImpl;
import com.flipkart.product.model.cart.Cart;

import java.util.Optional;

/**
 * <p>
 * Provides the service for the Cart of the user.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class CartServiceImpl implements CartService {

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
     * Creates a single instance of CartServiceImpl class.
     * </p>
     */
    private static class InstanceHolder {

        private static final CartServiceImpl CART_SERVICE = new CartServiceImpl();
    }

    /**
     * <p>
     * Gets a single object of CartServiceImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of CartServiceImpl Class.
     */
    public static CartService getInstance() {
        return InstanceHolder.CART_SERVICE;
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
    public boolean addProduct(final int productId, final int userId, final ProductCategory productCategory) {
        return !cartDAO.isProductExist(productId, userId) && cartDAO.addProduct(productId, userId);
    }

    /**
     * <p>
     * Removes the specific product from the cart.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be removed from the cart.
     * @return true if item removed
     */
    @Override
    public boolean removeProduct(final int productId, final int userId) {
        return cartDAO.removeProduct(productId, userId);
    }

    /**
     * <p>
     * Gets the cart of the current user and returns it.
     * </p>
     *
     * @param userId Refers the user id.
     * @param page Refers the page number.
     * @return Optional of {@link Cart} of the user.
     */
    @Override
    public Optional<Cart> getCart(final int userId, final int page) {
        return cartDAO.getCart(userId, page);
    }

    /**
     * <p>
     * Checks whether the user exists or not.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return true if the user already exists or false otherwise.
     */
    @Override
    public boolean isUserExist(final int userId) {
        return UserDAOImpl.getInstance().isUserExist(userId);
    }

    /**
     * <p>
     * Checks whether the product exists or not.
     * </p>
     *
     * @param productId Refers the id of the product.
     * @return true if the user already exists or false otherwise.
     */
    @Override
    public boolean isProductExist(final int productId) {
        return InventoryDAOImpl.getInstance().isProductExist(productId);
    }
}
