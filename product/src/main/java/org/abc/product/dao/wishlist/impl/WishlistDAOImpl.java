package org.abc.product.dao.wishlist.impl;

import org.abc.product.dao.wishlist.WishlistDAO;
import org.abc.dbconnection.connection.DBConnection;
import org.abc.product.ProductCategory;
import org.abc.product.exceptions.ItemNotFoundException;
import org.abc.product.exceptions.ItemRemovalFailedException;
import org.abc.product.model.wishlist.Wishlist;
import org.abc.product.model.product.Clothes;
import org.abc.product.model.product.Laptop;
import org.abc.product.model.product.Mobile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * <p>
 * Provides DAO for wishlist.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class WishlistDAOImpl implements WishlistDAO {

    private static WishlistDAOImpl wishlistDAO;
    private final Connection connection;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of the WishlistDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private WishlistDAOImpl() {
        connection = DBConnection.getConnection();
        logger = LogManager.getLogger(WishlistDAOImpl.class);
    }

    /**
     * <p>
     * Creates a single object of WishlistDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of WishlistDAOImpl Class.
     */
    public static WishlistDAO getInstance() {
        return Objects.isNull(wishlistDAO) ? wishlistDAO = new WishlistDAOImpl() : wishlistDAO;
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
    public boolean addItem(final int productId, final int userId) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("insert into wishlist (user_id , product_id) values(?,?)")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            final int updatedRows = preparedStatement.executeUpdate();

            return  updatedRows > 0;
        } catch (final SQLException exception) {
            logger.info(String.format("User id :%d Product Id :%d - Item is already in the wishlist", userId, productId));

            return false;
        }
    }

    /**
     * <p>
     * Removes the product from the wishlist of the specified user.
     * * </p>
     *
     * @param productId Refers the id of the product to be removed.
     * @param userId Refers the user id.
     */
    @Override
    public void removeItem(final int productId, final int userId) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("delete from wishlist where user_id =? and product_id =?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();
            logger.info(String.format("User id :%d Product Id :%d - Item removed from the wishlist", userId, productId));
        } catch (final SQLException exception) {
            logger.info(String.format("User id :%d Product Id :%d - Item can't be removed from the wishlist", userId, productId));
            throw new ItemRemovalFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the wishlist of the specified user id and returns it.
     * </p>
     *
     * @param limit Refers the limit of data to show.
     * @param page Refers the page number.
     * @param userId Refers the id of the user who owns the cart.
     * @return the {@link Wishlist} of the user.
     */
    @Override
    public Wishlist getWishlist(final int userId, final int page, final int limit) {
        final int offset = (page - 1) * limit;

        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select w.product_id,",
                        "p.product_category_id, e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand ,p.quantity",
                        "from wishlist w join product p on w.product_id=p.id left join electronics_inventory e on",
                        "w.product_id = e.product_id left join clothes_inventory c on p.id=c.product_id where w.user_id = ? order by wishlist.id offset ? limit ?"))) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

            return getWishlistFromResultSet(preparedStatement.executeQuery());
        } catch (final SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets the wishlist of the specified user id and returns it.
     * </p>
     *
     * @param userId Refers the id of the user who owns the cart.
     * @return the wishlist of the user.
     */
    @Override
    public Wishlist getWishlist(final int userId) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select w.product_id,",
                        "p.product_category_id, e.brand,e.model, p.price,c.clothes_type,c.size,c.gender, c.brand ,p.quantity",
                        "from wishlist w join product p on w.product_id=p.id left join electronics_inventory e on",
                        "w.product_id = e.product_id left join clothes_inventory c on p.id=c.product_id where w.user_id = ?"))) {
            preparedStatement.setInt(1, userId);
            return getWishlistFromResultSet(preparedStatement.executeQuery());
        } catch (final SQLException exception) {
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
    private Wishlist getWishlistFromResultSet(final ResultSet resultSet) throws SQLException {
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
                wishlist.addItem(mobile);
            }

            if (ProductCategory.LAPTOP == productCategory) {
                final String brand = resultSet.getString(3);
                final String model = resultSet.getString(4);
                final float price = resultSet.getFloat(5);
                final int quantity = resultSet.getInt(10);
                final Laptop laptop = new Laptop(brand, model, price, quantity);

                laptop.setId(productId);
                wishlist.addItem(laptop);
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
                wishlist.addItem(clothes);
            }
        }

        return wishlist;
    }
}
