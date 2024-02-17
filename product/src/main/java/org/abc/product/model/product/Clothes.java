package org.abc.product.model.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.abc.product.ProductCategory;
import org.abc.product.validation.group.ClothesChecker;
import org.abc.product.validation.group.ElectronicProductChecker;

import java.util.Objects;

/**
 * <p>
 * Represents the model for clothes.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Entity
@DiscriminatorValue("3")
public class Clothes extends Product {

    @NotNull(message = "Gender can't be null", groups = ClothesChecker.class)
    private final String gender;
    @NotNull(message = "Size can't be null", groups = ClothesChecker.class)
    private final String size;
    @NotNull(message = "Clothes type can't be null", groups = ClothesChecker.class)
    private final String clothesType;

    @JsonCreator
    public Clothes(@JsonProperty("clothesType")final String clothesType,
                   @JsonProperty("gender")final String gender,
                   @JsonProperty("size")final String size,
                   @JsonProperty("price")final float price,
                   @JsonProperty("brandName")final String brandName,
                   @JsonProperty("quantity")final int quantity) {
        super(ProductCategory.CLOTHES, price, brandName, quantity);

        this.gender = gender;
        this.size = size;
        this.clothesType = clothesType;
    }

    public Clothes(final String clothesType, final String brandName, final String gender, final String size,
                   final double price, final int quantity, final int id) {
        super(ProductCategory.CLOTHES, (float) price, brandName, quantity);

        this.gender = gender;
        this.size = size;
        this.clothesType = clothesType;

        super.setId(id);
    }

    public String getClothesType() {
        return clothesType;
    }

    public String getGender() {
        return gender;
    }

    public String getSize() {
        return size;
    }

    @Override
    public String toString() {
        return String.format("%s - %s : %s : %s : %.2f", clothesType, super.getBrandName(), gender, size, super.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBrandName(), gender, size, clothesType);
    }
}
