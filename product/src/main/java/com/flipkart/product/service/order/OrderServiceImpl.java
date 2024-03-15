package com.flipkart.product.service.order;

import com.flipkart.authentication.dao.v1.UserDAOImpl;
import com.flipkart.authentication.model.Address;
import com.flipkart.product.dao.order.OrderDAO;
import com.flipkart.product.dao.order.v1.OrderDAOImpl;
import com.flipkart.product.dao.inventory.v1.InventoryDAOImpl;
import com.flipkart.product.model.order.Order;

import java.util.List;

/**
 * <p>
 * Provides the service for the Order.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO;

    /**
     * <p>
     * Default constructor of the OrderServiceImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private OrderServiceImpl() {
        orderDAO = OrderDAOImpl.getInstance();
    }

    /**
     * <p>
     * Creates a single instance of OrderServiceImpl class.
     * </p>
     */
    private static class InstanceHolder {

        private static final OrderServiceImpl ORDER_SERVICE = new OrderServiceImpl();
    }

    /**
     * <p>
     * Gets a single object of OrderServiceImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of OrderServiceImpl Class.
     */
    public static OrderService getInstance() {
        return InstanceHolder.ORDER_SERVICE;
    }

    /**
     * <p>
     * Adds the order placed by the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @param order  Refers the {@link Order} to be added.
     */
    @Override
    public void addOrder(final int userId, final Order order) {
        orderDAO.addOrder(userId, order);
    }

    /**
     * <p>
     * Gets all the orders placed by the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @param page   Refers the page number.
     * @return all the {@link Order} of the user.
     */
    @Override
    public List<Order> getOrders(final int userId, final int page) {
        return orderDAO.getOrders(userId, page);
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
        orderDAO.cancelOrder(order);
    }

    /**
     * <p>
     * Adds the address of the user.
     * </p>
     *
     * @param userId  Refers the id of the user.
     * @param address Refers the address to be added.
     */
    @Override
    public void addAddress(final int userId, final Address address) {
        orderDAO.addAddress(userId, address);
    }

    /**
     * <p>
     * Gets all the addresses of the user.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return the list of all the address.
     */
    @Override
    public List<Address> getAddresses(final int userId) {
        return orderDAO.getAddresses(userId);
    }

    /**
     * <p>
     * Checks whether the user exists or not.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return true if the user already exists or false otherwise.
     */
    @Override
    public boolean isUserExist(final int userId) {
        return UserDAOImpl.getInstance().isUserExist(userId);
    }

    /**
     * <p>
     * Checks whether the product exists or not.
     * </p>
     *
     * @param productId Refers the id of the product.
     * @return true if the user already exists or false otherwise.
     */
    @Override
    public boolean isProductExist(final int productId) {
        return InventoryDAOImpl.getInstance().isProductExist(productId);
    }
}
