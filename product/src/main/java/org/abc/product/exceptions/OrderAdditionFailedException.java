package org.abc.product.exceptions;

import org.abc.exception.CustomException;

/**
 * <p>
 * Represents the exception when order addition action is failed.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class OrderAdditionFailedException extends CustomException {

    /**
     * <p>
     * Constructs the OrderAdditionFailedException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public OrderAdditionFailedException(final String message) {
        super(message);
    }
}
