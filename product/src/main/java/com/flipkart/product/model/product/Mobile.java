package com.flipkart.product.model.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import com.flipkart.product.model.ProductCategory;
import com.flipkart.product.validation.group.ElectronicProductChecker;

import java.util.Objects;

/**
 * <p>
 * Represents the model for Mobile.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Entity
public non-sealed class Mobile extends Product {

    @NotNull(message = "Product model name can't be null", groups = ElectronicProductChecker.class)
    private final String model;

    @JsonCreator
    public Mobile(@JsonProperty("brandName") final String brandName,
                  @JsonProperty("model") final String model,
                  @JsonProperty("float") final float price,
                  @JsonProperty("quantity") final int quantity) {
        super(ProductCategory.MOBILE, price, brandName, quantity);
        this.model = model;
    }

    public Mobile(final String brandName, final String model, final double price, final int quantity, final int id) {
        super(ProductCategory.MOBILE, (float) price, brandName, quantity);
        this.model = model;

        super.setId(id);
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return String.format("%s : %s - Rs : %.2f", super.getBrandName(), model, super.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBrandName(), model);
    }
}