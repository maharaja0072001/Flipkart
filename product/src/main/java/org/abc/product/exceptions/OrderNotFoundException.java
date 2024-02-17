package org.abc.product.exceptions;

import org.abc.exception.CustomException;

/**
 * <p>
 * Represents the exception when order is not found.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class OrderNotFoundException extends CustomException {

    /**
     * <p>
     * Constructs the OrderNotFoundException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public OrderNotFoundException(final String message) {
        super(message);
    }
}
