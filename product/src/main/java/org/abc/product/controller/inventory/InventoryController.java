package org.abc.product.controller.inventory;

import org.abc.product.ProductCategory;
import org.abc.product.model.product.Product;
import org.abc.product.service.inventory.impl2.InventoryServiceImpl;
import org.abc.product.service.inventory.InventoryService;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Interacts between InventoryView and InventoryService for adding, removing and retrieving products from inventory.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class InventoryController {

    private static InventoryController inventoryController;
    private final InventoryService inventory;

    /**
     * <p>
     * Default constructor of InventoryController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private InventoryController() {
        inventory = (InventoryService) InventoryServiceImpl.getInstance();
    }

    /**
     * <p>
     * Creates a single object of InventoryController class and returns it.
     * </p>
     *
     * @return the single instance of InventoryController class.
     */
    public static InventoryController getInstance() {
        return Objects.isNull(inventoryController) ? inventoryController = new InventoryController() : inventoryController;
    }

    /**
     * <p>
     * Adds the given products to the inventory.
     * </p>
     *
     * @param products the products to be added.
     */
    public void addItemToInventory(final List<Product> products) {
        inventory.addItem(products);
    }

    /**
     * <p>
     * Removes the given item from the inventory.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     */
    public void removeItemFromInventory(final int productId, final ProductCategory productCategory) {
        inventory.removeItem(productId, productCategory);
    }

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @return all the {@link Product} from the inventory.
     */
    public List<Product> getItemsByCategory(final ProductCategory productCategory) {
        return (List<Product>) inventory.getItemsByCategory(productCategory);
    }
}
