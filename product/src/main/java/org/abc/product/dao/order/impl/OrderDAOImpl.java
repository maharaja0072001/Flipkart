package org.abc.product.dao.order.impl;

import org.abc.product.OrderStatus;
import org.abc.product.ProductCategory;
import org.abc.authentication.exceptions.UpdateActionFailedException;
import org.abc.authentication.exceptions.UserNotFoundException;
import org.abc.product.dao.order.OrderDAO;
import org.abc.dbconnection.connection.DBConnection;
import org.abc.product.exceptions.ItemUpdateFailedException;
import org.abc.product.exceptions.OrderAdditionFailedException;
import org.abc.product.exceptions.OrderRemovalFailedException;
import org.abc.product.exceptions.OrderNotFoundException;
import org.abc.product.model.order.Order;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Stores all the order details in the database.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class OrderDAOImpl implements OrderDAO {

    private static OrderDAOImpl orderDAO;
    private final Connection connection;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of the OrderDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private OrderDAOImpl() {
        connection = DBConnection.getConnection();
        logger = LogManager.getLogger(OrderDAOImpl.class);
    }

    /**
     * <p>
     * Creates a single object of OrderDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of OrderDAOImpl Class.
     */
    public static OrderDAO getInstance() {
        return Objects.isNull(orderDAO) ? orderDAO = new OrderDAOImpl() : orderDAO;
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
        final int productId = order.getProductId();

        try (PreparedStatement preparedStatement = connection.prepareStatement(String.join(" ",
                "insert into orders(user_id, product_id, address, payment_mode_id,",
                "quantity, total_amount, order_status_id) values (?,?,?,?,?,?,?)"))) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            preparedStatement.setString(3, order.getAddress());
            preparedStatement.setInt(4, order.getPaymentModeId());
            preparedStatement.setInt(5, order.getQuantity());
            preparedStatement.setFloat(6, order.getTotalAmount());
            preparedStatement.setInt(7, order.getOrderStatusId());
            preparedStatement.executeUpdate();
            updateQuantity(productId, order.getQuantity());
            logger.info(String.format("User id :%d Product Id :%d - Order placed successfully", userId, productId));
        } catch (final SQLException exception) {
            logger.info(String.format("User id :%d Product Id :%d - Order not placed", userId, productId));
            throw new OrderAdditionFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Updates the quantity of product when order is placed by the user.
     * </p>
     *
     * @param productId Refers the id of the product.
     * @param quantity Refers the quantity to be updated.
     */
    private void updateQuantity(final int productId, final int quantity) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("update product set quantity = quantity - ? where id =?")) {
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();
        } catch (final SQLException exception) {
            throw new ItemUpdateFailedException(exception.getMessage());
        }
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
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("update orders set order_status_id =? where id =?")) {
            preparedStatement.setInt(1, OrderStatus.CANCELLED.getId());
            preparedStatement.setInt(2, order.getId());
            preparedStatement.executeUpdate();
            updateQuantity(order.getProductId(), -order.getQuantity());
            logger.info(String.format("User id :%d Product Id :%d - Order cancelled successfully.", order.getUserId(), order.getProductId()));
        } catch (final SQLException exception) {
            logger.warn(String.format("User id :%d Product Id :%d - Order can't be cancelled.", order.getUserId(), order.getProductId()));
            throw new OrderRemovalFailedException(exception.getMessage());
        }
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
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select o.id,o.product_id,",
                        "o.payment_mode_id,o.quantity,o.total_amount, o.address,o.order_status_id, p.product_category_id,",
                        "e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand from orders o join product p",
                        "on o.product_id=p.id  left join electronics_inventory e on o.product_id = e.product_id",
                        "left join clothes_inventory c on o.product_id=c.product_id where o.user_id=?"))) {
            preparedStatement.setInt(1, userId);

            return getOrdersFromResultSet(preparedStatement.executeQuery(), userId);
        } catch (final SQLException exception) {
            throw new OrderNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the orders placed by the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @return  all the {@link Order} of the user.
     */
    @Override
    public List<Order> getOrders(final int userId, final int page, final int limit) {
        final int offset = (page - 1) * limit;

        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select o.id,o.product_id,",
                        "o.payment_mode_id,o.quantity,o.total_amount, o.address,o.order_status_id, p.product_category_id,",
                        "e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand from orders o join product p",
                        "on o.product_id=p.id  left join electronics_inventory e on o.product_id = e.product_id",
                        "left join clothes_inventory c on o.product_id=c.product_id where o.user_id=? order by order.id offset ? limit ?"))) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

            return getOrdersFromResultSet(preparedStatement.executeQuery(), userId);
        } catch (final SQLException exception) {
            throw new OrderNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the orders from the provided ResultSet.
     * </p>
     *
     * @param resultSet Refers the result set.
     * @param userId Refers the user id.
     * @return  all the {@link Order} of the user.
     */
    private List<Order> getOrdersFromResultSet(final ResultSet resultSet, final int userId) throws SQLException {
        final List<Order> orders = new ArrayList<>();

        while (resultSet.next()) {
            final int orderId = resultSet.getInt(1);
            final int productId = resultSet.getInt(2);
            String productName = null;
            final int paymentModeId = resultSet.getInt(3);
            final int quantity = resultSet.getInt(4);
            final float totalAmount = resultSet.getFloat(5);
            final String address = resultSet.getString(6);
            final int orderStatusId = resultSet.getInt(7);
            final ProductCategory productCategory = ProductCategory.valueOf(resultSet.getInt(8));

            if (ProductCategory.MOBILE == productCategory || ProductCategory.LAPTOP == productCategory) {
                final String brand = resultSet.getString(9);
                final String model = resultSet.getString(10);
                final float price = resultSet.getFloat(11);
                productName = String.format("Product name : %s %s - Rs :%.2f", brand, model, price);
            }

            if (ProductCategory.CLOTHES == productCategory) {
                final float price = resultSet.getFloat(11);
                final String clothesType = resultSet.getString(12);
                final String size = resultSet.getString(13);
                final String gender = resultSet.getString(14);
                final String brand = resultSet.getString(15);
                productName = String.format("%s brand :%s size : %s gender: %s - Rs :%.2f ", clothesType, brand, size, gender, price);
            }
            final Order order = new Order.OrderBuilder(userId).setId(orderId)
                    .setProductName(productName).setTotalAmount(totalAmount).setQuantity(quantity).setAddress(address)
                    .setOrderStatusId(orderStatusId).setPaymentModeId(paymentModeId).setProductId(productId).build();

            orders.add(order);
        }

        return orders;
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
    public List<String> getAllAddresses(final int userId) {
        final List<String> addresses = new ArrayList<>();

        try (PreparedStatement preparedStatement = DBConnection.getConnection()
                .prepareStatement("select address from address join users on users.id=address.user_id where user_id =?")) {
            preparedStatement.setInt(1, userId);
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final String address = resultSet.getString(1);
                addresses.add(address);
            }
        } catch (final SQLException exception) {
            throw new UserNotFoundException(exception.getMessage());
        }

        return addresses;
    }

    /**
     * <p>
     * Adds the address of the user.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @param address Refers the address to be added.
     */
    @Override
    public void addAddress(final int userId, final String address) {
        try (PreparedStatement preparedStatement = DBConnection.getConnection()
                .prepareStatement("insert into address(user_id, address) values (?,?)")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, address);
            preparedStatement.executeUpdate();
            logger.info(String.format("User id :%d - Address added successfully.", userId));
        } catch (final SQLException exception) {
            logger.warn(String.format("User id :%d - Address not added", userId));
            throw new UpdateActionFailedException(exception.getMessage());
        }
    }
}
