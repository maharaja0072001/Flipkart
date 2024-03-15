package com.flipkart.product.service.wishlist;

import com.flipkart.authentication.dao.v1.UserDAOImpl;
import com.flipkart.product.dao.wishlist.WishlistDAO;
import com.flipkart.product.dao.wishlist.v1.WishlistDAOImpl;
import com.flipkart.product.model.wishlist.Wishlist;
import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.dao.inventory.v1.InventoryDAOImpl;

import java.util.Optional;

/**
 * <p>
 * Provides the service for the Wishlist.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class WishlistServiceImpl implements WishlistService {

    private final WishlistDAO wishlistDAO;

    /**
     * <p>
     * Default constructor of the WishlistServiceImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private WishlistServiceImpl() {
        wishlistDAO = WishlistDAOImpl.getInstance();
    }

    /**
     * <p>
     * Creates a single instance of WishlistServiceImpl class.
     * </p>
     */
    private static class InstanceHolder {

        private static final WishlistServiceImpl WISHLIST_SERVICE = new WishlistServiceImpl();
    }

    /**
     * <p>
     * Gets a single object of WishlistServiceImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of WishlistServiceImpl Class.
     */
    public static WishlistService getInstance() {
        return InstanceHolder.WISHLIST_SERVICE;
    }

    /**
     * <p>
     * Adds the specific product to the wishlist
     * </p>
     *
     * @param productId Refers the id of the product to be added to the wishlist.
     * @param userId Refers the user id.
     * @param productCategory Refers the product category.
     * @return true if product added to the cart
     */
    @Override
    public boolean addProduct(final int productId, final int userId, final ProductCategory productCategory) {
        return !wishlistDAO.isProductExist(productId, userId) && wishlistDAO.addProduct(productId, userId);
    }

    /**
     * <p>
     * Removes the specific product from the wishlist
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be removed from the wishlist.
     * @return true if item removed
     */
    @Override
    public boolean removeProduct(final int productId, final int userId) {
         return wishlistDAO.removeProduct(productId, userId);
    }

    /**
     * <p>
     * Gets the wishlist of the current user and returns it.
     * </p>
     *
     * @param page Refers the page number.
     * @param userId Refers the id of the user.
     * @return Optional of {@link Wishlist} of the user.
     */
    @Override
    public Optional<Wishlist> getWishlist(final int userId, final int page) {
        return wishlistDAO.getWishlist(userId, page);
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
