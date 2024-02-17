package org.abc.product.controller.order;

import org.abc.product.model.order.Order;
import org.abc.product.service.order.impl2.OrderServiceImpl;
import org.abc.product.service.order.OrderService;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Interacts between OrderView and OrderService for adding , viewing and cancelling orders.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class OrderController {

    private static OrderController orderController;
    private final OrderService orderService;

    /**
     * <p>
     * Default constructor of OrderController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private OrderController() {
        orderService = (OrderService) OrderServiceImpl.getInstance();
    }

    /**
     * <p>
     * Creates a single object of OrderController class and returns it.
     * </p>
     *
     * @return the single instance of OrderController class.
     */
    public static OrderController getInstance() {
        return Objects.isNull(orderController) ? orderController = new OrderController() : orderController;
    }

    /**
     * <p>
     * Gets all the orders placed by the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @return  all the {@link Order} of the user.
     */
    public List<Order> getOrders(final int userId) {
        return orderService.getOrders(userId);
    }

    /**
     * <p>
     * Adds the order of the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @param order Refers the {@link Order} to be added.
     */
    public void addOrder(final int userId, final Order order) {
        orderService.addOrder(userId, order);
    }

    /**
     * <p>
     * Cancels the order placed by the user.
     * </p>
     *
     * @param order Refers the {@link Order} to be cancelled.
     */
    public void cancelOrder(final Order order) {
        orderService.cancelOrder(order);
    }

    /**
     * <p>
     * Adds the address of the user.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @param address Refers the address to be added.
     */
    public void addAddress(final int userId, final String address) {
        orderService.addAddress(userId, address);
    }

    /**
     * <p>
     * Gets all the addresses of the user.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return the list of all the address.
     */
    public List<String> getAllAddresses(final int userId) {
        return orderService.getAllAddresses(userId);
    }
}




