package com.flipkart.product.dao.order.v2;

import jakarta.persistence.Query;

import com.flipkart.product.dao.order.OrderDAO;
import com.flipkart.authentication.exceptions.UpdateActionFailedException;
import com.flipkart.authentication.exceptions.UserNotFoundException;
import com.flipkart.authentication.model.Address;
import com.flipkart.product.model.OrderStatus;
import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.exceptions.OrderAdditionFailedException;
import com.flipkart.product.exceptions.OrderNotFoundException;
import com.flipkart.product.exceptions.OrderRemovalFailedException;
import com.flipkart.product.model.order.Order;
import com.flipkart.product.model.product.Product;

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
public class OrderDAOImpl implements OrderDAO {

    private final SessionFactory sessionFactory;

    /**
     * <p>
     * Default constructor of the OrderDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private OrderDAOImpl() {
        sessionFactory = new Configuration().addAnnotatedClass(Order.class).addAnnotatedClass(Product.class).buildSessionFactory();
    }

    /**
     * <p>
     * Creates a single instance of OrderDAOImpl2 class.
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
    public static OrderDAOImpl getInstance() {
        return InstanceHolder.ORDER_DAO;
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
        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(order);
            session.getTransaction().commit();
        } catch (Exception exception) {
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
     * @return  all the {@link Order} of the user.
     */
    @Override
    public List<Order> getOrders(final int userId, final int page) {
        final int limit = 5;

        try (final Session session = sessionFactory.openSession()) {
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
        Session session = null;

        try {
            session = sessionFactory.openSession();
            final Query query = (Query) session.createNativeMutationQuery("update orders set orderStatusId=:orderStatusId where id=:id");

            session.beginTransaction();
            query.setParameter("orderStatusId", OrderStatus.CANCELLED.getId());
            query.setParameter("id", order.getId());
            query.executeUpdate();
            updateQuantity(order.getProductId(), order.getQuantity());
            session.getTransaction().commit();
        } catch (Exception exception) {
            if (Objects.nonNull(session)) {
                session.getTransaction().rollback();
            }
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
    public void addAddress(final int userId, final Address address) {
        try (final Session session = sessionFactory.openSession()) {
            final Query query = (Query) session.createNativeMutationQuery(String.join(" ", "insert into address(user_id,",
                   "door_number, street, city, state, country, pin_code) values (?,?,?,?,?,?,?)" ));

            session.beginTransaction();
            query.setParameter(1, userId);
            query.setParameter(2, address.getDoorNumber());
            query.setParameter(3, address.getStreet());
            query.setParameter(4, address.getCity());
            query.setParameter(5, address.getState());
            query.setParameter(6, address.getCountry());
            query.setParameter(7, address.getPinCode());
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception exception) {
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
    public List<Address> getAddresses(final int userId) {
        try (final Session session = sessionFactory.openSession()) {
            final String sqlQuery = """
            select a.id, a.door_number, a.street, a.city, a.state, a.country,
            a.pin_code from address a join users on users.id=a.user_id where a.user_id =?""";

            return session.createNativeQuery(sqlQuery, Address.class)
                    .setParameter(1, userId).getResultList();
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
        try (final Session session = sessionFactory.openSession()) {
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
            final int orderStatusId = (int) row[5];
            final ProductCategory productCategory = ProductCategory.valueOf((int) row[6]);
            final String doorNumber = (String) row[14];
            final String street = (String) row[15];
            final String city = (String) row[16];
            final String state = (String) row[17];
            final String country = (String) row[18];
            final int pinCode = (int) row[19];

            if (ProductCategory.MOBILE == productCategory || ProductCategory.LAPTOP == productCategory) {
                final String brand = (String) row[7];
                final String model = (String) row[8];
                final double price = (double) row[9];
                productName = String.format("Product name : %s %s - Rs :%.2f", brand, model, price);
            }

            if (ProductCategory.CLOTHES == productCategory) {
                final double price = (double) row[9];
                final String clothesType = (String) row[10];
                final String size = (String) row[11];
                final String gender = (String) row[12];
                final String brand = (String) row[13];
                productName = String.format("%s brand :%s size : %s gender: %s - Rs :%.2f ", clothesType, brand, size, gender, price);
            }

            final Address address = new Address(userId, doorNumber, street, city, state, pinCode, country);
            final Order order = new Order.OrderBuilder(userId).setId(orderId)
                    .setProductName(productName).setTotalAmount((float) totalAmount).setQuantity(quantity).setAddress(address)
                    .setOrderStatusId(orderStatusId).setPaymentModeId(paymentModeId).setProductId(productId).build();

            orders.add(order);
        }

        return orders;
    }
}