package org.abc.product.service.cart.impl;

import org.abc.product.ProductCategory;
import org.abc.product.controller.inventory.InventoryController;
import org.abc.product.model.cart.Cart;
import org.abc.product.service.cart.CartService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * Provides the service for the Cart of the user.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class CartServiceImpl implements CartService {

    private static CartService cartService;
    private final Map<Integer, Cart> carts;

    /**
     * <p>
     * Default constructor of the CartServiceImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private CartServiceImpl() {
        carts = new HashMap<>();
    }

    /**
     * <p>
     * Creates a single object of CartServiceImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of CartServiceImpl Class.
     */
    public static CartService getInstance() {
        return Objects.isNull(cartService) ? cartService = new CartServiceImpl() : cartService;
    }

    /**
     * <p>
     * Adds the product to the cart of the user.
     * </p>
     *
     * @param productId Refers the id of the product to be added to the cart.
     * @param userId Refers the user id.
     * @param productCategory Refers the category of product.
     * @return true if the product is added or false otherwise.
     */
    @Override
    public boolean addItem(final int productId, final int userId, final ProductCategory productCategory) {
        carts.computeIfAbsent(userId, key -> new Cart());
        final Cart cart = carts.get(userId);

        return InventoryController.getInstance().getItemsByCategory(productCategory).stream()
                .filter(product -> productId == product.getId())
                .findFirst().map(cart::addItem).orElse(false);
    }

    /**
     * <p>
     * Removes the specific product from the cart.
     * </p>
     *
     * @param userId Refers the user id.
     * @param productId Refers the id of the product to be removed from the cart.
     */
    @Override
    public void removeItem(final int productId, final int userId) {
        final Cart cart = carts.get(userId);

        cart.getItems().removeIf(product -> productId == product.getId());
    }

    /**
     * <p>
     * Gets the cart of the current user and returns it.
     * </p>
     *
     * @param userId Refers the user id.
     * @return the {@link Cart} of the user.
     */
    @Override
    public Cart getCart(final int userId) {
        return carts.get(userId);
    }
}
