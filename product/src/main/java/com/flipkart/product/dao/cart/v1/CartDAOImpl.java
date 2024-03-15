package com.flipkart.product.dao.cart.v1;

import com.flipkart.authentication.exceptions.UserCheckFailedException;
import com.flipkart.database.connection.DatabaseConnection;
import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.exceptions.ItemNotFoundException;
import com.flipkart.product.model.cart.Cart;
import com.flipkart.product.model.product.Clothes;
import com.flipkart.product.model.product.Laptop;
import com.flipkart.product.model.product.Mobile;
import com.flipkart.product.dao.cart.CartDAO;
import com.flipkart.product.exceptions.ItemAdditionFailedException;
import com.flipkart.product.exceptions.ItemRemovalFailedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * <p>
 * Stores all the cart details in the database.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class CartDAOImpl implements CartDAO {

    private final Connection connection;

    /**
     * <p>
     * Default constructor of the CartDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private CartDAOImpl() {
        connection = DatabaseConnection.get();
    }

    /**
     * <p>
     * Creates a single instance of CartDAOImpl class.
     * </p>
     */
    private static class InstanceHolder {

        private static final CartDAOImpl CART_DAO = new CartDAOImpl();
    }

    /**
     * <p>
     * Gets a single object of CartDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of CartDAOImpl Class.
     */
    public static CartDAO getInstance() {
        return InstanceHolder.CART_DAO;
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
    public boolean addProduct(final int productId, final int userId) {
        try (final PreparedStatement preparedStatement = connection
                .prepareStatement("insert into cart (user_id , product_id) values(?,?)")) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            final int updatedRows = preparedStatement.executeUpdate();

            return  updatedRows > 0;
        } catch (SQLException exception) {
            throw new ItemAdditionFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Checks whether the product exists in cart or not.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @param productId Refers the product id.
     * @return true if the product already exists or false otherwise.
     */
    @Override
    public boolean isProductExist(final int productId, final int userId) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("select count(id) from cart where user_id = ? and  product_id = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            final ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();

            return resultSet.getInt(1) > 0;
        } catch (SQLException exception) {
            throw new UserCheckFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Removes the product from the cart in the database.
     * </p>
     *
     * @param productId Refers the id of the product to be removed from the cart.
     * @param userId Refers the user id.
     * @return true if item removed
     */
    @Override
    public boolean removeProduct(final int productId, final int userId) {
        try (final PreparedStatement preparedStatement = connection
                .prepareStatement("delete from cart where user_id =? and product_id =?")) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);

            return preparedStatement.executeUpdate() > 0;
        } catch (final SQLException exception) {
            throw new ItemRemovalFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the cart to the user from the database.
     * </p>
     *
     * @param userId Refers the user id.
     * @param page Refers the page number.
     * @return Optional of {@link Cart} of the user.
     */
    @Override
    public Optional<Cart> getCart(final int userId, final int page) {
        final int limit = 5;
        final int offset = (page - 1) * limit;

        try (final PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select cart.product_id, p.product_category_id,",
                "e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand, p.quantity from cart join product p",
                "on cart.product_id=p.id left join electronics_inventory e on cart.product_id = e.product_id",
                "left join clothes_inventory c on p.id=c.product_id where cart.user_id = ? order by cart.id offset ? limit ?"))) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

            return getCartFromResultSet(preparedStatement.executeQuery());
        } catch (SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the cart from the provided Resultset.
     * </p>
     *
     * @param resultSet Refers the resultset.
     * @return optional of {@link Cart} of the user.
     */
    private Optional<Cart> getCartFromResultSet(final ResultSet resultSet) throws SQLException {
        if (!resultSet.isBeforeFirst()) {
            return Optional.empty();
        }
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

        return Optional.of(cart);
    }
}
