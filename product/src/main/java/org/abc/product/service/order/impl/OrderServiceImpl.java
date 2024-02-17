package org.abc.product.service.order.impl;

import org.abc.product.OrderStatus;
import org.abc.product.model.order.Order;
import org.abc.product.service.order.OrderService;
import org.abc.authentication.service.impl2.UserServiceImpl;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * Provides the service for the Order.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class OrderServiceImpl implements OrderService {

    private static OrderService orderService;
    private final Map<Integer, List<Order>> orders;

    /**
     * <p>
     * Default constructor of the OrderServiceImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private OrderServiceImpl() {
        orders = new HashMap<>();
    }

    /**
     * <p>
     * Creates a single object of OrderServiceImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of OrderServiceImpl Class.
     */
    public static OrderService getInstance() {
        return Objects.isNull(orderService) ? orderService = new OrderServiceImpl() : orderService;
    }

    /**
     * <p>
     * Adds the order of the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @param order Refers the {@link Order} to be added.
     */
    @Override
    public void addOrder(final int userId, final Order order) {
        orders.computeIfAbsent(userId, key -> new ArrayList<>()).add(order);
    }

    /**
     * <p>
     * Gets all the orders placed by the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @return  all the {@link Order} of the user.
     */
    @Override
    public List<Order> getOrders(final int userId) {
        return orders.get(userId);
    }

    /**
     * <p>
     * Cancels the order placed by the user.
     * </p>
     *
     * @param order Refers the {@link Order} to be cancelled.
     */
    @Override
    public void cancelOrder(final Order order) {
        order.setOrderStatusId(OrderStatus.CANCELLED.getId());
    }

    /**
     * <p>
     * Adds the address of the user.
     * </p>
     *
     * @param userId Refers the is of the user.
     * @param address Refers the address to be added.
     */
    @Override
    public void addAddress(final int userId, final String address) {
       UserServiceImpl.getInstance().getUserById(userId).addAddress(address);
    }

    /**
     * <p>
     * Gets all the addresses of the user.
     * </p>
     *
     * @param userId Refers the user id..
     * @return the list of all the address.
     */
    @Override
    public List<String> getAllAddresses(final int userId) {
        return UserServiceImpl.getInstance().getUserById(userId).getAddresses();
    }
}
