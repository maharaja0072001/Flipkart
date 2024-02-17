package org.abc.product.exceptions;

import org.abc.exception.CustomException;

/**
 * <p>
 * Represents the exception when order removal action is failed.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class OrderRemovalFailedException extends CustomException {

    /**
     * <p>
     * Constructs the OrderRemovalFailedException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public OrderRemovalFailedException(final String message) {
        super(message);
    }
}
