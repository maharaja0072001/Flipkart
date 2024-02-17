package org.abc.product.service.wishlist;

import org.abc.product.ProductCategory;
import org.abc.product.model.wishlist.Wishlist;

/**
 * <p>
 * Provides the service for the Wishlist.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public interface WishlistService {

    /**
     * <p>
     * Adds the specific product to the wishlist.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be added to the wishlist.
     * @param productCategory Refers the product category.
     * @return true if the product is added.
     */
    boolean addItem(final int productId, final int userId, final ProductCategory productCategory);

    /**
     * <p>
     * Removes the specific product from the wishlist.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be removed from the wishlist.
     */
    void removeItem(final int productId, final int userId);

    /**
     * <p>
     * Gets the wishlist of the current user and returns it.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return the {@link Wishlist} of the user.
     */
    Wishlist getWishlist(final int userId);
}
