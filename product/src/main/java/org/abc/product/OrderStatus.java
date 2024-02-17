package org.abc.product;

import org.abc.product.exceptions.ConstantNotFoundException;

import java.util.EnumSet;

/**
 * <p>
 * Provides the status of order.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public enum OrderStatus {

    PLACED(1), DELIVERED(2), IN_TRANSIT(3), CANCELLED(4);

    final int id;

    /**
     * <p>
     * Constructor of the enum.
     * </p>
     *
     * @param id Refers the id of the enum values
     */
    OrderStatus(final int id) {
        this.id = id;
    }

    /**
     * <p>
     * Gets the id of the enum value of returns it.
     * </p>
     *
     * @return the id of the enum value.
     */
    public int getId() {
        return id;
    }

    /**
     * <p>
     * Gets the enum value based on id and returns it.
     * </p>
     *
     * @param id Refers the id of the enum value.
     * @return the enum value.
     */
    public static OrderStatus valueOf(final int id) {
        return EnumSet.allOf(OrderStatus.class)
                .stream()
                .filter(orderStatus -> orderStatus.id == id)
                .findFirst()
                .orElseThrow(() -> new ConstantNotFoundException(String.format("Constant not found for the id: %d", id)));
    }
}
