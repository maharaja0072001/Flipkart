package org.abc.product.controller.wishlist;

import org.abc.product.ProductCategory;
import org.abc.product.model.wishlist.Wishlist;
import org.abc.product.service.wishlist.impl2.WishlistServiceImpl;
import org.abc.product.service.wishlist.WishlistService;

import java.util.Objects;

/**
 * <p>
 * Handles requests and responses from WishlistView clas and WishlistService class.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class WishlistController {

    private static WishlistController wishlistController;
    private final WishlistService wishlistService;

    /**
     * <p>
     * Default constructor of the WishlistController class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private WishlistController() {
        wishlistService = (WishlistService) WishlistServiceImpl.getInstance();
    }

    /**
     * <p>
     * Creates a single object of WishlistController Class and returns it.
     * </p>
     *
     * @return returns the single instance of WishlistController Class.
     */
    public static WishlistController getInstance() {
        return Objects.isNull(wishlistController) ? wishlistController = new WishlistController() : wishlistController;
    }

    /**
     * <p>
     * Adds the product to the wishlist of the specified user.
     * </p>
     * @param productId Refers the id of the product to be added
     * @param userId Refers the user id.
     * @param productCategory Refers the product category.
     * @return the if product added to the wishlist.
     */
    public boolean addItem(final int productId, final int userId, final ProductCategory productCategory) {
        return wishlistService.addItem(productId, userId, productCategory);
    }

    /**
     * <p>
     * Removes the product from the wishlist of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId Refers the user id.
     */
    public void removeItem(final int productId, final int userId) {
        wishlistService.removeItem(productId, userId);
    }

    /**
     * <p>
     * Gets the wishlist of the specified user id and returns it.
     * </p>
     *
     * @param userId Refers the user id who owns the cart.
     * @return the {@link Wishlist} of the user.
     */
    public Wishlist getWishlist(final int userId) {
        return wishlistService.getWishlist(userId);
    }
}
