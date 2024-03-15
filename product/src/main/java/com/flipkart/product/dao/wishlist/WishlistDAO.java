package com.flipkart.product.dao.wishlist;

import com.flipkart.product.model.wishlist.Wishlist;

import java.util.Optional;

/**
 * <p>
 * Provides service for the WishlistDAO.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public interface WishlistDAO {

    /**
     * <p>
     * Adds the specific product to the wishlist.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be added to the wishlist.
     * @return true if the product is added.
     */
    boolean addProduct(final int productId, final int userId);

    /**
     * <p>
     * Removes the specific product from the wishlist.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be removed from the wishlist.
     * @return true if item removed
     */
    boolean removeProduct(final int productId, final int userId);

    /**
     * <p>
     * Gets the wishlist of the current user and returns it.
     * </p>
     *
     * @param page Refers the page number.
     * @param userId Refers the id of the user.
     * @return Optional of {@link Wishlist} of the user.
     */
    Optional<Wishlist> getWishlist(final int userId, final int page);

    /**
     * <p>
     * Checks whether the product exists in wishlist or not.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @param productId Refers the product id.
     * @return true if the product already exists or false otherwise.
     */
    boolean isProductExist(final int userId, final int productId);
}