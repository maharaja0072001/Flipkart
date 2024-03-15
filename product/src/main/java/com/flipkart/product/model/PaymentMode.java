package com.flipkart.product.model;

import com.flipkart.product.exceptions.ConstantNotFoundException;

/**
 * <p>
 * Provides the payment modes.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public enum PaymentMode {

    CASH_ON_DELIVERY(1), CREDIT_OR_DEBIT_CARD(2), NET_BANKING(3), UPI(4);

    final int id;

    /**
     * <p>
     * Constructor of the enum.
     * </p>
     *
     * @param id Refers the id of the enum values
     */
     PaymentMode(final int id) {
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
    public static PaymentMode valueOf(final int id) {
        return switch (id) {
            case 1 -> CASH_ON_DELIVERY;
            case 2 -> CREDIT_OR_DEBIT_CARD;
            case 3 -> NET_BANKING;
            case 4 -> UPI;
            default -> throw new ConstantNotFoundException(String.format("Constant not found for the id: %d", id));
        };
    }
}
