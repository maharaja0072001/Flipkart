package com.flipkart.product.dao.inventory.v2;

import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.exceptions.ItemNotFoundException;
import com.flipkart.product.model.product.Clothes;
import com.flipkart.product.model.product.Laptop;
import com.flipkart.product.model.product.Mobile;
import com.flipkart.authentication.exceptions.UserCheckFailedException;
import com.flipkart.product.dao.inventory.InventoryDAO;
import com.flipkart.product.exceptions.ItemAdditionFailedException;
import com.flipkart.product.exceptions.ItemRemovalFailedException;
import com.flipkart.product.model.product.Product;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import jakarta.persistence.Query;

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

    private final SessionFactory sessionFactory;

    /**
     * <p>
     * Default constructor of the InventoryDAOImpl2 class. Kept private to restrict from creating object from outside of this class.
     * </p>
     */
    private InventoryDAOImpl() {
        sessionFactory = new Configuration().buildSessionFactory();
    }

    /**
     * <p>
     * Creates a single instance of InventoryDAOImpl2 class.
     * </p>
     */
    private static class InstanceHolder {

        private static final InventoryDAOImpl INVENTORY_DAO = new InventoryDAOImpl();
    }

    /**
     * <p>
     * Gets the single object of InventoryDAOImpl2 Class and returns it.
     * </p>
     *
     * @return returns the single instance of InventoryDAOImpl2 Class.
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
        Transaction transaction = null;
        try (final Session session = sessionFactory.openSession()) {
            final HashSet<Product> allProducts = new HashSet<>();

            allProducts.addAll(getMobileItems());
            allProducts.addAll(getLaptopItems());
            allProducts.addAll(getClothesItems());

            for (final Product product : products) {
                if (allProducts.contains(product)) {
                    continue;
                }

                transaction = session.beginTransaction();
                final Query query = (Query) session
                        .createNativeMutationQuery("insert into product (product_category_id, price, quantity) values(?,?,?) returning id");

                query.setParameter(1, product.getProductCategory().getId());
                query.setParameter(2, product.getPrice());
                query.setParameter(3, product.getQuantity());

                int productId = (int) query.getSingleResult();

                product.setId(productId);

                switch (product.getProductCategory()) {
                    case MOBILE, LAPTOP -> saveMobileOrLaptop(product, session);
                    case CLOTHES -> saveClothes(product, session);
                }
                allProducts.add(product);
                session.getTransaction().commit();
            }
        } catch (Exception exception) {
            if (Objects.nonNull(transaction)) {
                transaction.rollback();
            }
            throw new ItemAdditionFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Saves the given mobile or laptop to the inventory.
     * </p>
     *
     * @param product Refers the {@link Product} to be added.
     * @param session Refers the session.
     */
    private void saveMobileOrLaptop(final Product product, final Session session) {
        try {
            final Query query = (Query) session.createNativeMutationQuery("insert into electronics_inventory(product_id, brand, model) values(?, ?, ?)");

            query.setParameter(1, product.getId());
            query.setParameter(2, product.getBrandName());

            if (product.getProductCategory() == ProductCategory.MOBILE) {
                query.setParameter(3, ((Mobile) product).getModel());
            } else {
                query.setParameter(3, ((Laptop) product).getModel());
            }

            query.executeUpdate();
        } catch (Exception exception) {
            throw new ItemAdditionFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Saves the given clothes to the inventory.
     * </p>
     *
     * @param product Refers the {@link Product} to be added.
     * @param session Refers the session.
     */
    private void saveClothes(final Product product, final Session session) {
        try {
            final Query query = (Query) session.createNativeMutationQuery("insert into clothes_inventory(product_id, brand, clothes_type, gender, size) values(?, ?, ?, ?, ?)");

            query.setParameter(1, product.getId());
            query.setParameter(2, product.getBrandName());
            query.setParameter(3, ((Clothes) product).getClothesType());
            query.setParameter(4, ((Clothes) product).getGender());
            query.setParameter(5, ((Clothes) product).getSize());
            query.executeUpdate();
        } catch (Exception exception) {
            throw new ItemAdditionFailedException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Removes the item from the inventory.
     * </p>
     *
     * @param productId Refers the id of the {@link Product} to be removed.
     * @return true if item  removed
     */
    @Override
    public boolean removeItem(final int productId) {
        try (final Session session = sessionFactory.openSession()) {
            final Query query = session.createNativeQuery("Delete from product where id=?", Product.class);

            session.beginTransaction();
            query.setParameter(1, 116);
            final int rowsAffected = query.executeUpdate();

            session.getTransaction().commit();

            return rowsAffected > 0;
        } catch (Exception exception) {
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
     * @return all the {@link Mobile}.
     */
    private List<Mobile> getMobileItems() {
        try (final Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(getQueryForMobiles(), Mobile.class).getResultList();
        } catch (Exception exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the mobiles from the inventory and returns it.
     * </p>
     *
     * @param page Refers the page number.
     * @return all the {@link Product}.
     */
    private List<Mobile> getMobileItems(final int page) {
        final int limit = 5;
        final Session session = sessionFactory.openSession();

        final Query query = session.createNativeQuery(getQueryForMobiles(), Mobile.class);
        query.setFirstResult(page);
        query.setMaxResults(limit);
        session.close();

        return query.getResultList();
    }

    /**
     * <p>
     * Gets all the laptops from the inventory and returns it.
     * </p>
     *
     * @return all the {@link Laptop}.
     */
    private List<Laptop> getLaptopItems() {
        try (final Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(getQueryForLaptops(), Laptop.class).getResultList();
        } catch (Exception exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the Laptops from the inventory and returns it.
     * </p>
     *
     * @param page Refers the page number.
     * @return all the {@link Product} in the mobile inventory.
     */
    private List<Laptop> getLaptopItems(final int page) {
        final int limit = 5;
        final Session session = sessionFactory.openSession();

        final Query query = session.createNativeQuery(getQueryForMobiles(), Laptop.class);
        query.setFirstResult(page);
        query.setMaxResults(limit);
        session.close();

        return query.getResultList();
    }

    /**
     * <p>
     * Gets all the clothes from the inventory and returns it.
     * </p>
     *
     * @return all the {@link Clothes}.
     */
    private List<Clothes> getClothesItems() {
        try (final Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(getQueryForClothes(), Clothes.class).getResultList();
        } catch (Exception exception) {
            throw new ItemNotFoundException(exception.getMessage());
        }
    }

    /**
     * <p>
     * Gets all the clothes from inventory and returns it.
     * </p>
     *
     * @param page Refers the page number.
     * @return all the {@link Product} in the mobile inventory.
     */
    private List<Clothes> getClothesItems(final int page) {
        final int limit = 5;
        final Session session = sessionFactory.openSession();

        final Query query = session.createNativeQuery(getQueryForClothes(), Clothes.class);
        query.setFirstResult(page);
        query.setMaxResults(limit);
        session.close();

        return (List<Clothes>)query.getResultList();
    }

    /**
     * <p>
     * Gets the query for getting all the mobiles.
     * </p>
     *
     * @return the query.
     */
    private String getQueryForMobiles() {
        return String.join(" ", "select  e.brand, e.model, p.price,",
                "p.quantity, p.id from electronics_inventory e join product p on p.id = e.product_id",
                "where p.product_category_id=1");
    }

    /**
     * <p>
     * Gets the query for getting all the laptops.
     * </p>
     *
     * @return the query.
     */
    private String getQueryForLaptops() {
        return String.join(" ", "select  e.brand, e.model, p.price,",
                "p.quantity, p.id from electronics_inventory e join product p on p.id = e.product_id",
                "where p.product_category_id=2");
    }

    /**
     * <p>
     * Gets the query for getting all the clothes.
     * </p>
     *
     * @return the query.
     */
    private String getQueryForClothes() {
        return String.join(" ", "select  c.clothes_type ,c.brand,",
                "c.gender, c.size, p.price,p.quantity,p.id  from clothes_inventory c join product p",
                "on p.id = c.product_id where p.product_category_id =3");
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
        try (final Session session = sessionFactory.openSession()) {
            final Query query = session.createNativeQuery(
                    "select count(id) from product where product.id = :productId", Integer.class);
            query.setParameter("productId", productId);
            final Integer count = (Integer) query.getSingleResult();

            return count > 0;
        } catch (Exception exception) {
            throw new UserCheckFailedException(exception.getMessage());
        }
    }
}
