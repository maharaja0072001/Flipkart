package org.abc.product.view.cart;

import org.abc.product.model.cart.Cart;
import org.abc.product.controller.cart.CartController;
import org.abc.authentication.model.User;
import org.abc.product.model.product.Product;
import org.abc.product.view.common_view.View;
import org.abc.product.view.homepage.HomepageView;
import org.abc.product.view.order.OrderView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Shows the user cart for placing order or remove the product from the cart.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class CartView extends View {

    private static CartView cartView;
    private final CartController cartController;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of CartView class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private CartView() {
        cartController = CartController.getInstance();
        logger = LogManager.getLogger(CartView.class);
    }

    /**
     * <p>
     * Creates a single object of CartView class and returns it.
     * </p>
     *
     * @return the single instance of CartView class.
     */
    public static CartView getInstance() {
        return Objects.isNull(cartView) ? cartView = new CartView() : cartView;
    }

    /**
     * <p>
     * Adds the specific product to the cart.
     * </p>
     *
     * @param user Refers the current {@link User}
     * @param product Refers the {@link Product} to be added to the cart
     */
    public boolean addItem(final Product product, final User user) {
        if (0 == product.getQuantity()) {
            logger.warn(String.format("User id :%d Product Id :%d -The item is out of stock", user.getId(), product.getId()));

            return false;
        }

        if (cartController.addItem(product.getId(), user.getId(), product.getProductCategory())) {
            logger.info(String.format("User id :%d Product Id :%d - Item added to the cart", user.getId(), product.getId()));

            return true;
        }
        logger.info(String.format("User id :%d Product Id :%d - Item is already in the cart", user.getId(), product.getId()));

        return false;
    }

    /**
     * <p>
     * Shows the items presented in the cart and user can place the order of the items present in the cart.
     * </p>
     *
     * @param user Refers the current {@link User}
     */
    public void viewCart(final User user) {
        final Cart cart = cartController.getCart(user.getId());

        if (Objects.isNull(cart) || Objects.isNull(cart.getItems())) {
            logger.info(String.format("User id :%d-Cart is empty", user.getId()));
            HomepageView.getInstance().showHomePage(user);
        } else {
            final List<Product> cartItems = cart.getItems();

            logger.info("Cart :");
            showItems(cartItems);
            logger.info(String.format("Total amount : Rs-%.2f", cart.getTotalAmount()));
            placeOrderOrRemoveItem(cartItems, user);
        }
    }

    /**
     * <p>
     * Paces the order of the item or remove it from cart.
     * </p>
     *
     * @param user Refers the current {@link User}
     * @param items Refers the items in the cart.
     */
    private void placeOrderOrRemoveItem(final List<Product> items, final User user) {
        if (items.isEmpty()) {
            viewCart(user);
        }
        logger.info("Enter the product id to order the item or to remove from cart: [Press '$' to go back] ");
        final int productId = getChoice();

        if (-1 == productId) {
            HomepageView.getInstance().viewPage(user);
        }

        if ((items.size() >= productId)) {
            final Product product = items.get(productId - 1);

            logger.info("Enter '1' to place order or '2' to remove from cart.\nEnter a choice");
            final int choice = getChoice();

            if (-1 == choice) {
                placeOrderOrRemoveItem(items, user);
            }

            switch (choice) {
                case 1 -> {
                    if ((0 < product.getQuantity())) {
                        OrderView.getInstance().placeOrder(product, user);
                    } else {
                        logger.warn(String.format("User id :%d Product Id :%d -The product is out of stock", user.getId(), product.getId()));
                        placeOrderOrRemoveItem(items, user);
                    }
                }
                case 2 -> {
                    cartController.removeItem(product.getId(), user.getId());
                    logger.info(String.format("User id :%d Product Id :%d -Item removed from the cart", user.getId(), product.getId()));
                    placeOrderOrRemoveItem(items, user);
                }
                default -> {
                    logger.warn("Invalid choice");
                    placeOrderOrRemoveItem(items, user);
                }
            }
        } else {
            logger.warn("Invalid product id");
            placeOrderOrRemoveItem(items, user);
        }
    }
}

