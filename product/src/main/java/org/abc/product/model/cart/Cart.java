package org.abc.product.model.cart;

import org.abc.product.model.product.Product;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Represents a cart for the user to add the items to the cart and placing the orders.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class Cart {

    private List<Product> cartItems;
    private float totalAmountInCart;

    /**
     * <p>
     * Adds the specific product to the cart
     * </p>
     *
     * @param product Refers {@link Product} to be added to the cart.
     * @return true if the product is added.
     */
    public boolean addItem(final Product product) {
        cartItems = Objects.isNull(cartItems) ? new LinkedList<>() : cartItems;

        if (!cartItems.contains(product)) {
            totalAmountInCart += product.getPrice();

            return cartItems.add(product);
        }

        return false;
    }

    /**
     * <p>
     * Removes the specific product from the cart
     * </p>
     *
     * @param product Refers {@link Product} the product to be removed.
     */
    public void removeItem(final Product product) {
        cartItems.remove(product);
        totalAmountInCart -= product.getPrice();
    }

    /**
     * <p>
     * Gets the products in the cart and returns it.
     * </p>
     *
     * @return all the {@link Product} in the cart.
     */
    public List<Product> getItems() {
        return cartItems;
    }

    /**
     * <p>
     * Gets the total amount in the cart
     * </p>
     *
     * @return the total amount.
     */
    public float getTotalAmount() {
        return totalAmountInCart;
    }
}
