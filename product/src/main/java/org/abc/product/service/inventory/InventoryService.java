package org.abc.product.service.inventory;

import org.abc.product.ProductCategory;
import org.abc.product.model.product.Product;

import java.util.List;

/**
 * <p>
 * Provides service for the Inventory.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public interface InventoryService {

    /**
     * <p>
     * Adds the products to the inventory.
     * </p>
     *
     * @param products Refers the {@link Product} to be added.
     */
    void addItem(final List<Product> products);

    /**
     * <p>
     * Removes the product from the inventory.
     * </p>
     *
     * @param productId Refers the id of the {@link Product} to be removed.
     * @param productCategory Refers the {@link ProductCategory}
     */
    void removeItem(final int productId, final ProductCategory productCategory);

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @return all the {@link Product} from the inventory.
     */
    List<? extends Product> getItemsByCategory(final ProductCategory productCategory);
}
