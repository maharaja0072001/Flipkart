package org.abc.product.service.inventory;

import org.abc.product.ProductCategory;
import org.abc.product.model.product.Product;

import java.util.List;

public interface InventoryServiceREST {

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
     * @param productCategory Refers the product category.
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @return all the {@link Product} from the inventory.
     */
    List<? extends Product> getItemsByCategory(final ProductCategory productCategory, final int page, final int limit);
}
