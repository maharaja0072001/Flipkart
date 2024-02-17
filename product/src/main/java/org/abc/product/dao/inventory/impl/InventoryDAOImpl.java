package org.abc.product.dao.inventory.impl;

import org.abc.product.ProductCategory;
import org.abc.product.dao.inventory.InventoryDAO;
import org.abc.dbconnection.connection.DBConnection;
import org.abc.product.exceptions.ItemAdditionFailedException;
import org.abc.product.exceptions.ItemNotFoundException;
import org.abc.product.exceptions.ItemRemovalFailedException;
import org.abc.product.model.product.Product;
import org.abc.product.model.product.Clothes;
import org.abc.product.model.product.Laptop;
import org.abc.product.model.product.Mobile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Stores all the products in the database.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class InventoryDAOImpl implements InventoryDAO {

    private static InventoryDAOImpl inventoryDAO;
    private final Connection connection;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of the InventoryDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private InventoryDAOImpl() {
        logger = LogManager.getLogger(InventoryDAOImpl.class);
        connection = DBConnection.getConnection();
    }

    /**
     * <p>
     * Creates a single object of InventoryDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of InventoryDAOImpl Class.
     */
    public static InventoryDAOImpl getInstance() {
        return Objects.isNull(inventoryDAO) ? inventoryDAO = new InventoryDAOImpl() : inventoryDAO;
    }

    /**
     * <p>
     * Adds the given products to the inventory.
     * </p>
     *
     * @param products Refers the {@link Product} to be added.
     */
    @Override
    public void addItem(final List<Product> products) {
        int productId;
        final HashSet<Product> allProducts = new HashSet<>();

        allProducts.addAll(getMobileItems());
        allProducts.addAll(getLaptopItems());
        allProducts.addAll(getClothesItems());

        for (final Product product : products) {
            if (allProducts.contains(product)) {
                continue;
            }

            try (PreparedStatement preparedStatement = connection
                    .prepareStatement("insert into product (product_category_id, price, quantity) values(?,?,?) returning id")) {
                preparedStatement.setInt(1, product.getProductCategory().getId());
                preparedStatement.setFloat(2, product.getPrice());
                preparedStatement.setFloat(3, product.getQuantity());

                final ResultSet resultSet = preparedStatement.executeQuery();

                resultSet.next();
                productId = resultSet.getInt(1) ;
            } catch (SQLException exception) {

                throw new ItemAdditionFailedException(exception.getMessage());
            }
            final String query = switch (product.getProductCategory()) {
                case MOBILE, LAPTOP -> "insert into electronics_inventory(product_id, brand, model) values(?, ?, ?)";
                case CLOTHES -> "insert into clothes_inventory(product_id, brand, clothes_type, gender, size) values(?, ?, ?, ?, ?)";
            };

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, productId);
                preparedStatement.setString(2, product.getBrandName());

                switch (product.getProductCategory()) {
                    case MOBILE -> preparedStatement.setString(3, ((Mobile) product).getModel());
                    case LAPTOP -> preparedStatement.setString(3, ((Laptop) product).getModel());
                    case CLOTHES -> {
                        preparedStatement.setString(3, ((Clothes) product).getClothesType());
                        preparedStatement.setString(4, ((Clothes) product).getGender());
                        preparedStatement.setString(5, ((Clothes) product).getSize());
                    }
                }
                preparedStatement.executeUpdate();
                allProducts.add(product);
            } catch (final SQLException exception) {
                logger.warn(String.format("Product - %s -Item can't be added to the inventory", product));
                throw new ItemAdditionFailedException(exception.getMessage());
            }
        }
    }

    /**
     * <p>
     * Removes the given item from the inventory.
     * </p>
     *
     * @param productId Refers the id of the {@link Product} to be removed.
     */
    @Override
    public void removeItem(final int productId) {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("delete from product where id = ?")) {
            preparedStatement.setInt(1, productId);
            preparedStatement.executeUpdate();
            logger.info(String.format("Product Id :%d - Item removed from the inventory", productId));
        } catch (final SQLException exception) {
            logger.info(String.format("Product Id :%d - Item can't be removed from the inventory", productId));
            throw new ItemRemovalFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @return all the {@link Product} from the inventory.
     */
    @Override
    public List<? extends Product> getItemsByCategory(final ProductCategory productCategory) {
        return switch (productCategory) {
            case MOBILE -> getMobileItems();
            case LAPTOP -> getLaptopItems();
            case CLOTHES -> getClothesItems();
        };
    }

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @param productCategory Refers the product category.
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @return all the {@link Product} from the inventory.
     */
    @Override
    public List<? extends Product> getItemsByCategory(final ProductCategory productCategory, final int page, final int limit) {
        return switch (productCategory) {
            case MOBILE -> getMobileItems(page, limit);
            case LAPTOP -> getLaptopItems(page, limit);
            case CLOTHES -> getClothesItems(page, limit);
        };
    }

    /**
     * <p>
     * Gets all the mobiles from the inventory and returns it.
     * </p>
     *
     * @param page Refers the page number.
     * @param limit Refers the limit of data to show.
     * @return all the {@link Mobile}.
     */
    private List<Mobile> getMobileItems(final int page, final int limit) {
        final int offset = (page - 1) * limit;

        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, e.brand, e.model, p.price,",
                "p.quantity from electronics_inventory e join product p on p.id = e.product_id",
                "where p.product_category_id=? order by p.id offset ? limit ?"))) {
            preparedStatement.setInt(1, ProductCategory.MOBILE.getId());
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

           return getMobilesFromResultSet(preparedStatement.executeQuery()) ;
        } catch (final SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the mobiles from the inventory and returns it.
     * </p>
     *
     * @return all the {@link Product} in the mobile inventory.
     */
    private List<Mobile> getMobileItems() {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, e.brand, e.model, p.price,",
                        "p.quantity from electronics_inventory e join product p on p.id = e.product_id",
                        "where p.product_category_id=?"))) {
            preparedStatement.setInt(1, ProductCategory.MOBILE.getId());

            return getMobilesFromResultSet(preparedStatement.executeQuery()) ;
        } catch (final SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the mobiles from provided Result set and returns it.
     * </p>
     *
     * @return all the {@link Mobile}.
     */
    private List<Mobile> getMobilesFromResultSet(final ResultSet resultSet) throws SQLException {
        final List<Mobile> mobileCollection = new ArrayList<>();

        while (resultSet.next()) {
            final int productId = resultSet.getInt(1);
            final String brand = resultSet.getString(2);
            final String model = resultSet.getString(3);
            final float price = resultSet.getFloat(4);
            final int quantity = resultSet.getInt(5);
            final Mobile mobile = new Mobile(brand, model, price, quantity);

            mobile.setId(productId);
            mobileCollection.add(mobile);
        }

        return mobileCollection;
    }

    /**
     * <p>
     * Gets all the laptops from the inventory and returns it.
     * </p>
     *
     * @return all the {@link Laptop}.
     */
    private List<Laptop> getLaptopItems() {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, e.brand, e.model, p.price,",
                "p.quantity  from electronics_inventory e join product p on p.id = e.product_id where",
                "p.product_category_id=?"))) {
            preparedStatement.setInt(1, ProductCategory.LAPTOP.getId());

            return getLaptopsFromResultSet(preparedStatement.executeQuery());
        } catch (final SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the laptops from the inventory and returns it.
     * </p>
     *
     * @param limit Refers the limit of data to show.
     * @param page Refers the page number.
     * @return all the {@link Laptop}.
     */
    private List<Laptop> getLaptopItems(final int page, final int limit) {
        final int offset = (page - 1) * limit;

        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, e.brand, e.model, p.price,",
                        "p.quantity  from electronics_inventory e join product p on p.id = e.product_id where",
                        "p.product_category_id=? order by p.id offset ? limit ?"))) {
            preparedStatement.setInt(1, ProductCategory.LAPTOP.getId());
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

            return  getLaptopsFromResultSet(preparedStatement.executeQuery());
        } catch (final SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the laptops from the provided result set and returns it.
     * </p>
     *
     * @param resultSet Refers the result set.
     * @return all the {@link Laptop}.
     */
    private List<Laptop> getLaptopsFromResultSet(final ResultSet resultSet) throws SQLException {
        final List<Laptop> laptopCollection = new ArrayList<>();

        while (resultSet.next()) {
            final int product_id = resultSet.getInt(1);
            final String brand = resultSet.getString(2);
            final String model = resultSet.getString(3);
            final float price = resultSet.getFloat(4);
            final int quantity = resultSet.getInt(5);
            final Laptop laptop = new Laptop(brand, model, price, quantity);

            laptop.setId(product_id);
            laptopCollection.add(laptop);
        }

        return laptopCollection;
    }

    /**
     * <p>
     * Gets all the clothes from the inventory and returns it.
     * </p>
     *
     * @return all the {@link Clothes}.
     */
    private List<Clothes> getClothesItems() {
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, c.clothes_type ,c.brand,",
                "c.gender, c.size, p.price,p.quantity  from clothes_inventory c join product p",
                "on p.id = c.product_id where p.product_category_id =?"))) {
            preparedStatement.setInt(1, ProductCategory.CLOTHES.getId());

            return getClothesFromResultSet(preparedStatement.executeQuery());
        } catch (final SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the clothes from the inventory and returns it.
     * </p>
     *
     * @param limit Refers the limit of data to show.
     * @param page Refers the page number.
     * @return all the {@link Clothes} in the clothes inventory.
     */
    private List<Clothes> getClothesItems(final int page, final int limit) {
        final int offset = (page - 1) * limit;

        try (PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, c.clothes_type ,c.brand,",
                        "c.gender, c.size, p.price,p.quantity  from clothes_inventory c join product p",
                        "on p.id = c.product_id where p.product_category_id =? order by p.id offset ? limit ?"))) {
            preparedStatement.setInt(1, ProductCategory.CLOTHES.getId());
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

            return getClothesFromResultSet(preparedStatement.executeQuery());
        } catch (final SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the clothes from the provided result set and returns it.
     * </p>
     *
     * @param resultSet Refers the result set.
     * @return all the {@link Clothes}.
     */
    private List<Clothes> getClothesFromResultSet(final ResultSet resultSet) throws SQLException {
        final List<Clothes> clothesCollection = new ArrayList<>();

        while (resultSet.next()) {
            final int productId = resultSet.getInt(1);
            final String clothesType = resultSet.getString(2);
            final String brand = resultSet.getString(3);
            final String gender = resultSet.getString(4);
            final String size = resultSet.getString(5);
            final float price = resultSet.getFloat(6);
            final int quantity = resultSet.getInt(7);
            final Clothes clothes = new Clothes(clothesType, gender, size, price, brand, quantity);

            clothes.setId(productId);
            clothesCollection.add(clothes);
        }

        return clothesCollection;
    }
}