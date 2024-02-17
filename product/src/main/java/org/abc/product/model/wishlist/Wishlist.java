package org.abc.product.model.wishlist;

import org.abc.product.model.product.Product;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Represents a wishlist for the user to add the items to the wishlist and can move them to cart for ordering.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class Wishlist {

    private List<Product> wishlistItems;

    /**
     * <p>
     * Adds the specific product to the wishlist
     * </p>
     *
     * @param product Refers {@link Product} to be added to the wishlist.
     * @return true if the product is added.
     */
    public boolean addItem(final Product product) {
        wishlistItems = Objects.isNull(wishlistItems) ? new LinkedList<>() : wishlistItems;

        return wishlistItems.add(product);
    }

    /**
     * <p>
     * Removes the specific product from the wishlist
     * </p>
     *
     * @param product Refers {@link Product} the product to be removed.
     */
    public void removeItem(final Product product) {
        wishlistItems.remove(product);
    }

    /**
     * <p>
     * Gets the products in the wishlist and returns it.
     * </p>
     *
     * @return all the {@link Product} in the wishlist.
     */
    public List<Product> getItems() {
        return wishlistItems;
    }
}
