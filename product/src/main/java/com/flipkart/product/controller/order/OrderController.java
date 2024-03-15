package com.flipkart.product.controller.order;

import com.flipkart.product.model.order.Order;
import com.flipkart.product.service.order.OrderService;
import com.flipkart.product.service.order.OrderServiceImpl;
import com.flipkart.product.validation.group.AddressChecker;
import com.flipkart.product.validation.group.CancelOrderChecker;
import com.flipkart.product.validation.group.UserIdChecker;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import com.flipkart.authentication.json.Json;
import com.flipkart.authentication.json.JsonFactory;
import com.flipkart.authentication.json.JsonObject;
import com.flipkart.authentication.model.Address;
import com.flipkart.authentication.validation.groups.GetUserChecker;
import com.flipkart.authentication.validation.groups.OrderChecker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * <p>
 * Interacts between OrderView and OrderService for adding , viewing and cancelling orders.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/order")
public class OrderController {

    private final OrderService orderService;
    private final JsonFactory jsonFactory;
    private final Validator validator;
    private static final Logger LOGGER = LogManager.getLogger(OrderController.class);

    /**
     * <p>
     * Default constructor of OrderController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private OrderController() {
        orderService = OrderServiceImpl.getInstance();
        jsonFactory = new JsonFactory();
        validator = Validation.byProvider(HibernateValidator.class).configure()
                .messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory().getValidator();
    }

    /**
     * <p>
     * Creates a single instance of RestOrderController class.
     * </p>
     */
    private static class InstanceHolder {

        private static final OrderController ORDER_CONTROLLER = new OrderController();
    }

    /**
     * <p>
     * Gets a single instance of OrderController class and returns it.
     * </p>
     *
     * @return the single instance of OrderController class.
     */
    public static OrderController getInstance() {
        return InstanceHolder.ORDER_CONTROLLER;
    }

    /**
     * <p>
     * Gets all the orders placed by the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @return all the {@link Order} of the user.
     */
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public byte[] getOrders(@PathParam("userId") final int userId,
                            @QueryParam("page") final int page) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!orderService.isUserExist(userId)) {
            return jsonObject.put("status", "User not found").asBytes();
        }

        final JsonObject violationsInJson = (JsonObject) validate(UserIdChecker.class, new Order.OrderBuilder(userId).build());

        if (violationsInJson.isEmpty()) {
            List<Order> orders = orderService.getOrders(userId, page);

            return !orders.isEmpty()
                    ? jsonFactory.toJson(orders).asBytes()
                    : jsonObject.put("status", "No orders found").asBytes();
        } else {
            return violationsInJson.asBytes();
        }
    }

    /**
     * <p>
     * Adds the order of the user.
     * </p>
     *
     * @param order Refers the {@link Order} to be added.
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public byte[] addOrder(final Order order) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!orderService.isProductExist(order.getProductId())) {
            return jsonObject.put("status", "product not found").asBytes();
        } else if (!orderService.isUserExist(order.getUserId())) {
            return jsonObject.put("status", "User not found").asBytes();
        }
        final JsonObject violationsInJson = (JsonObject) validate(OrderChecker.class, order);

        if (violationsInJson.isEmpty()) {
            orderService.addOrder(order.getUserId(), order);
            LOGGER.info(String.format("User id :%d Product Id :%d - Order placed successfully", order.getUserId(), order.getProductId()));

            return jsonObject.put("status", "Order placed successfully").asBytes();
        } else {
            return violationsInJson.asBytes();
        }
    }

    /**
     * <p>
     * Cancels the order placed by the user.
     * </p>
     *
     * @param order Refers the {@link Order} to be cancelled.
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    public byte[] cancelOrder(final Order order) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!orderService.isProductExist(order.getProductId())) {
            return jsonObject.put("status", "product not found").asBytes();
        } else if (!orderService.isUserExist(order.getUserId())) {
            return jsonObject.put("status", "User not found").asBytes();
        }
        final JsonObject violationsInJson = (JsonObject) validate(CancelOrderChecker.class, order);

        if (violationsInJson.isEmpty()) {
            orderService.cancelOrder(order);
            LOGGER.info(String.format("User id :%d Product Id :%d - Order cancelled successfully.",
                    order.getUserId(), order.getProductId()));

            return jsonObject.put("status", "Order cancelled successfully").asBytes();
        } else {
            return violationsInJson.asBytes();
        }
    }

    /**
     * <p>
     * Adds the address of the user.
     * </p>
     *
     * @param userId  Refers the id of the user.
     * @param address Refers the address to be added.
     */
    @Path("/address/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public byte[] addAddress(@PathParam("userId") final int userId, final Address address) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!orderService.isUserExist(userId)) {
            return jsonObject.put("status", "User not found").asBytes();
        }
        final JsonObject violationsInJson = (JsonObject) validate(AddressChecker.class, new Order.OrderBuilder(userId).setAddress(address).build());

        if (violationsInJson.isEmpty()) {
            orderService.addAddress(userId, address);
            LOGGER.info(String.format("User id :%d - Address added successfully.", userId));

            return jsonFactory.createJsonObject().put("status", "Address added successfully").asBytes();
        } else {
            return violationsInJson.asBytes();
        }
    }

    /**
     * <p>
     * Gets all the addresses of the user.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return the list of all the address.
     */
    @Path("/address/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public byte[] getAddresses(@PathParam("userId") final int userId) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();

        if (!orderService.isUserExist(userId)) {
            return jsonObject.put("status", "User not found").asBytes();
        }
        final JsonObject violationsInJson = (JsonObject) validate(GetUserChecker.class, new Order.OrderBuilder(userId).build());

        if (violationsInJson.isEmpty()) {
            List<Address> addresses = orderService.getAddresses(userId);

            return !addresses.isEmpty()
                    ? jsonFactory.toJson(addresses).asBytes()
                    : jsonFactory.createJsonObject().put("status", "No address found").asBytes();
        } else {
            return violationsInJson.asBytes();
        }
    }

    /**
     * <p>
     * Validates the object by the given group and returns object node containing the violations.
     * </p>
     *
     * @param clazz Refers the group class.
     * @param order Refers the {@link Order}.
     * @return the object node contains the violations.
     */
    private Json validate(final Class<?> clazz, final Order order) {
        final JsonObject violationsInJson = jsonFactory.createJsonObject();

        validator.validate(order, clazz).forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        return violationsInJson;
    }
}