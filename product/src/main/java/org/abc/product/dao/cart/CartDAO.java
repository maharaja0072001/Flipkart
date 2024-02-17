package org.abc.product.dao.cart;

import org.abc.product.model.cart.Cart;

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
     * Gets the cart of the current user and returns it.
     * </p>
     *
     * @param userId Refers the user id.
     * @return {@link Cart} of the user.
     */
    Cart getCart(final int userId);

    /**
     * <p>
     * Adds the specific product to the cart.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be added to the cart.
     * @return true if the product is added.
     */
    boolean addItem(final int productId, final int userId);

    /**
     * <p>
     * Removes the specific product from the cart.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be removed from the cart.
     */
    void removeItem(final int productId, final int userId);

    /**
     * <p>
     * Gets the cart of the current user and returns it.
     * </p>
     *
     * @param userId Refers the user id.
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @return {@link Cart} of the user.
     */
    Cart getCart(final int userId, final int page, final int limit);
}
