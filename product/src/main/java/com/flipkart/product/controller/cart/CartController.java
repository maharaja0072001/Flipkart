package com.flipkart.product.controller.cart;

import com.flipkart.authentication.json.JsonFactory;
import com.flipkart.authentication.json.JsonObject;
import com.flipkart.product.service.cart.CartServiceImpl;
import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.model.cart.Cart;
import com.flipkart.product.service.cart.CartService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Optional;

/**
 * <p>
 * Interacts between CartView and CartService for adding, removing from the cart of the user.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/cart")
public class CartController {

    private final CartService cartService;
    private final JsonFactory jsonFactory;
    private static final Logger LOGGER = LogManager.getLogger(CartController.class);

    /**
     * <p>
     * Default constructor of the CartController class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private CartController() {
        cartService = CartServiceImpl.getInstance();
        jsonFactory = new JsonFactory();
    }

    /**
     * <p>
     * Creates a single instance of RestCartController class.
     * </p>
     */
    private static class InstanceHolder {

        private static final CartController CART_CONTROLLER = new CartController();
    }

    /**
     * <p>
     * Gets a single instance of CartController Class and returns it.
     * </p>
     *
     * @return returns the single instance of CartController Class.
     */
    public static CartController getInstance() {
        return InstanceHolder.CART_CONTROLLER;
    }

    /**
     * <p>
     * Adds the product to the cart of the specified user.
     * </p>
     *
     * @param productId       Refers the id of the product to be added
     * @param userId          Refers the user id.
     * @param productCategory Refers the product category.
     * @return JsonNode if product added.
     */
    @Path("/{userId}/{category}/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public byte[] addProduct(@PathParam("productId") final int productId,
                             @PathParam("userId") final int userId,
                             @PathParam("category") final ProductCategory productCategory) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!cartService.isUserExist(userId)) {
            return jsonObject.put("status", "User not found").asBytes();
        } else if (!cartService.isProductExist(productId)) {
            return jsonObject.put("status", "product not found").asBytes();
        }

        if (cartService.addProduct(productId, userId, productCategory)) {
            LOGGER.info(String.format("User id :%d Product Id :%d - Item added to the cart", userId, productId));

            return jsonObject.put("status", "Item added to the cart").asBytes();
        } else {
            LOGGER.info(String.format("User id :%d Product Id :%d - Item is already in the cart", userId, productId));

            return jsonObject.put("status", "Item is already in the cart").asBytes();
        }
    }

    /**
     * <p>
     * Removes the product from the cart of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId    Refers the user id.
     */
    @Path("/{userId}/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    public byte[] removeProduct(@PathParam("productId") final int productId,
                                @PathParam("userId") final int userId) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!cartService.isProductExist(productId)) {
            return jsonObject.put("status", "product not found").asBytes();
        } else if (!cartService.isUserExist(userId)) {
            return jsonObject.put("status", "User not found").asBytes();
        }
        final boolean status = cartService.removeProduct(productId, userId);

        if (status) {
            LOGGER.info(String.format("User id :%d Product Id :%d - Item removed from the cart", userId, productId));

            return jsonObject.put("status", "Item removed from the cart").asBytes();
        } else {
            LOGGER.info(String.format("User id :%d Product Id :%d - Item not found", userId, productId));

            return jsonObject.put("status", "Item not found").asBytes();
        }
    }

    /**
     * <p>
     * Gets the cart of the specified user id and returns it.
     * </p>
     *
     * @param userId Refers the user id.
     * @param page   Refers the page number.
     * @return the {@link Cart} of the user.
     */
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public byte[] getCart(@PathParam("userId") final int userId,
                          @QueryParam("page") final int page) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!cartService.isUserExist(userId)) {
            return jsonObject.put("status", "User not found").asBytes();
        }

        final Optional<Cart> optionalCart = cartService.getCart(userId, page);

        return optionalCart.isPresent()
                ? jsonFactory.toJson(optionalCart.get()).asBytes()
                : jsonObject.put("status", "No items in the cart").asBytes();
    }
}



