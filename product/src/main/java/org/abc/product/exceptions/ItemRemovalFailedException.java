package org.abc.product.exceptions;

import org.abc.exception.CustomException;

/**
 * <p>
 * Represents the exception when item removal action is failed.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class ItemRemovalFailedException extends CustomException {

    /**
     * <p>
     * Constructs the ItemRemovalFailedException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public ItemRemovalFailedException(final String message) {
        super(message);
    }
}
