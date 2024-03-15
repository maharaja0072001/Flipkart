package com.flipkart.product.dao.inventory;

import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.model.product.Product;

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
     * @return true if item  removed
     */
    boolean removeItem(final int productId);

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @param productCategory Refers the product Category.
     * @param page Refers the page number.
     * @return all the {@link Product} from the inventory.
     */
    List<? extends Product> getProductByCategory(final ProductCategory productCategory, final int page);

    /**
     * <p>
     * Checks whether the product exists or not.
     * </p>
     *
     * @param productId Refers the product id.
     * @return true if the product already exists or false otherwise.
     */
    boolean isProductExist(final int productId);
}
