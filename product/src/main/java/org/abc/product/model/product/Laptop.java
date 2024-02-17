package org.abc.product.model.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.abc.product.ProductCategory;
import org.abc.product.validation.group.ElectronicProductChecker;

import java.util.Objects;

/**
 * <p>
 * Represents the model for laptop.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Entity
@DiscriminatorValue("2")
public class Laptop extends Product {

    @NotNull(message = "Product model name can't be null", groups = ElectronicProductChecker.class)
    private final String model;

    public String getModel() {
            return model;
    }

    @JsonCreator
    public Laptop(@JsonProperty("brandName")final String brandName,
                  @JsonProperty("model")final String model,
                  @JsonProperty("price")final float price,
                  @JsonProperty("quantity")final int quantity) {
        super(ProductCategory.LAPTOP, price, brandName, quantity);

        this.model = model;
    }
    public Laptop(final String brandName, final String model, final double price, final int quantity, final int id) {
        super(ProductCategory.MOBILE, (float) price, brandName, quantity);
        this.model = model;

        super.setId(id);
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

