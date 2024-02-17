package org.abc.product.view.homepage.filter.impl;

import org.abc.product.model.product.Product;
import org.abc.product.view.homepage.filter.Filter;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * Filters the products based on price high to low or low to high or between a price range.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class RateFilter implements Filter {

    private static RateFilter rateFilter;

    /**
     * <p>
     * Default constructor of RateFilter class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private RateFilter() {}

    /**
     * <p>
     * Creates a single object of RateFilter class and returns it.
     * </p>
     *
     * @return the single instance of RateFilter class.
     */
    public static RateFilter getInstance() {
        return Objects.isNull(rateFilter) ? rateFilter = new RateFilter() : rateFilter;
    }

    /**
     * <p>
     * Filters the items present in the inventory based on price high to low and returns it.
     * </p>
     *
     * @param products Refer the items to be sorted.
     * @return the items filtered by price high to low.
     */
    public List<Product> sortHighToLow(final List<Product> products) {
        return products.stream().sorted(Comparator.comparing(Product::getPrice, Comparator.reverseOrder())).collect(Collectors.toList());
    }

    /**
     * <p>
     * Filters the items present in the inventory based on price low to high and returns it.
     * </p>
     *
     * @param products Refer the items to be sorted.
     * @return all the products filtered by price from low to high.
     */
    public List<Product> sortLowToHigh(final List<Product> products) {
        return products.stream().sorted(Comparator.comparing(Product::getPrice)).collect(Collectors.toList());
    }

    /**
     * <p>
     * Filters the inventory based on the price range given by the user and returns the filtered items.
     * </p>
     *
     * @return the filtered items of given price range.
     */
    public List<Product> sortByRange(final List<Product> products, final int minimumAmount, final int maximumAmount) {
        return products.stream().filter(product -> product.getPrice() >= minimumAmount && product.getPrice() <= maximumAmount).collect(Collectors.toList());
    }
}