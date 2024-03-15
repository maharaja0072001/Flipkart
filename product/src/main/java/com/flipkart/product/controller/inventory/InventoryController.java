package com.flipkart.product.controller.inventory;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import com.flipkart.authentication.json.Json;
import com.flipkart.authentication.json.JsonArray;
import com.flipkart.authentication.json.JsonFactory;
import com.flipkart.authentication.json.JsonObject;
import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.model.product.Product;
import com.flipkart.product.service.inventory.InventoryService;
import com.flipkart.product.service.inventory.InventoryServiceImpl;
import com.flipkart.product.validation.group.ClothesChecker;
import com.flipkart.product.validation.group.ElectronicProductChecker;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
@Path("/inventory")
public class InventoryController {

    private final InventoryService inventory;
    private final JsonFactory jsonFactory;
    private final Validator validator;

    /**
     * <p>
     * Default constructor of InventoryController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private InventoryController() {
        inventory = InventoryServiceImpl.getInstance();
        jsonFactory = new JsonFactory();
        validator = Validation.byProvider(HibernateValidator.class)
                .configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory().getValidator();
    }

    /**
     * <p>
     * Creates a single instance of RestInventoryController class.
     * </p>
     */
    private static class InstanceHolder {

        private static final InventoryController REST_INVENTORY_CONTROLLER = new InventoryController();
    }

    /**
     * <p>
     * Gets a single instance of InventoryController class and returns it.
     * </p>
     *
     * @return the single instance of InventoryController class.
     */
    public static InventoryController getInstance() {
        return InstanceHolder.REST_INVENTORY_CONTROLLER;
    }

    /**
     * <p>
     * Adds the given products to the inventory.
     * </p>
     *
     * @param products the products to be added.
     */
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public byte[] addProduct(final List<Product> products) {
        final JsonArray violationsInJson = jsonFactory.createJsonArray();

        for (final Product product : products) {
            switch (product.getProductCategory()) {
                case MOBILE, LAPTOP -> {
                    final JsonObject jsonObject = (JsonObject) validate(ElectronicProductChecker.class, product);

                    if (Objects.nonNull(jsonObject)) {
                        violationsInJson.add(jsonObject);
                    }
                }
                case CLOTHES -> {
                    final JsonObject jsonObject = (JsonObject) validate(ClothesChecker.class, product);

                    if (Objects.nonNull(jsonObject)) {
                        violationsInJson.add(jsonObject);
                    }
                }
            }
        }

        if (violationsInJson.isEmpty()) {
            inventory.addProduct(products);
            violationsInJson.add(jsonFactory.createJsonObject().put("status", "Items added"));
        }

        return violationsInJson.asBytes();
    }

    /**
     * <p>
     * Removes the given item from the inventory.
     * </p>
     *
     * @param productId Refers the id of the product to be removed.
     */
    @Path("/{category}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    public byte[] removeProduct(@PathParam("id") final int productId,
                                @PathParam("category") final ProductCategory productCategory) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!inventory.isProductExist(productId)) {
            return jsonObject.put("status", "product id not found").asBytes();
        }

        return inventory.removeProduct(productId, productCategory)
                ? jsonFactory.createJsonObject().put("status", "Item removed").asBytes()
                : jsonFactory.createJsonObject().put("status", "Item not found").asBytes();
    }

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @param productCategory Refers the product category
     * @param page            Refers the page number.
     * @return all the {@link Product} from the inventory.
     */
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public byte[] getProductsByCategory(@QueryParam("category") final ProductCategory productCategory,
                                        @QueryParam("page") final int page) {
        final List<? extends Product> products = inventory.getItemsByCategory(productCategory, page);

        return products.isEmpty()
                ? jsonFactory.createJsonObject().put("status", "No items found").asBytes()
                : jsonFactory.toJson(products).asBytes();
    }

    /**
     * <p>
     * Validates the object by the given group and returns object node containing the violations.
     * </p>
     *
     * @param clazz   Refers the group class.
     * @param product Refers the {@link Product}.
     * @return the object node contains the violations.
     */
    private Json validate(final Class<?> clazz, final Product product) {
        final JsonObject violationsInJson = jsonFactory.createJsonObject();

        validator.validate(product, clazz).forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        return violationsInJson.isEmpty() ? null : violationsInJson;
    }
}
