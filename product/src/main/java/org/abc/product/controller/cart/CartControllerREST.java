package org.abc.product.controller.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.abc.product.ProductCategory;
import org.abc.product.model.cart.Cart;
import org.abc.product.service.cart.CartServiceREST;
import org.abc.product.service.cart.impl2.CartServiceImpl;

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
 * Interacts between CartView and CartService for adding , removing from the cart of the user.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class CartControllerREST {

    private static CartControllerREST cartController;
    private final CartServiceREST cartService;
    private final ObjectMapper objectMapper;

    /**
     * <p>
     * Default constructor of the CartController class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private CartControllerREST() {
        cartService = CartServiceImpl.getInstance();
        objectMapper = new ObjectMapper();
    }

    /**
     * <p>
     * Creates a single object of CartController Class and returns it.
     * </p>
     *
     * @return returns the single instance of CartController Class.
     */
    public static CartControllerREST getInstance() {
        return Objects.isNull(cartController) ? cartController = new CartControllerREST() : cartController;
    }

    /**
     * <p>
     * Adds the product to the cart of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be added
     * @param userId Refers the user id.
     * @param productCategory Refers the product category.
     * @return true if product added.
     */
    @Path("/add/{userId}/{category}/{productId}")
    @POST
    public ObjectNode addItem(@PathParam("productId") final int productId,
                              @PathParam("userId") final int userId,
                              @PathParam("category") final ProductCategory productCategory) {
        return objectMapper.createObjectNode().put("status", cartService.addItem(productId, userId, productCategory));
    }

    /**
     * <p>
     * Removes the product from the cart of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId Refers the user id.
     */
    @Path("/remove/{userId}/{productId}")
    @DELETE
    public ObjectNode removeItem(@PathParam("productId") final int productId,
                                 @PathParam("userId") final int userId) {
        cartService.removeItem(productId, userId);

        return objectMapper.createObjectNode().put("status", "Successful");
    }

    /**
     * <p>
     * Gets the cart of the specified user id and returns it.
     * </p>
     *
     * @param userId Refers the user id.
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @return the {@link Cart} of the user.
     */
    @Path("/get/{userId}")
    @GET
    public ObjectNode getCart(@PathParam("userId") final int userId,
                              @QueryParam("page") final int page,
                              @QueryParam("limit") final int limit) {
        return objectMapper.valueToTree(cartService.getCart(userId, page, limit));
    }
}



