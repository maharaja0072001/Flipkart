package org.abc.product.service.inventory.impl2;

import org.abc.product.ProductCategory;
import org.abc.product.dao.inventory.impl.InventoryDAOImpl;
import org.abc.product.dao.inventory.InventoryDAO;
import org.abc.product.model.product.Product;
import org.abc.product.service.inventory.InventoryService;
import org.abc.product.service.inventory.InventoryServiceREST;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Provides the service for the Inventory. Responsible for storing all the products.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class InventoryServiceImpl implements InventoryServiceREST, InventoryService {

    private static InventoryServiceREST inventoryService;
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
     * Creates a single object of InventoryServiceImpl class and returns it.
     * </p>
     *
     * @return the single instance of InventoryController class.
      */
    public static InventoryServiceREST getInstance() {
        return Objects.isNull(inventoryService) ? inventoryService = new InventoryServiceImpl() : inventoryService;
    }

    /**
     * <p>
     * Adds the given products to the inventory.
     * </p>
     *
     * @param products Refers the {@link Product} to be added.
     */
    @Override
    public void addItem(final List<Product> products) {
        inventoryDao.addItem(products);
    }

    /**
     * <p>
     * Removes the given item from the inventory.
     * </p>
     *
     * @param productId Refers the id of the {@link Product} to be removed.
     * @param productCategory Refers the {@link ProductCategory}
     */
    @Override
    public void removeItem(final int productId, final ProductCategory productCategory) {
        inventoryDao.removeItem(productId);
    }

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @param productCategory Refers the product Category.
     * @return all the {@link Product} from the inventory.
     */
    @Override
    public List<? extends Product> getItemsByCategory(ProductCategory productCategory) {
        return inventoryDao.getItemsByCategory(productCategory);
    }

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @param productCategory Refers the product Category.
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @return all the {@link Product} from the inventory.
     */
    @Override
    public List<? extends Product> getItemsByCategory(final ProductCategory productCategory, final int page, final int limit) {
        return inventoryDao.getItemsByCategory(productCategory, page, limit);
    }
}