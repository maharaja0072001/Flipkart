package org.abc.product.exceptions;

import org.abc.exception.CustomException;

/**
 * <p>
 * Represents the exception when item addition action is failed.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class ItemAdditionFailedException extends CustomException {

    /**
     * <p>
     * Constructs the ItemAdditionFailedException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public ItemAdditionFailedException(final String message) {
        super(message);
    }
}
