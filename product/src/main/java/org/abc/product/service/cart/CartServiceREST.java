package org.abc.product.service.cart;

import org.abc.product.ProductCategory;
import org.abc.product.model.cart.Cart;

public interface CartServiceREST {

    /**
     * <p>
     * Adds the specific product to the cart.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be added to the cart.
     * @param productCategory Refers the product category.
     * @return true if the product is added.
     */
    boolean addItem(final int productId, final int userId, final ProductCategory productCategory);

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
