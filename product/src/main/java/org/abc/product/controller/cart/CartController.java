package org.abc.product.controller.cart;

import org.abc.product.ProductCategory;
import org.abc.product.model.cart.Cart;
import org.abc.product.service.cart.CartService;
import org.abc.product.service.cart.impl2.CartServiceImpl;

import java.util.Objects;

/**
 * <p>
 * Interacts between CartView and CartService for adding , removing from the cart of the user.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class CartController {

    private static CartController cartController;
    private final CartService cartService;

    /**
     * <p>
     * Default constructor of the CartController class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private CartController() {
        cartService = (CartService) CartServiceImpl.getInstance();
    }

    /**
     * <p>
     * Creates a single object of CartController Class and returns it.
     * </p>
     *
     * @return returns the single instance of CartController Class.
     */
    public static CartController getInstance() {
        return Objects.isNull(cartController) ? cartController = new CartController() : cartController;
    }

    /**
     * <p>
     * Adds the product to the cart of the specified user.
     * </p>
     *
     * @param productId       Refers the id of the product to be added
     * @param userId          Refers the user id.
     * @param productCategory Refers the product category.
     * @return true if product added.
     */
    public boolean addItem(final int productId, final int userId, final ProductCategory productCategory) {
        return cartService.addItem(productId, userId, productCategory);
    }

    /**
     * <p>
     * Removes the product from the cart of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId    Refers the user id.
     */
    public void removeItem(final int productId, final int userId) {
        cartService.removeItem(productId, userId);
    }

    /**
     * <p>
     * Gets the cart of the specified user id and returns it.
     * </p>
     *
     * @param userId Refers the user id.
     * @return the {@link Cart} of the user.
     */
    public Cart getCart(final int userId) {
        return cartService.getCart(userId);
    }
}
