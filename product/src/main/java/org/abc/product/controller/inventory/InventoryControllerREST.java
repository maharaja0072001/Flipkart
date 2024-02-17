package org.abc.product.controller.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.abc.product.ProductCategory;
import org.abc.product.model.product.Product;
import org.abc.product.service.inventory.InventoryServiceREST;
import org.abc.product.service.inventory.impl2.InventoryServiceImpl;

import org.abc.product.validation.group.ClothesChecker;
import org.abc.product.validation.group.ElectronicProductChecker;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class InventoryControllerREST {

    private static InventoryControllerREST inventoryController;
    private final InventoryServiceREST inventory;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * <p>
     * Default constructor of InventoryController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private InventoryControllerREST() {
        inventory = InventoryServiceImpl.getInstance();
        objectMapper = new ObjectMapper();
        validator = Validation.byProvider(HibernateValidator.class)
                .configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory().getValidator();
    }

    /**
     * <p>
     * Creates a single object of InventoryController class and returns it.
     * </p>
     *
     * @return the single instance of InventoryController class.
     */
    public static InventoryControllerREST getInstance() {
        return Objects.isNull(inventoryController) ? inventoryController = new InventoryControllerREST() : inventoryController;
    }

    /**
     * <p>
     * Adds the given products to the inventory.
     * </p>
     *
     * @param products the products to be added.
     */
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public ArrayNode addItemToInventory(@Valid final List<Product> products) {
        final ArrayNode violationsInJson = objectMapper.createArrayNode();

        for (final Product product : products) {
           violationsInJson.add(switch (product.getProductCategory()) {
               case MOBILE, LAPTOP -> validate(ElectronicProductChecker.class, product);
               case CLOTHES -> validate(ClothesChecker.class, product);
           });
        }

        if (violationsInJson.isEmpty()) {
            inventory.addItem(products);
            violationsInJson.add(objectMapper.createObjectNode().put("status", "Successful"));
        }

        return violationsInJson;
    }

    /**
     * <p>
     * Removes the given item from the inventory.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     */
    @Path("/remove/{category}/{id}")
    @DELETE
    public ObjectNode removeItemFromInventory(@PathParam("id") final int productId,
                                              @PathParam("category") final ProductCategory productCategory) {
        inventory.removeItem(productId, productCategory);

        return objectMapper.createObjectNode().put("status", "Successful");
    }

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @return all the {@link Product} from the inventory.
     */
    @Path("/getByCategory")
    @GET
    public ObjectNode getItemsByCategory(@QueryParam("category") final ProductCategory productCategory,
                                         @QueryParam("page") final int page,
                                         @QueryParam("limit") final int limit) {
        return objectMapper.valueToTree(inventory.getItemsByCategory(productCategory, page, limit));
    }

    /**
     * <p>
     * Validates the object by the given group and returns object node containing the violations.
     * </p>
     * @param clazz Refers the group class.
     * @param product Refers the {@link Product}.
     * @return the object node contains the violations.
     */
    private ObjectNode validate(final Class clazz, final Product product) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

        validator.validate(product, clazz).forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        return violationsInJson;
    }
}
