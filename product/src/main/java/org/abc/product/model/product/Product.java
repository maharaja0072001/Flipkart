package org.abc.product.model.product;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.abc.product.ProductCategory;
import org.abc.product.validation.group.ProductChecker;

import java.util.Objects;

/**
 * <p>
 * Represents the abstract class for the products..
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */

@JsonTypeInfo(use = Id.NAME,
        property = "type")
@JsonSubTypes({
        @Type(value = Mobile.class),
        @Type(value = Laptop.class),
        @Type(value = Clothes.class),
})
@Entity
@Table(name = "product")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "product_category_id", discriminatorType = DiscriminatorType.INTEGER)
public abstract class Product {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Transient
    @NotNull(message = "Product category can't be null", groups = ProductChecker.class)
    private final ProductCategory productCategory;
    @Positive(message = "Price should be positive", groups = ProductChecker.class)
    private float price;
    @Transient
    @NotNull(message = "Brand name can't be null", groups = ProductChecker.class)
    private final String brandName;
    @Positive(message = "Quantity should be positive", groups = ProductChecker.class)
    private int quantity;

    public Product(final ProductCategory productCategory, final float price,
                   final String brandName, final int quantity) {
        this.productCategory = productCategory;
        this.price = price;
        this.brandName = brandName;
        this.quantity = quantity;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public float getPrice() {
        return price;
    }

    public String getBrandName() {
        return brandName;
    }

    public void changePrice(final float price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(final Object object) {
        return !Objects.isNull(object) && getClass() == object.getClass() && this.hashCode() == object.hashCode();
    }
}