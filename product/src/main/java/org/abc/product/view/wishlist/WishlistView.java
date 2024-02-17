package org.abc.product.view.wishlist;

import org.abc.product.controller.wishlist.WishlistController;
import org.abc.authentication.model.User;
import org.abc.product.model.wishlist.Wishlist;
import org.abc.product.model.product.Product;
import org.abc.product.view.cart.CartView;
import org.abc.product.view.common_view.View;
import org.abc.product.view.homepage.HomepageView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Shows the wishlist of the user for adding the item to the cart or removing it from the wishlist.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class WishlistView extends View {

    private static WishlistView wishlistView;
    private final WishlistController wishlistController;
    private final HomepageView homepageView;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of WishlistView class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private WishlistView() {
        wishlistController = WishlistController.getInstance();
        homepageView = HomepageView.getInstance();
        logger = LogManager.getLogger(WishlistView.class);
    }

    /**
     * <p>
     * Creates a single object of WishlistView class and returns it.
     * </p>
     *
     * @return the single instance of WishlistView class.
     */
    public static WishlistView getInstance() {
        return Objects.isNull(wishlistView) ? wishlistView = new WishlistView() : wishlistView;
    }

    /**
     * <p>
     * Adds the specific product to the wishlist
     * </p>
     *
     * @param product Refers the product to be added
     * @param user Refers {@link User} who owns the wishlist.
     */
    public boolean addItem(final Product product, final User user) {
        if (wishlistController.addItem(product.getId(), user.getId(), product.getProductCategory())) {
            logger.info(String.format("User Id : %d Product Id : %d - Item added to the wishlist",
                    user.getId(), product.getId()));

            return true;
        } else {
            logger.warn(String.format("User Id : %d Product Id : %d - Item is already in the wishlist",
                    user.getId(), product.getId()));
        }

        return false;
    }

    /**
     * <p>
     * Shows the wishlist items to the user and the user can add the items to the cart
     * </p>
     *
     * @param user Refers {@link User} to show the wishlist of that user.
     */
    public void viewWishlist(final User user) {
        final Wishlist wishlist = wishlistController.getWishlist(user.getId());

        if (Objects.isNull(wishlist) || Objects.isNull(wishlist.getItems())) {
            logger.info(String.format("User Id : %d - Wishlist is empty", user.getId()));
            homepageView.showHomePage(user);
        } else {
            final List<Product> items = wishlist.getItems();

            logger.info("Wishlist :");
            showItems(items);
            addToCartOrRemoveItem(items, user);
        }
    }

    /**
     * <p>
     * Adds the item to the cart or remove it from the wishlist.
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @param items Refers the items in the wishlist.
     */
    private void addToCartOrRemoveItem(final List<Product> items, final User user) {
        if (items.isEmpty()) {
            viewWishlist(user);
        }
        logger.info("Enter the product id to add to cart or to remove from wishlist: [Press '$' to go back] ");
        final int productId = getChoice();

        if (-1 == productId) {
            homepageView.showHomePage(user);
        }

        if ((items.size() >= productId)) {
            final Product item = items.get(productId - 1);

            logger.info("Enter '1' to add to cart or '2' to remove from wishlist.\nEnter a choice :");
            final int choice = getChoice();

            if (-1 == choice) {
                addToCartOrRemoveItem(items, user);
            }

            switch (choice) {
                case 1 -> {
                    if (!CartView.getInstance().addItem(item, user)) {
                        addToCartOrRemoveItem(items, user);
                    }
                }
                case 2 -> {
                    wishlistController.removeItem(item.getId(), user.getId());
                    logger.info(String.format("User Id : %d Product Id : %d - Item removed from wishlist",
                            user.getId(), item.getId()));
                    addToCartOrRemoveItem(items, user);
                }
                default -> {
                    logger.warn("Invalid choice");
                    addToCartOrRemoveItem(items, user);
                }
            }
        } else {
            logger.warn("Invalid product id");
            viewWishlist(user);
        }
    }
}


