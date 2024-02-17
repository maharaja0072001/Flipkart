package org.abc.product.exceptions;

import org.abc.exception.CustomException;

/**
 * <p>
 * Represents the exception when item update action is failed.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class ItemUpdateFailedException extends CustomException {

    /**
     * <p>
     * Constructs the ItemUpdateFailedException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public ItemUpdateFailedException(final String message) {
        super(message);
    }
}
