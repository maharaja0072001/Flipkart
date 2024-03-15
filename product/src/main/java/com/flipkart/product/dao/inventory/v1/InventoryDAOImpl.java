package com.flipkart.product.dao.inventory.v1;

import com.flipkart.authentication.exceptions.UserCheckFailedException;
import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.exceptions.ItemAdditionFailedException;
import com.flipkart.product.exceptions.ItemNotFoundException;
import com.flipkart.product.exceptions.ItemRemovalFailedException;
import com.flipkart.product.exceptions.RollbackFailedException;
import com.flipkart.product.model.product.Clothes;
import com.flipkart.product.model.product.Laptop;
import com.flipkart.product.model.product.Mobile;
import com.flipkart.product.model.product.Product;
import com.flipkart.database.connection.DatabaseConnection;
import com.flipkart.product.dao.inventory.InventoryDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * <p>
 * Stores all the products in the database.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class InventoryDAOImpl implements InventoryDAO {

    private final Connection connection;

    /**
     * <p>
     * Default constructor of the InventoryDAOImpl class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private InventoryDAOImpl() {
        connection = DatabaseConnection.get();
    }
    /**
     * <p>
     * Creates a single instance of InventoryDAOImpl class.
     * </p>
     */
    private static class InstanceHolder {

        private static final InventoryDAOImpl INVENTORY_DAO = new InventoryDAOImpl();
    }

    /**
     * <p>
     * Gets a single object of InventoryDAOImpl Class and returns it.
     * </p>
     *
     * @return returns the single instance of InventoryDAOImpl Class.
     */
    public static InventoryDAOImpl getInstance() {
        return InstanceHolder.INVENTORY_DAO;
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
        final HashSet<Product> allProducts = new HashSet<>();

        allProducts.addAll(getMobileItems());
        allProducts.addAll(getLaptopItems());
        allProducts.addAll(getClothesItems());

        for (final Product product : products) {
            if (allProducts.contains(product)) {
                continue;
            }

            final String query = switch (product.getProductCategory()) {
                case MOBILE, LAPTOP -> "insert into electronics_inventory(product_id, brand, model) values(?, ?, ?)";
                case CLOTHES -> "insert into clothes_inventory(product_id, brand, clothes_type, gender, size) values(?, ?, ?, ?, ?)";
            };

            try (final PreparedStatement insertInProduct = connection
                    .prepareStatement("insert into product (product_category_id, price, quantity) values(?,?,?) returning id");
                 final PreparedStatement insertInInventory = connection.prepareStatement(query)) {
                connection.setAutoCommit(false);
                insertInProduct.setInt(1, product.getProductCategory().getId());
                insertInProduct.setFloat(2, product.getPrice());
                insertInProduct.setFloat(3, product.getQuantity());
                final ResultSet resultSet = insertInProduct.executeQuery();

                resultSet.next();
                final int productId = resultSet.getInt(1) ;

                insertInInventory.setInt(1, productId);
                insertInInventory.setString(2, product.getBrandName());

                switch (product.getProductCategory()) {
                    case MOBILE -> insertInInventory.setString(3, ((Mobile) product).getModel());
                    case LAPTOP -> insertInInventory.setString(3, ((Laptop) product).getModel());
                    case CLOTHES -> {
                        insertInInventory.setString(3, ((Clothes) product).getClothesType());
                        insertInInventory.setString(4, ((Clothes) product).getGender());
                        insertInInventory.setString(5, ((Clothes) product).getSize());
                    }
                }
                insertInInventory.executeUpdate();
                allProducts.add(product);
                connection.commit();

            } catch (SQLException exception) {
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    throw new RollbackFailedException(e.getMessage());
                }

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
     * @return true if item  removed
     */
    @Override
    public boolean removeItem(final int productId) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("delete from product where id = ?")) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, productId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new ItemRemovalFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the products from the inventory based on the category and returns it.
     * </p>
     *
     * @param productCategory Refers the product category.
     * @param page Refers the page number.
     * @return all the {@link Product} from the inventory.
     */
    @Override
    public List<? extends Product> getProductByCategory(final ProductCategory productCategory, final int page) {

        return switch (productCategory) {
            case MOBILE -> getMobileItems(page);
            case LAPTOP -> getLaptopItems(page);
            case CLOTHES -> getClothesItems(page);
        };
    }

    /**
     * <p>
     * Gets all the mobiles from the inventory and returns it.
     * </p>
     *
     * @param page Refers the page number.
     * @return all the {@link Mobile}.
     */
    private List<Mobile> getMobileItems(final int page) {
        final int limit = 5;
        final int offset = (page - 1) * limit;

        try (final PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, e.brand, e.model, p.price,",
                "p.quantity from electronics_inventory e join product p on p.id = e.product_id",
                "where p.product_category_id=? order by p.id offset ? limit ?"))) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, ProductCategory.MOBILE.getId());
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

           return getMobilesFromResultSet(preparedStatement.executeQuery()) ;
        } catch (SQLException exception) {
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
        try (final PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, e.brand, e.model, p.price,",
                        "p.quantity from electronics_inventory e join product p on p.id = e.product_id",
                        "where p.product_category_id=?"))) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, ProductCategory.MOBILE.getId());

            return getMobilesFromResultSet(preparedStatement.executeQuery()) ;
        } catch (SQLException exception) {
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
        try (final PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, e.brand, e.model, p.price,",
                "p.quantity  from electronics_inventory e join product p on p.id = e.product_id where",
                "p.product_category_id=?"))) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, ProductCategory.LAPTOP.getId());

            return getLaptopsFromResultSet(preparedStatement.executeQuery());
        } catch (SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the laptops from the inventory and returns it.
     * </p>
     *
     * @param page Refers the page number.
     * @return all the {@link Laptop}.
     */
    private List<Laptop> getLaptopItems(final int page) {
        final int limit = 5;
        final int offset = (page - 1) * limit;

        try (final PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, e.brand, e.model, p.price,",
                        "p.quantity  from electronics_inventory e join product p on p.id = e.product_id where",
                        "p.product_category_id=? order by p.id offset ? limit ?"))) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, ProductCategory.LAPTOP.getId());
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

            return  getLaptopsFromResultSet(preparedStatement.executeQuery());
        } catch (SQLException exception) {
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
        try (final PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, c.clothes_type ,c.brand,",
                "c.gender, c.size, p.price,p.quantity  from clothes_inventory c join product p",
                "on p.id = c.product_id where p.product_category_id =?"))) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, ProductCategory.CLOTHES.getId());

            return getClothesFromResultSet(preparedStatement.executeQuery());
        } catch (SQLException exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the clothes from the inventory and returns it.
     * </p>
     *
     * @param page Refers the page number.
     * @return all the {@link Clothes} in the clothes inventory.
     */
    private List<Clothes> getClothesItems(final int page) {
        final int limit = 5;
        final int offset = (page - 1) * limit;

        try (final PreparedStatement preparedStatement = connection
                .prepareStatement(String.join(" ", "select p.id, c.clothes_type ,c.brand,",
                        "c.gender, c.size, p.price,p.quantity  from clothes_inventory c join product p",
                        "on p.id = c.product_id where p.product_category_id =? order by p.id offset ? limit ?"))) {
            connection.setAutoCommit(true);
            preparedStatement.setInt(1, ProductCategory.CLOTHES.getId());
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);

            return getClothesFromResultSet(preparedStatement.executeQuery());
        } catch (SQLException exception) {
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

    /**
     * <p>
     * Checks whether the product exists or not.
     * </p>
     *
     * @param productId Refers the product id.
     * @return true if the product already exists or false otherwise.
     */
    @Override
    public boolean isProductExist(final int productId) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("select count(id) from product where id = ?")) {
            preparedStatement.setInt(1, productId);
            final ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();

            return resultSet.getInt(1) > 0;
        } catch (SQLException exception) {
            throw new UserCheckFailedException(exception.getMessage());
        }
    }
}