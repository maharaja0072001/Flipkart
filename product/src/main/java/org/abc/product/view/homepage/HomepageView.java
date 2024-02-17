package org.abc.product.view.homepage;

import org.abc.product.ProductCategory;
import org.abc.authentication.view.AuthenticationView;
import org.abc.singleton_scanner.SingletonScanner;
import org.abc.product.view.common_view.View;
import org.abc.product.controller.inventory.InventoryController;
import org.abc.authentication.model.User;
import org.abc.product.view.order.OrderView;
import org.abc.product.model.product.Product;
import org.abc.product.InventoryManager;
import org.abc.validation.Validator;
import org.abc.pageview.PageViewer;
import org.abc.product.view.wishlist.WishlistView;
import org.abc.product.view.cart.CartView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Shows the homepage of the flipkart application to the user to shop products.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class HomepageView extends View implements PageViewer {

    private static HomepageView homePageView;
    private final AuthenticationView authenticationView;
    private final InventoryController inventoryController;
    private final Validator validator;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of HomepageView class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private HomepageView() {
        logger = LogManager.getLogger(CartView.class);
        validator = Validator.getInstance();
        authenticationView = AuthenticationView.getInstance();
        inventoryController = InventoryController.getInstance();
    }

    /**
     * <p>
     * Creates a single object of HomepageView class and returns it.
     * </p>
     *
     * @return the single instance of HomepageView class.
     */
    public static HomepageView getInstance() {
        return Objects.isNull(homePageView) ? homePageView = new HomepageView() : homePageView;
    }

    /**
     * <p>
     * Shows the home page of the flipkart application to the user for shopping the products.
     * </p>
     *
     * @param user Refers the current {@link User}.
     */
    public void showHomePage(final User user) {
        InventoryManager.getInstance().addAllItems();
        final List<Product> MOBILES = inventoryController.getItemsByCategory(ProductCategory.MOBILE);
        final List<Product> LAPTOPS = inventoryController.getItemsByCategory(ProductCategory.LAPTOP);
        final List<Product> CLOTHES = inventoryController.getItemsByCategory(ProductCategory.CLOTHES);
        logger.info("1.Mobiles\n2.Laptops\n3.Clothes\n4.Cart\n5.Wishlist\n6.My Orders\n7.Profile\n8.Logout");

        switch (getChoice()) {
            case 1 -> {
                showItems(MOBILES);
                toAddItemOrShowFilterMenu(user, MOBILES);
            }
            case 2 -> {
                showItems(LAPTOPS);
                toAddItemOrShowFilterMenu(user, LAPTOPS);
            }
            case 3 -> {
                showItems(CLOTHES);
                toAddItemOrShowFilterMenu(user, CLOTHES);
            }
            case 4 -> CartView.getInstance().viewCart(user);
            case 5 -> WishlistView.getInstance().viewWishlist(user);
            case 6 -> OrderView.getInstance().viewAndCancelOrder(user);
            case 7 -> authenticationView.viewAndEditProfile(user);
            case 8 -> {
                logger.info("Logged out successfully");
                authenticationView.showAuthenticationPage();
            }
            default -> logger.warn("Enter a valid choice");
        }
        showHomePage(user);
    }

    /**
     * <p>
     * Gets choice from the user to add to cart or wishlist or show filtered products to the user
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @param products Refers the products in the inventory.
     */
    private void toAddItemOrShowFilterMenu(final User user, final List<Product> products) {
        logger.info("Enter the product id to add to cart or wishlist or '#' to show filter menu or press '$' to go back");
        final String choice = SingletonScanner.getScanner().nextLine().trim();

        if (validator.checkToGoBack(choice)) {
            showHomePage(user);
        } else {
            if (validator.toShowFilterMenu(choice)) {
                FilterMenuView.getInstance().showFilterMenu(user, products);
            } else if (validator.isPositiveNumber(choice) && (Integer.parseInt(choice) <= products.size())) {
                final Product product = products.get(Integer.parseInt(choice) - 1);

                addItemToCartOrWishlist(product, user, products);
            } else {
                    logger.warn("Enter a valid product id");
                    toAddItemOrShowFilterMenu(user, products);
            }
        }
    }

    /**
     * <p>
     * Shows the homepage of the user.
     * </p>
     *
     * @param user Refers the current {@link User}.
     */
    @Override
    public void viewPage(Object user) {
        showHomePage((User) user);
    }

    /**
     * <p>
     * Gets the choice from the user to add the product to the cart or wishlist.
     * </p>
     *
     * @param product Refers the {@link Product} to be added to cart or wishlist
     * @param user Refers the current {@link User}.
     */
    private void addItemToCartOrWishlist(final Product product, final User user, final List<Product> products) {
        logger.info("Enter '1' to add to cart or '2' to add to wishlist. Press '$' to go back");
        final int choice = getChoice();

        if (-1 == choice) {    // -1 is returned if back key is pressed.
            showHomePage(user);
        } else {
            switch (choice) {
                case 1 -> {
                    if (!CartView.getInstance().addItem(product, user)) {
                        toAddItemOrShowFilterMenu(user, products);
                    }
                }
                case 2 -> {
                    if (!WishlistView.getInstance().addItem(product, user)) {
                        toAddItemOrShowFilterMenu(user, products);
                    }
                }
                default -> {
                    logger.warn("Invalid choice");
                    addItemToCartOrWishlist(product, user, products);
                }
            }
        }
    }
}
