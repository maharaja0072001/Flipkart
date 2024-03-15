package com.flipkart.product.service.inventory;

import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.model.product.Product;

import java.util.List;

public interface InventoryService {

    /**
     * <p>
     * Adds the products to the inventory.
     * </p>
     *
     * @param products Refers the {@link Product} to be added.
     */
    void addProduct(final List<Product> products);

    /**
     * <p>
     * Removes the product from the inventory.
     * </p>
     *
     * @param productId Refers the id of the {@link Product} to be removed.
     * @param productCategory Refers the {@link ProductCategory}
     * @return true if item  removed
     */
    boolean removeProduct(final int productId, final ProductCategory productCategory);

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @param productCategory Refers the product category.
     * @param page Refers the page number.
     * @return all the {@link Product} from the inventory.
     */
    List<? extends Product> getItemsByCategory(final ProductCategory productCategory, final int page);

    /**
     * <p>
     * Checks whether the product exists or not.
     * </p>
     *
     * @param productId Refers the id of the product.
     * @return true if the user already exists or false otherwise.
     */
    boolean isProductExist(final int productId);
}
