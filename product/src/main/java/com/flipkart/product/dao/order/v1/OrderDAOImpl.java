package com.flipkart.product.dao.order.v1;

import com.flipkart.authentication.model.Address;
import com.flipkart.product.dao.order.OrderDAO;
import com.flipkart.product.model.OrderStatus;
import com.flipkart.product.model.ProductCategory;
import com.flipkart.authentication.exceptions.UpdateActionFailedException;
import com.flipkart.authentication.exceptions.UserNotFoundException;
import com.flipkart.database.connection.DatabaseConnection;
import com.flipkart.product.exceptions.*;
import com.flipkart.product.model.order.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Stores all the order details in the database.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class OrderDAOImpl implements OrderDAO {

    private final Connection connection;

    /**
     * <p>
     * Default constructor of the OrderDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private OrderDAOImpl() {
        connection = DatabaseConnection.get();
    }

    /**
     * <p>
     * Creates a single instance of OrderDAOImpl class.
     * </p>
     */
    private static class InstanceHolder {

        private static final OrderDAOImpl ORDER_DAO = new OrderDAOImpl();
    }

    /**
     * <p>
     * Gets a single object of OrderDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of OrderDAOImpl Class.
     */
    public static OrderDAO getInstance() {
        return InstanceHolder.ORDER_DAO;
    }

    /**
     * <p>
     * Adds the order of the user.
     * </p>
     *
     * @param userId Refers the id of the user
     * @param order  Refers the {@link Order} to be added.
     */
    @Override
    public void addOrder(final int userId, final Order order) {
        final int productId = order.getProductId();

        try (final PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "insert into orders(user_id, product_id, address_id,",
                        "payment_mode_id, quantity, total_amount, order_status_id) values (?,?,?,?,?,?,?)"))) {
            connection.setAutoCommit(false);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, order.getAddress().getId());
            preparedStatement.setInt(4, order.getPaymentModeId());
            preparedStatement.setInt(5, order.getQuantity());
            preparedStatement.setFloat(6, order.getTotalAmount());
            preparedStatement.setInt(7, order.getOrderStatusId());
            preparedStatement.executeUpdate();
            addAddress(userId, order.getAddress());
            updateQuantity(productId, order.getQuantity());
            connection.commit();
        } catch (SQLException exception) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollbackFailedException(e.getMessage());
            }
            throw new OrderAdditionFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Updates the quantity of product when order is placed by the user.
     * </p>
     *
     * @param productId Refers the id of the product.
     * @param quantity  Refers the quantity to be updated.
     */
    private void updateQuantity(final int productId, final int quantity) {
        try (final PreparedStatement preparedStatement = connection
                .prepareStatement("update product set quantity = quantity - ? where id =?")) {
            connection.setAutoCommit(false);
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException exception) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollbackFailedException(e.getMessage());
            }
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
        try (final PreparedStatement preparedStatement = connection
                .prepareStatement("update orders set order_status_id =? where id =?")) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, OrderStatus.CANCELLED.getId());
            preparedStatement.setInt(2, order.getId());
            preparedStatement.executeUpdate();
            updateQuantity(order.getProductId(), -order.getQuantity());
        } catch (SQLException exception) {
            throw new OrderRemovalFailedException(exception.getMessage());
        }
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
        final int limit = 5;
        final int offset = (page - 1) * limit;

        try (final PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select o.id,o.product_id,",
                        "o.payment_mode_id,o.quantity,o.total_amount,o.order_status_id, p.product_category_id,",
                        "e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand,a.door_number, a.street,",
                        "a.city, a.state, a.country, a.pin_code from orders o join product p",
                        "on o.product_id=p.id  left join electronics_inventory e on o.product_id = e.product_id",
                        "left join clothes_inventory c on o.product_id=c.product_id inner join address a on a.id= o.address_id",
                        "where o.user_id=? order by o.id offset ? limit ?"))) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

            return getOrdersFromResultSet(preparedStatement.executeQuery(), userId);
        } catch (SQLException exception) {
            throw new OrderNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the orders from the provided ResultSet.
     * </p>
     *
     * @param resultSet Refers the result set.
     * @param userId    Refers the user id.
     * @return all the {@link Order} of the user.
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
            final int orderStatusId = resultSet.getInt(6);
            final ProductCategory productCategory = ProductCategory.valueOf(resultSet.getInt(7));
            final String doorNumber = resultSet.getString(14);
            final String street = resultSet.getString(15);
            final String city = resultSet.getString(16);
            final String state = resultSet.getString(17);
            final String country = resultSet.getString(18);
            final int pinCode = resultSet.getInt(19);

            if (ProductCategory.MOBILE == productCategory || ProductCategory.LAPTOP == productCategory) {
                final String brand = resultSet.getString(8);
                final String model = resultSet.getString(9);
                final float price = resultSet.getFloat(10);
                productName = String.format("Product name : %s %s - Rs :%.2f", brand, model, price);
            }

            if (ProductCategory.CLOTHES == productCategory) {
                final float price = resultSet.getFloat(10);
                final String clothesType = resultSet.getString(11);
                final String size = resultSet.getString(12);
                final String gender = resultSet.getString(13);
                final String brand = resultSet.getString(14);
                productName = String.format("%s brand :%s size : %s gender: %s - Rs :%.2f ", clothesType, brand, size, gender, price);
            }
            final Address address = new Address(userId, doorNumber, street, city, state, pinCode, country);
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
    public List<Address> getAddresses(final int userId) {
        final List<Address> addresses = new ArrayList<>();

        try (final PreparedStatement preparedStatement = connection
                .prepareStatement("select id, door_number, street, city, state, country, pin_code from address where user_id =?")) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, userId);
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final int id = resultSet.getInt(1);
                final String doorNumber = resultSet.getString(2);
                final String street = resultSet.getString(3);
                final String city = resultSet.getString(4);
                final String state = resultSet.getString(5);
                final String country = resultSet.getString(6);
                final int pinCode = resultSet.getInt(7);
                final Address address = new Address(userId, doorNumber, street, city, state, pinCode, country);

                address.setId(id);

                addresses.add(address);
            }
        } catch (SQLException exception) {
            throw new UserNotFoundException(exception.getMessage());
        }

        return addresses;
    }

    /**
     * <p>
     * Adds the address of the user.
     * </p>
     *
     * @param userId  Refers the id of the user.
     * @param address Refers the {@link Address} to be added.
     */
    @Override
    public void addAddress(final int userId, final Address address) {
        try (final PreparedStatement preparedStatement = connection
                .prepareStatement("insert into address(user_id, door_number, street, city, state, country, pin_code) values (?,?,?,?,?,?,?)")) {
            connection.setAutoCommit(false);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, address.getDoorNumber());
            preparedStatement.setString(3, address.getStreet());
            preparedStatement.setString(4, address.getCity());
            preparedStatement.setString(5, address.getState());
            preparedStatement.setString(6, address.getCountry());
            preparedStatement.setInt(7, address.getPinCode());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException exception) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RollbackFailedException(e.getMessage());
            }
            throw new UpdateActionFailedException(exception.getMessage());
        }
    }
}
