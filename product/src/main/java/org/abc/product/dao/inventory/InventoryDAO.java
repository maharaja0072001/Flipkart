package org.abc.product.dao.inventory;

import org.abc.product.ProductCategory;
import org.abc.product.model.product.Product;

import java.util.List;

/**
 * <p>
 * Provides service for the InventoryDAO.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public interface InventoryDAO {

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
     */
    void removeItem(final int productId);

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @param productCategory Refers the product category.
     * @return all the {@link Product} from the inventory.
     */
    List<? extends Product> getItemsByCategory(final ProductCategory productCategory);

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @param productCategory Refers the product Category.
     * @param limit Refers the limit of data to show.
     * @param page Refers the page number.
     * @return all the {@link Product} from the inventory.
     */
    List<? extends Product> getItemsByCategory(final ProductCategory productCategory, final int page, final int limit);
}
