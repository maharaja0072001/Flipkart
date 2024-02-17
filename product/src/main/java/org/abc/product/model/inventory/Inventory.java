package org.abc.product.model.inventory;

import org.abc.product.model.product.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a generic inventory to store products of a specific type.
 *
 * @param <T> The type of product stored in the inventory that extend the {@link Product} class.
 */
public class Inventory<T extends Product> {

    private final List<T> inventory;

    /**
     * Default constructor of Inventory class.
     */
    public Inventory() {
        this.inventory = new ArrayList<>();
    }

    /**
     * Adds a product to the inventory.
     *
     * @param product The product to be added to the inventory.
     */
    public void add(final T product) {
        inventory.add(product);
    }

    /**
     * Removes a product from the inventory based on its id.
     *
     * @param productId The id of the product to be removed.
     */
    public void remove(final int productId) {
        inventory.removeIf(product -> product.getId() == productId);
    }

    /**
     * Gets the list of products in the inventory.
     *
     * @return The list of products in the inventory.
     */
    public List<T> get() {
        return inventory;
    }
}
