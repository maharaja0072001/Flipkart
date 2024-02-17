package org.abc.product.service.wishlist;

import org.abc.product.ProductCategory;
import org.abc.product.model.wishlist.Wishlist;

public interface WishlistServiceREST {

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
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @param userId Refers the id of the user.
     * @return the {@link Wishlist} of the user.
     */
    Wishlist getWishlist(final int userId, final int page, final int limit);
}
