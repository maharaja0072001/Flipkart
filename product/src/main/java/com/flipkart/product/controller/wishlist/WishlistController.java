package com.flipkart.product.controller.wishlist;

import com.flipkart.authentication.json.JsonFactory;
import com.flipkart.authentication.json.JsonObject;
import com.flipkart.product.model.wishlist.Wishlist;
import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.service.wishlist.WishlistService;
import com.flipkart.product.service.wishlist.WishlistServiceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Optional;

/**
 * <p>
 * Handles requests and responses from WishlistService class.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final JsonFactory jsonFactory;
    private static final Logger LOGGER = LogManager.getLogger(WishlistService.class);

    /**
     * <p>
     * Default constructor of the WishlistController class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private WishlistController() {
        wishlistService = WishlistServiceImpl.getInstance();
        jsonFactory = new JsonFactory();
    }

    /**
     * <p>
     * Creates a single instance of RestWishlistController class.
     * </p>
     */
    private static class InstanceHolder {

        private static final WishlistController REST_WISHLIST_CONTROLLER = new WishlistController();
    }

    /**
     * <p>
     * Gets a single instance of WishlistController Class and returns it.
     * </p>
     *
     * @return returns the single instance of WishlistController Class.
     */
    public static WishlistController getInstance() {
        return InstanceHolder.REST_WISHLIST_CONTROLLER;
    }

    /**
     * <p>
     * Adds the product to the wishlist of the specified user.
     * </p>
     *
     * @param productId       Refers the id of the product to be added
     * @param userId          Refers the user id.
     * @param productCategory Refers the product category.
     * @return the if product added to the wishlist.
     */
    @Path("/{userId}/{category}/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public byte[] addProduct(@PathParam("productId") final int productId,
                             @PathParam("userId") final int userId,
                             @PathParam("category") final ProductCategory productCategory) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!wishlistService.isUserExist(userId)) {
            return jsonObject.put("status", "User id not found").asBytes();
        } else if (!wishlistService.isProductExist(productId)) {
            return jsonObject.put("status", "product id not found").asBytes();
        }

        if (wishlistService.addProduct(productId, userId, productCategory)) {
            LOGGER.info(String.format("User id :%d Product Id :%d - Item added to the wishlist", userId, productId));

            return jsonObject.put("status", "Item added to the wishlist").asBytes();
        } else {
            LOGGER.info(String.format("User id :%d Product Id :%d - Item is already in the wishlist", userId, productId));

            return jsonObject.put("status", "Item is already in the wishlist").asBytes();
        }
    }

    /**
     * <p>
     * Removes the product from the wishlist of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId    Refers the user id.
     */
    @Path("/{userId}/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    public byte[] removeProduct(@PathParam("productId") final int productId, @PathParam("userId") final int userId) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!wishlistService.isProductExist(productId)) {
            return jsonObject.put("status", "product not found").asBytes();
        } else if (!wishlistService.isUserExist(userId)) {
            return jsonObject.put("status", "User not found").asBytes();
        }
        final boolean status = wishlistService.removeProduct(productId, userId);

        if (status) {
            LOGGER.info(String.format("User id :%d Product Id :%d - Item removed from the wishlist", userId, productId));

            return jsonObject.put("status", "Item removed from the wishlist").asBytes();
        } else {
            LOGGER.info(String.format("User id :%d Product Id :%d - Item not found", userId, productId));

            return jsonObject.put("status", "Item not found").asBytes();
        }
    }

    /**
     * <p>
     * Gets the wishlist of the specified user id and returns it.
     * </p>
     *
     * @param userId Refers the user id who owns the cart.
     * @return the {@link Wishlist} of the user.
     */
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public byte[] getWishlist(@PathParam("userId") final int userId,
                              @QueryParam("page") final int page) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!wishlistService.isUserExist(userId)) {
            return jsonObject.put("status", "User not found").asBytes();
        }
        final Optional<Wishlist> optionalWishlist = wishlistService.getWishlist(userId, page);

        return optionalWishlist.isPresent()
                ? jsonFactory.toJson(optionalWishlist.get()).asBytes()
                : jsonObject.put("status", "No items in wishlist").asBytes();
    }
}
