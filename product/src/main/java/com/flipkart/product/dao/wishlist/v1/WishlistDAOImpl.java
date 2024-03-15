package com.flipkart.product.dao.wishlist.v1;

import com.flipkart.authentication.exceptions.UserCheckFailedException;
import com.flipkart.database.connection.DatabaseConnection;
import com.flipkart.product.dao.wishlist.WishlistDAO;
import com.flipkart.product.model.wishlist.Wishlist;
import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.exceptions.ItemAdditionFailedException;
import com.flipkart.product.exceptions.ItemNotFoundException;
import com.flipkart.product.exceptions.ItemRemovalFailedException;
import com.flipkart.product.model.product.Clothes;
import com.flipkart.product.model.product.Laptop;
import com.flipkart.product.model.product.Mobile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * <p>
 * Provides DAO for wishlist.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class WishlistDAOImpl implements WishlistDAO {

    private final Connection connection;

    /**
     * <p>
     * Default constructor of the WishlistDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private WishlistDAOImpl() {
        connection = DatabaseConnection.get();
    }

    /**
     * <p>
     * Creates a single instance of WishlistDAOImpl class.
     * </p>
     */
    private static class InstanceHolder {

        private static final WishlistDAOImpl WishlistDAOImpl = new WishlistDAOImpl();
    }

    /**
     * <p>
     * Gets a single object of WishlistDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of WishlistDAOImpl Class.
     */
    public static WishlistDAO getInstance() {
        return InstanceHolder.WishlistDAOImpl;
    }

    /**
     * <p>
     * Adds the product to the wishlist of the specified user.
     * </p>
     *
     * @param productId Refers the id of the product to be added
     * @param userId Refers the user id.
     * @return the wishlist of the user.
     */
    @Override
    public boolean addProduct(final int productId, final int userId) {
        try (final PreparedStatement preparedStatement = connection
                .prepareStatement("insert into wishlist (user_id , product_id) values(?,?)")) {
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
     * Removes the product from the wishlist of the specified user.
     * * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId Refers the user id.
     * @return true if item removed
     */
    @Override
    public boolean removeProduct(final int productId, final int userId) {
        try (final PreparedStatement preparedStatement = connection
                .prepareStatement("delete from wishlist where user_id =? and product_id =?")) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new ItemRemovalFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the wishlist of the specified user id and returns it.
     * </p>
     *
     * @param page Refers the page number.
     * @param userId Refers the id of the user who owns the cart.
     * @return Optional of {@link Wishlist} of the user.
     */
    @Override
    public Optional<Wishlist> getWishlist(final int userId, final int page) {
        final int limit = 5;
        final int offset = (page - 1) * limit;

        try (final PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select w.product_id,",
                        "p.product_category_id, e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand ,p.quantity",
                        "from wishlist w join product p on w.product_id=p.id left join electronics_inventory e on",
                        "w.product_id = e.product_id left join clothes_inventory c on p.id=c.product_id where",
                        "w.user_id = ? order by w.id offset ? limit ?"))) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

            return getWishlistFromResultSet(preparedStatement.executeQuery());
        } catch (SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the wishlist from the provided Result set and returns it.
     * </p>
     *
     * @param resultSet Refers the result set.
     * @return the wishlist of the user.
     */
    private Optional<Wishlist> getWishlistFromResultSet(final ResultSet resultSet) throws SQLException {
        if (!resultSet.isBeforeFirst()) {
            return Optional.empty();
        }
        final Wishlist wishlist = new Wishlist();

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
                wishlist.addProduct(mobile);
            }

            if (ProductCategory.LAPTOP == productCategory) {
                final String brand = resultSet.getString(3);
                final String model = resultSet.getString(4);
                final float price = resultSet.getFloat(5);
                final int quantity = resultSet.getInt(10);
                final Laptop laptop = new Laptop(brand, model, price, quantity);

                laptop.setId(productId);
                wishlist.addProduct(laptop);
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
                wishlist.addProduct(clothes);
            }
        }

        return Optional.of(wishlist);
    }

    /**
     * <p>
     * Checks whether the user exists or not.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @param productId Refers the product id.
     * @return true if the product already exists or false otherwise.
     */
    @Override
    public boolean isProductExist(final int productId, final int userId) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("select count(id) from wishlist where user_id = ? and  product_id = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            final ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();

            return resultSet.getInt(1) > 0;
        } catch (SQLException exception) {
            throw new UserCheckFailedException(exception.getMessage());
        }
    }
}
