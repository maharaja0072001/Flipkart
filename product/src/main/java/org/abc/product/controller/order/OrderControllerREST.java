package org.abc.product.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.abc.authentication.validation.groups.GetUserChecker;
import org.abc.product.model.order.Order;
import org.abc.product.service.order.OrderServiceREST;
import org.abc.product.service.order.impl2.OrderServiceImpl;

import org.abc.product.validation.group.AddressChecker;
import org.abc.product.validation.group.OrderChecker;
import org.abc.product.validation.group.UserIdChecker;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PATCH;

import java.util.Objects;

/**
 * <p>
 * Interacts between OrderView and OrderService for adding , viewing and cancelling orders.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class OrderControllerREST {

    private static OrderControllerREST orderController;
    private final OrderServiceREST orderService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * <p>
     * Default constructor of OrderController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private OrderControllerREST() {
        orderService = OrderServiceImpl.getInstance();
        objectMapper = new ObjectMapper();
        validator = Validation.byProvider(HibernateValidator.class)
                .configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory().getValidator();
    }

    /**
     * <p>
     * Creates a single object of OrderController class and returns it.
     * </p>
     *
     * @return the single instance of OrderController class.
     */
    public static OrderControllerREST getInstance() {
        return Objects.isNull(orderController) ? orderController = new OrderControllerREST() : orderController;
    }

    /**
     * <p>
     * Gets all the orders placed by the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @return  all the {@link Order} of the user.
     */
    @Path("/getOrders/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    public ObjectNode getOrders(@PathParam("userId") final int userId,
                                @QueryParam("page") final int page,
                                @QueryParam("limit") final int limit) {
        final ObjectNode violationsInJson = validate(UserIdChecker.class, new Order.OrderBuilder(userId).build());

        return violationsInJson.isEmpty()
                ? objectMapper.valueToTree(orderService.getOrders(userId, page, limit))
                : violationsInJson;
    }

    /**
     * <p>
     * Adds the order of the user.
     * </p>
     *
     * @param order Refers the {@link Order} to be added.
     */
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public ObjectNode addOrder(final Order order) {
        final ObjectNode violationsInJson = validate(OrderChecker.class, order);

        if (violationsInJson.isEmpty()) {
            orderService.addOrder(order.getUserId(), order);

            return objectMapper.createObjectNode().put("status", "Successful");
        } else {
            return violationsInJson;
        }
    }

    /**
     * <p>
     * Cancels the order placed by the user.
     * </p>
     *
     * @param order Refers the {@link Order} to be cancelled.
     */
    @Path("/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    @PATCH
    public ObjectNode cancelOrder(final Order order) {
        final ObjectNode violationsInJson = validate(OrderChecker.class, order);

        if (violationsInJson.isEmpty()) {
            orderService.cancelOrder(order);

            return objectMapper.createObjectNode().put("status", "Successful");
        } else {
            return violationsInJson;
        }
    }

    /**
     * <p>
     * Adds the address of the user.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @param address Refers the address to be added.
     */
    @Path("/addAddress/{userId}/{address}")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public ObjectNode addAddress(@PathParam("userId") final int userId,
                                 @PathParam("address") final String address) {
        final ObjectNode violationsInJson = validate(AddressChecker.class, new Order.OrderBuilder(userId).setAddress(address).build());

        if (violationsInJson.isEmpty()) {
            orderService.addAddress(userId, address);

            return objectMapper.createObjectNode().put("status","Successful");
        } else {
            return  violationsInJson;
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
    @Path("/getAddresses/{userId}")
    @GET
    public ObjectNode getAllAddresses(@PathParam("userId") final int userId) {
        final ObjectNode violationsInJson = validate(GetUserChecker.class, new Order.OrderBuilder(userId).build());

        return violationsInJson.isEmpty()
                ? objectMapper.valueToTree(orderService.getAllAddresses(userId))
                : violationsInJson;
    }

    /**
     * <p>
     * Validates the object by the given group and returns object node containing the violations.
     * </p>
     * @param clazz Refers the group class.
     * @param order Refers the {@link Order}.
     * @return the object node contains the violations.
     */
    private ObjectNode validate(final Class clazz, final Order order) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

        validator.validate(order, clazz).forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        return violationsInJson;
    }
}




