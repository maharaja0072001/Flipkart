package com.flipkart.product.service.order;

import com.flipkart.authentication.model.Address;
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
public interface OrderService {

    /**
     * <p>
     * Adds the order placed by the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @param order Refers the {@link Order} to be added.
     */
    void addOrder(final int userId, final Order order);

    /**
     * <p>
     * Gets all the orders placed by the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @param page Refers the page number.
     * @return all the {@link Order} of the user.
     */
    List<Order> getOrders(final int userId, final int page);

    /**
     * <p>
     * Cancels the order placed by the user.
     * </p>
     *
     * @param order Refers the {@link Order} to be cancelled.
     */
    void cancelOrder(final Order order);

    /**
     * <p>
     * Adds the address of the user.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @param address Refers the address to be added.
     */
    void addAddress(final int userId, final Address address);

    /**
     * <p>
     * Gets all the addresses of the user.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return the list of all the address.
     */
    List<Address> getAddresses(final int userId);

    /**
     * <p>
     * Checks whether the user exists or not.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return true if the user already exists or false otherwise.
     */
    boolean isUserExist(final int userId);

    /**
     * <p>
     * Checks whether the product exists or not.
     * </p>
     *
     * @param productId Refers the id of the product.
     * @return true if the user already exists or false otherwise.
     */
    boolean isProductExist(final int productId);
}
