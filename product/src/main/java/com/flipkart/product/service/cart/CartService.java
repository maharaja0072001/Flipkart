package com.flipkart.product.service.cart;

import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.model.cart.Cart;

import java.util.Optional;

public interface CartService {

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
    boolean addProduct(final int productId, final int userId, final ProductCategory productCategory);

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
     * Checks whether the user exists or not.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return true if the user already exists or false otherwise.
     */
    boolean isUserExist(final int userId);

    /**
     * <p>
     * Checks whether the product exists or not.
     * </p>
     *
     * @param productId Refers the id of the product.
     * @return true if the user already exists or false otherwise.
     */
    boolean isProductExist(final int productId);
}
