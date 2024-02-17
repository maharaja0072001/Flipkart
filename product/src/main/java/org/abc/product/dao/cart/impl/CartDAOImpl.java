package org.abc.product.dao.cart.impl;

import org.abc.product.dao.cart.CartDAO;
import org.abc.dbconnection.connection.DBConnection;
import org.abc.product.exceptions.ItemRemovalFailedException;
import org.abc.product.exceptions.ItemNotFoundException;
import org.abc.product.model.cart.Cart;
import org.abc.product.model.product.Clothes;
import org.abc.product.model.product.Laptop;
import org.abc.product.model.product.Mobile;
import org.abc.product.ProductCategory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * <p>
 * Stores all the cart details in the database.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class CartDAOImpl implements CartDAO {

    private static CartDAOImpl cartDAO;
    private final Connection connection;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of the CartDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private CartDAOImpl() {
        connection = DBConnection.getConnection();
        logger = LogManager.getLogger(CartDAOImpl.class);
    }

    /**
     * <p>
     * Creates a single object of CartDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of CartDAOImpl Class.
     */
    public static CartDAO getInstance() {
        return Objects.isNull(cartDAO) ? cartDAO = new CartDAOImpl() : cartDAO;
    }

    /**
     * <p>
     * Adds the product to the cart in the database.
     * </p>
     *
     * @param productId Refers the id of the product to be added.
     * @param userId Refers the user id.
     * @return true is the product is added to the cart.
     */
    @Override
    public boolean addItem(final int productId, final int userId) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("insert into cart (user_id , product_id) values(?,?)")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            final int updatedRows = preparedStatement.executeUpdate();

            return  updatedRows > 0;
        } catch (final SQLException exception) {
            logger.info(String.format("User id :%d Product Id :%d - Item is already in the cart", userId, productId));

            return false;
        }
    }

    /**
     * <p>
     * Removes the product from the cart in the database.
     * </p>
     *
     * @param productId Refers the id of the product to be removed from the cart.
     * @param userId Refers the user id.
     */
    @Override
    public void removeItem(final int productId, final int userId) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("delete from cart where user_id =? and product_id =?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();
            logger.info(String.format("User id :%d Product Id :%d - Item removed from the cart", userId, productId));

        } catch (final SQLException exception) {
            logger.info(String.format("User id :%d Product Id :%d - Item can't be removed", userId, productId));
            throw new ItemRemovalFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the cart to the user from the database.
     * </p>
     *
     * @param userId Refers the user id.
     * @return {@link Cart} of the user.
     */
    @Override
    public Cart getCart(final int userId) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select cart.product_id, p.product_category_id,",
                        "e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand, p.quantity from cart join product p",
                        "on cart.product_id=p.id left join electronics_inventory e on cart.product_id = e.product_id",
                        "left join clothes_inventory c on p.id=c.product_id where cart.user_id = ?"))) {
            preparedStatement.setInt(1, userId);

            return getCartFromResultSet(preparedStatement.executeQuery());
        } catch (final SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the cart to the user from the database.
     * </p>
     *
     * @param userId Refers the user id.
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @return {@link Cart} of the user.
     */
    @Override
    public Cart getCart(final int userId, final int page, final int limit) {
        final int offset = (page - 1) * limit;

        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select cart.product_id, p.product_category_id,",
                "e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand, p.quantity from cart join product p",
                "on cart.product_id=p.id left join electronics_inventory e on cart.product_id = e.product_id",
                "left join clothes_inventory c on p.id=c.product_id where cart.user_id = ? order by cart.id offset ? limit ?"))) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

            return getCartFromResultSet(preparedStatement.executeQuery());
        } catch (final SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the cart from the provided Resultset.
     * </p>
     *
     * @param resultSet Refers the resultset.
     * @return {@link Cart} of the user.
     */
    private Cart getCartFromResultSet(final ResultSet resultSet) throws SQLException {
        final Cart cart = new Cart();

        while (resultSet.next()) {
            final int productId = resultSet.getInt(1);
            final ProductCategory productCategory = ProductCategory.valueOf(resultSet.getInt(2));

            if (ProductCategory.MOBILE == productCategory) {
                final String brand = resultSet.getString(3);
                final String model = resultSet.getString(4);
                final float price = resultSet.getFloat(5);
                final int quantity = resultSet.getInt(10);
                final Mobile mobile = new Mobile(brand, model, price, quantity);

                mobile.setId(productId);
                cart.addItem(mobile);
            }

            if (ProductCategory.LAPTOP == productCategory) {
                final String brand = resultSet.getString(3);
                final String model = resultSet.getString(4);
                final float price = resultSet.getFloat(5);
                final int quantity = resultSet.getInt(10);
                final Laptop laptop = new Laptop(brand, model, price, quantity);

                laptop.setId(productId);
                cart.addItem(laptop);
            }

            if (ProductCategory.CLOTHES == productCategory) {
                final String brand = resultSet.getString(9);
                final String clothesType = resultSet.getString(6);
                final String size = resultSet.getString(7);
                final String gender = resultSet.getString(8);
                final float price = resultSet.getFloat(5);
                final int quantity = resultSet.getInt(10);
                final Clothes clothes = new Clothes(clothesType, gender, size, price, brand, quantity);

                clothes.setId(productId);
                cart.addItem(clothes);
            }
        }

        return cart;
    }
}
