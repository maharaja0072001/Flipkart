package com.flipkart.product.service.inventory;

import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.dao.inventory.InventoryDAO;
import com.flipkart.product.dao.inventory.v1.InventoryDAOImpl;
import com.flipkart.product.model.product.Product;

import java.util.List;

/**
 * <p>
 * Provides the service for the Inventory. Responsible for storing all the products.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class InventoryServiceImpl implements InventoryService {

    private final InventoryDAO inventoryDao;

    /**
     * <p>
     * Default constructor of InventoryServiceImpl class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private InventoryServiceImpl() {
        inventoryDao = InventoryDAOImpl.getInstance();
    }

    /**
     * <p>
     * Creates a single instance of InventoryServiceImpl class.
     * </p>
     */
    private static class InstanceHolder {

        private static final InventoryServiceImpl INVENTORY_SERVICE = new InventoryServiceImpl();
    }

    /**
     * <p>
     * Gets a single instance of InventoryServiceImpl class and returns it.
     * </p>
     *
     * @return the single instance of InventoryController class.
     */
    public static InventoryService getInstance() {
        return InstanceHolder.INVENTORY_SERVICE;
    }

    /**
     * <p>
     * Adds the given products to the inventory.
     * </p>
     *
     * @param products Refers the {@link Product} to be added.
     */
    @Override
    public void addProduct(final List<Product> products) {
        inventoryDao.addItem(products);
    }

    /**
     * <p>
     * Removes the given item from the inventory.
     * </p>
     *
     * @param productId       Refers the id of the {@link Product} to be removed.
     * @param productCategory Refers the {@link ProductCategory}
     * @return true if item  removed
     */
    @Override
    public boolean removeProduct(final int productId, final ProductCategory productCategory) {
        return inventoryDao.removeItem(productId);
    }

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @param productCategory Refers the product Category.
     * @param page            Refers the page number.
     * @return all the {@link Product} from the inventory.
     */
    @Override
    public List<? extends Product> getItemsByCategory(final ProductCategory productCategory, final int page) {
        return inventoryDao.getProductByCategory(productCategory, page);
    }

    /**
     * <p>
     * Checks whether the product exists or not.
     * </p>
     *
     * @param productId Refers the id of the product.
     * @return true if the user already exists or false otherwise.
     */
    @Override
    public boolean isProductExist(final int productId) {
        return inventoryDao.isProductExist(productId);
    }
}