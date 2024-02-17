package org.abc.product.controller.wishlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.abc.product.ProductCategory;
import org.abc.product.model.wishlist.Wishlist;
import org.abc.product.service.wishlist.WishlistServiceREST;
import org.abc.product.service.wishlist.impl2.WishlistServiceImpl;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.Objects;

/**
 * <p>
 * Handles requests and responses from WishlistView clas and WishlistService class.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class WishlistControllerREST {

    private static WishlistControllerREST wishlistController;
    private final WishlistServiceREST wishlistService;
    private final ObjectMapper objectMapper;

    /**
     * <p>
     * Default constructor of the WishlistController class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private WishlistControllerREST() {
        wishlistService = WishlistServiceImpl.getInstance();
        objectMapper = new ObjectMapper();
    }

    /**
     * <p>
     * Creates a single object of WishlistController Class and returns it.
     * </p>
     *
     * @return returns the single instance of WishlistController Class.
     */
    public static WishlistControllerREST getInstance() {
        return Objects.isNull(wishlistController) ? wishlistController = new WishlistControllerREST() : wishlistController;
    }

    /**
     * <p>
     * Adds the product to the wishlist of the specified user.
     * </p>
     * @param productId Refers the id of the product to be added
     * @param userId Refers the user id.
     * @param productCategory Refers the product category.
     * @return the if product added to the wishlist.
     */
    @Path("/add/{userId}/{category}/{productId}")
    @POST
    public ObjectNode addItem(@PathParam("productId") final int productId,
                              @PathParam("userId") final int userId,
                              @PathParam("category") final ProductCategory productCategory) {
        return objectMapper.createObjectNode().put("status", wishlistService.addItem(productId, userId, productCategory));
    }

    /**
     * <p>
     * Removes the product from the wishlist of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId Refers the user id.
     */
    @Path("/remove/{userId}/{productId}")
    @DELETE
    public ObjectNode removeItem(@PathParam("productId") final int productId,
                                 @PathParam("userId") final int userId) {
        wishlistService.removeItem(productId, userId);

        return objectMapper.createObjectNode().put("status", "Successful");
    }

    /**
     * <p>
     * Gets the wishlist of the specified user id and returns it.
     * </p>
     *
     * @param userId Refers the user id who owns the cart.
     * @return the {@link Wishlist} of the user.
     */
    @Path("/get/{userId}")
    @GET
    public ObjectNode getWishlist(@PathParam("userId") final int userId,
                                  @QueryParam("page") final int page,
                                  @QueryParam("limit") final int limit) {
        return objectMapper.valueToTree(wishlistService.getWishlist(userId, page, limit));
    }
}
