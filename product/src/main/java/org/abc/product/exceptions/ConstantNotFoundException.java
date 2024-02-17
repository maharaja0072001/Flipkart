package org.abc.product.exceptions;

import org.abc.exception.CustomException;

/**
 * <p>
 * Represents the exception when constant not found in enum.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class ConstantNotFoundException extends CustomException {

    /**
     * <p>
     * Constructs the ConstantNotFoundException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public ConstantNotFoundException(final String message) {
        super(message);
    }
}
