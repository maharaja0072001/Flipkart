package org.abc.product.dao.order.impl;

import jakarta.persistence.Query;

import org.abc.authentication.exceptions.UpdateActionFailedException;
import org.abc.authentication.exceptions.UserNotFoundException;
import org.abc.product.OrderStatus;
import org.abc.product.ProductCategory;
import org.abc.product.dao.order.OrderDAO;
import org.abc.product.exceptions.OrderAdditionFailedException;
import org.abc.product.exceptions.OrderNotFoundException;
import org.abc.product.exceptions.OrderRemovalFailedException;
import org.abc.product.model.order.Order;
import org.abc.product.model.product.Product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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
public class OrderDAOImpl2 implements OrderDAO {

    private static OrderDAOImpl2 orderDAO;
    private final SessionFactory sessionFactory;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of the OrderDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private OrderDAOImpl2() {
        sessionFactory = new Configuration().addAnnotatedClass(Order.class)
                .addAnnotatedClass(Product.class).buildSessionFactory();
        logger = LogManager.getLogger(OrderDAOImpl2.class);
    }

    /**
     * <p>
     * Creates a single object of OrderDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of OrderDAOImpl Class.
     */
    public static OrderDAOImpl2 getInstance() {
        return Objects.isNull(orderDAO) ? orderDAO = new OrderDAOImpl2() : orderDAO;
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
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(order);
            session.getTransaction().commit();
            logger.info(String.format("User id :%d Product Id :%d - Order placed successfully", userId, order.getProductId()));
        } catch (Exception exception) {
            logger.warn(String.format("User id :%d Product Id :%d - Order not placed", userId, order.getProductId()));
            throw new OrderAdditionFailedException(exception.getMessage());
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
        try (Session session = sessionFactory.openSession()) {
            final String sqlQuery = String.join(" ", "select o.id, o.product_id,",
                    "o.payment_mode_id,o.quantity,o.total_amount, o.address,o.order_status_id, p.product_category_id,",
                    "e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand from orders o join product p",
                    "on o.product_id=p.id  left join electronics_inventory e on o.product_id = e.product_id",
                    "left join clothes_inventory c on o.product_id=c.product_id where o.user_id=?");
            final List<Object[]> resultList = session.createNativeQuery(sqlQuery, Object[].class).setParameter(1, userId)
                    .setFirstResult(page).setMaxResults(limit).getResultList();

            return getOrdersFromResultList(resultList, userId);
        } catch (Exception exception) {
            throw new OrderNotFoundException(exception.getMessage());
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
        try (Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery("update orders set orderStatusId=:orderStatusId where id=:id");

            session.beginTransaction();
            query.setParameter("orderStatusId", OrderStatus.CANCELLED.getId());
            query.setParameter("id", order.getId());
            query.executeUpdate();
            updateQuantity(order.getProductId(), order.getQuantity());
            session.getTransaction().commit();
            logger.info(String.format("User id :%d Product Id :%d - Order cancelled successfully.", order.getUserId(), order.getProductId()));
        } catch (Exception exception) {
            logger.warn(String.format("User id :%d Product Id :%d - Order can't be cancelled.", order.getUserId(), order.getProductId()));
            throw new OrderRemovalFailedException(exception.getMessage());
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
    @Override
    public void addAddress(final int userId, final String address) {
        try (Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery("insert into address(user_id, address) values (?,?)");

            session.beginTransaction();
            query.setParameter(1, userId);
            query.setParameter(2, address);
            query.executeUpdate();
            session.getTransaction().commit();
            logger.info(String.format("User id :%d - Address added successfully.", userId));
        } catch (Exception exception) {
            logger.warn(String.format("User id :%d - Address not added", userId));
            throw new UpdateActionFailedException(exception.getMessage());
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
    @Override
    public List<String> getAllAddresses(final int userId) {
        try (Session session = sessionFactory.openSession()) {
            final String sqlQuery = "select address from address join users on users.id=address.user_id where user_id =?";

            return session.createNativeQuery(sqlQuery, String.class)
                    .setParameter(1, userId).getResultList();
        } catch (Exception exception) {
            throw new UserNotFoundException(exception.getMessage());
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
        try (Session session = sessionFactory.openSession()) {
            final String sqlQuery = String.join(" ", "select o.id, o.product_id,",
                    "o.payment_mode_id,o.quantity,o.total_amount, o.address,o.order_status_id, p.product_category_id,",
                    "e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand from orders o join product p",
                    "on o.product_id=p.id  left join electronics_inventory e on o.product_id = e.product_id",
                    "left join clothes_inventory c on o.product_id=c.product_id where o.user_id=?");
            final List<Object[]> resultList = session.createNativeQuery(sqlQuery, Object[].class)
                    .setParameter(1, userId).getResultList();

            return getOrdersFromResultList(resultList, userId);
        } catch (Exception exception) {
            throw new UserNotFoundException(exception.getMessage());
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
        try (Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery("update product p set p.quantity=quantity -:quantity where p.id =:id");

            session.beginTransaction();
            query.setParameter("quantity", quantity);
            query.setParameter("id", productId);
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception exception) {
            throw new UpdateActionFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the orders from the provided ResultList.
     * </p>
     *
     * @param resultList Refers the result set.
     * @param userId Refers the user id.
     * @return  all the {@link Order} of the user.
     */
    private List<Order> getOrdersFromResultList(final List<Object[]> resultList, final int userId) {
        final List<Order> orders = new ArrayList<>();

        for (final Object[] row : resultList) {
            final int orderId = (int) row[0];
            final int productId = (int) row[1];
            String productName = null;
            final int paymentModeId = (int) row[2];
            final int quantity = (int) row[3];
            final double totalAmount = (double) row[4];
            final String address = (String) row[5];
            final int orderStatusId = (int) row[6];
            final ProductCategory productCategory = ProductCategory.valueOf((int) row[7]);

            if (ProductCategory.MOBILE == productCategory || ProductCategory.LAPTOP == productCategory) {
                final String brand = (String) row[8];
                final String model = (String) row[9];
                final double price = (double) row[10];
                productName = String.format("Product name : %s %s - Rs :%.2f", brand, model, price);
            }

            if (ProductCategory.CLOTHES == productCategory) {
                final double price = (double) row[10];
                final String clothesType = (String) row[11];
                final String size = (String) row[12];
                final String gender = (String) row[13];
                final String brand = (String) row[14];
                productName = String.format("%s brand :%s size : %s gender: %s - Rs :%.2f ", clothesType, brand, size, gender, price);
            }
            final Order order = new Order.OrderBuilder(userId).setId(orderId)
                    .setProductName(productName).setTotalAmount((float) totalAmount).setQuantity(quantity).setAddress(address)
                    .setOrderStatusId(orderStatusId).setPaymentModeId(paymentModeId).setProductId(productId).build();

            orders.add(order);
        }

        return orders;
    }
}