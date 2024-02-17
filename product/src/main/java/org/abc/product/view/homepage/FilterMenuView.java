package org.abc.product.view.homepage;

import org.abc.authentication.model.User;
import org.abc.authentication.view.AuthenticationView;
import org.abc.singleton_scanner.SingletonScanner;
import org.abc.product.model.product.Product;
import org.abc.validation.Validator;
import org.abc.product.view.homepage.filter.Filter;
import org.abc.product.view.homepage.filter.impl.RateFilter;
import org.abc.product.view.wishlist.WishlistView;
import org.abc.product.view.cart.CartView;
import org.abc.product.view.common_view.View;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * <p>
 * Shows the filter menu to get filtered items.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class FilterMenuView extends View {

    private static FilterMenuView filterMenuView;
    private final Validator validator;
    private final Filter rateFilter;
    private final Scanner scanner;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of FilterMenuView class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private FilterMenuView() {
        logger = LogManager.getLogger(AuthenticationView.class);
        scanner = SingletonScanner.getScanner();
        rateFilter = RateFilter.getInstance();
        validator = Validator.getInstance();
    }

    /**
     * <p>
     * Creates a single object of FilterMenuView class and returns it.
     * </p>
     *
     * @return the single instance of FilterMenuView class.
     */
    public static FilterMenuView getInstance() {
        return Objects.isNull(filterMenuView) ? filterMenuView = new FilterMenuView() : filterMenuView;
    }

    /**
     * <p>
     * Shows the filter menu to the user to filter the items presented in the inventory.
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @param products Refers all the {@link Product} in the inventory.
     */
    void showFilterMenu(final User user, final List<Product> products) {
        logger.info("Filter By:[Press '$' to go back]\n1.Rate Low to High\n2.Rate High to Low\n3.Price");
        final int choice = getChoice();

        if (-1 == choice)  {        //-1 is returned if back key is pressed
            HomepageView.getInstance().showHomePage(user);
        }

        switch (choice) {
            case 1 -> filterByLowToHigh(user, products);
            case 2 -> filterByHighToLow(user, products);
            case 3 -> filterByRange(user, products);
            default -> {
                logger.warn("Enter a valid choice ");
                showFilterMenu(user, products);
            }
        }
    }

    /**
     * <p>
     * Filters the products by price high to low
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @param products Refers the collection of products.
     */
    private void filterByLowToHigh(final User user, final List<Product> products) {
        final List<Product> itemsFilteredByLowToHigh = rateFilter.sortLowToHigh(products);

        showItems(itemsFilteredByLowToHigh);
        getItem(user, products, itemsFilteredByLowToHigh);
    }

    /**
     * <p>
     * Filters the products by price low to high.
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @param products Refers the collection of products.
     */
    private void filterByHighToLow(final User user, final List<Product> products) {
        final List<Product> itemsFilteredByHighToLow = rateFilter.sortHighToLow(products);

        showItems(itemsFilteredByHighToLow);
        getItem(user, products, itemsFilteredByHighToLow);
    }

    /**
     * <p>
     * Filters the products by price range.
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @param products Refers the collection of products.
     */
    private void filterByRange(final User user, final List<Product> products) {
        logger.info("Enter minimum amount:");
        final String minimumAmount = scanner.nextLine().trim();

        validateAmount(minimumAmount, user, products);
        logger.info("Enter maximum amount:");
        final String maximumAmount = scanner.nextLine().trim();

        validateAmount(maximumAmount, user, products);

        if (Integer.parseInt(minimumAmount) > Integer.parseInt(maximumAmount)) {
            logger.warn("Entered amount is invalid");
            filterByRange(user, products);
        } else {
            final List<Product> itemsFilteredByPrice = rateFilter.sortByRange(products, Integer.parseInt(minimumAmount), Integer.parseInt(maximumAmount));

            if (Objects.isNull(itemsFilteredByPrice)) {
                logger.warn("No products found");
                showFilterMenu(user, products);
            } else {
                showItems(itemsFilteredByPrice);
                getItem(user, products, itemsFilteredByPrice);
            }
        }
    }

    /**
     * <p>
     * Gets the item and add it to cart or wishlist.
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @param products Refers the collection of products.
     * @param filteredItems Refers the filtered items.
     */
    private void getItem(final User user, final List<Product> products, final List<Product> filteredItems){
        logger.info("Enter the product id : [Press '$' to go back]");
        final int index = getChoice();

        if (-1 == index) {
            showFilterMenu(user, products);
        }

        if (index > filteredItems.size()) {
            logger.warn("Enter a valid product id");
            getItem(user, products, filteredItems);
        } else {
            addItemToCartOrWishlist(products.get(index - 1), user, products);
        }
    }

    /**
     * <p>
     * Gets the choice from the user to add the product to the cart or wishlist.
     * </p>
     *
     * @param product Refers the {@link Product} to be added to cart or wishlist
     * @param products Refers the collection of {@link Product}
     * @param user Refers the current {@link User}.
     */
    private void addItemToCartOrWishlist(final Product product, final User user, final List<Product> products) {
        logger.info("Enter '1' to add to cart or '2' to add to wishlist. Press '$' to go back");
        final int choice = getChoice();

        if (-1 == choice) {    // -1 is returned if back key is pressed.
            showFilterMenu(user, products);
        }

        switch (choice) {
            case 1 -> CartView.getInstance().addItem(product, user);
            case 2 -> WishlistView.getInstance().addItem(product, user);
            default -> {
                logger.warn("Invalid choice");
                addItemToCartOrWishlist(product, user, products);
            }
        }
    }

    /**
     * <p>
     * Validates the amount entered by the user.
     * </p>
     *
     * @param amount Refers the amount.
     * @param products Refers the collection of {@link Product}
     * @param user Refers the current {@link User}.
     */
    private void validateAmount(final String amount, final User user, final List<Product> products) {
        if (validator.checkToGoBack(amount)) {
            showFilterMenu(user,products);
        }

        if (! validator.isPositiveNumber(amount)) {
            logger.warn("Entered amount is invalid");
            filterByRange(user, products);
        }
    }
}