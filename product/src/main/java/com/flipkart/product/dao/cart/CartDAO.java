package com.flipkart.product.dao.cart;

import com.flipkart.product.model.cart.Cart;

import java.util.Optional;

/**
 * <p>
 * Provides service for the CartDAO.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public interface CartDAO {

    /**
     * <p>
     * Adds the specific product to the cart.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be added to the cart.
     * @return true if the product is added.
     */
    boolean addProduct(final int productId, final int userId);

    /**
     * <p>
     * Removes the specific product from the cart.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be removed from the cart.
     * @return true if item  removed
     */
    boolean removeProduct(final int productId, final int userId);

    /**
     * <p>
     * Gets the cart of the current user and returns it.
     * </p>
     *
     * @param userId Refers the user id.
     * @param page Refers the page number.
     * @return Optional of {@link Cart} of the user.
     */
    Optional<Cart> getCart(final int userId, final int page);

    /**
     * <p>
     * Checks whether the product exists in cart or not.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @param productId Refers the product id.
     * @return true if the product already exists or false otherwise.
     */
    boolean isProductExist(final int userId, final int productId);
}
