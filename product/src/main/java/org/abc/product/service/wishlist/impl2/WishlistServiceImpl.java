package org.abc.product.service.wishlist.impl2;

import org.abc.product.ProductCategory;
import org.abc.product.dao.wishlist.WishlistDAO;
import org.abc.product.dao.wishlist.impl.WishlistDAOImpl;
import org.abc.product.model.wishlist.Wishlist;
import org.abc.product.service.wishlist.WishlistService;
import org.abc.product.service.wishlist.WishlistServiceREST;

import java.util.Objects;

/**
 * <p>
 * Provides the service for the Wishlist.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class WishlistServiceImpl implements WishlistServiceREST, WishlistService {

    private static WishlistServiceREST wishlistService;
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
     * Creates a single object of WishlistServiceImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of WishlistServiceImpl Class.
     */
    public static WishlistServiceREST getInstance() {
        return Objects.isNull(wishlistService) ? wishlistService = new WishlistServiceImpl() : wishlistService;
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
    public boolean addItem(final int productId, final int userId, final ProductCategory productCategory) {
        return wishlistDAO.addItem(productId, userId);
    }

    /**
     * <p>
     * Removes the specific product from the wishlist
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be removed from the wishlist.
     */
    @Override
    public void removeItem(final int productId, final int userId) {
         wishlistDAO.removeItem(productId, userId);
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
    public Wishlist getWishlist(int userId) {
        return wishlistDAO.getWishlist(userId);
    }

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
    @Override
    public Wishlist getWishlist(final int userId, final int page, final int limit) {
        return wishlistDAO.getWishlist(userId, page, limit);
    }
}
