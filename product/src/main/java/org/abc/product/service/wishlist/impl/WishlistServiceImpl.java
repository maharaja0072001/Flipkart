package org.abc.product.service.wishlist.impl;

import org.abc.product.ProductCategory;
import org.abc.product.controller.inventory.InventoryController;
import org.abc.product.model.wishlist.Wishlist;
import org.abc.product.service.wishlist.WishlistService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * Provides the service for the Wishlist.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class WishlistServiceImpl implements WishlistService {

    private static WishlistServiceImpl wishlistService;
    private final Map<Integer, Wishlist> wishlists;

    /**
     * <p>
     * Default constructor of the WishlistServiceImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private WishlistServiceImpl() {
        wishlists = new HashMap<>();
    }

    /**
     * <p>
     * Creates a single object of WishlistServiceImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of WishlistServiceImpl Class.
     */
    public static WishlistService getInstance() {
        return Objects.isNull(wishlistService) ? wishlistService = new WishlistServiceImpl() : wishlistService;
    }

    /**
     * <p>
     * Adds the specific product to the wishlist
     * </p>
     *
     * @param productId Refers the id of the product to be added to the wishlist.
     * @param userId Refers the user id.
     * @param productCategory Refers the category of product.
     * @return true if the product is added or false otherwise.
     */
    @Override
    public boolean addItem(final int productId, final int userId, final ProductCategory productCategory) {
       wishlists.computeIfAbsent(userId, key -> new Wishlist());
        final Wishlist wishlist = wishlists.get(userId);

        return InventoryController.getInstance().getItemsByCategory(productCategory).stream()
                .filter(product -> productId == product.getId()).findFirst().map(wishlist::addItem).orElse(false);
    }

    /**
     * <p>
     * Removes the specific product from the wishlist
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be removed .
     */
    @Override
    public void removeItem(final int productId, final int userId) {
        final Wishlist wishlist = wishlists.get(userId);

        wishlist.getItems().removeIf(product -> productId == product.getId());
    }

    /**
     * <p>
     * Gets the wishlist of the current user and returns it.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return the {@link Wishlist} of the user.
     */
    @Override
    public Wishlist getWishlist(final int userId) {
        return wishlists.get(userId);
    }
}
