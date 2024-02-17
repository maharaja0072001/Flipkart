package org.abc.product.view.homepage.filter;

import org.abc.product.model.product.Product;

import java.util.List;

/**
 * <p>
 * Filters the items in the inventory and returns it.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public interface Filter {

    /**
     * <p>
     * Filters the items present in the inventory based on price low to high and returns it.
     * </p>
     *
     * @param products Refer the items to be sorted.
     * @return all the products filtered by price from low to high.
     */
    List<Product> sortLowToHigh(final List<Product> products);

    /**
     * <p>
     * Filters the items present in the inventory based on price high to low and returns it.
     * </p>
     *
     * @param products Refer the items to be sorted.
     * @return the items filtered by price high to low.
     */
    List<Product> sortHighToLow(final List<Product> products);

    /**
     * <p>
     * Filters the inventory based on the price range given by the user and returns the filtered items.
     * </p>
     *
     * @return the filtered items of given price range.
     */
    List<Product> sortByRange(final List<Product> inventoryItems, final int minimumAmount, final int maximumAmount);
}
