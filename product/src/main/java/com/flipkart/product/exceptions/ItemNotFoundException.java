package com.flipkart.product.exceptions;

import com.flipkart.exception.CustomException;

/**
 * <p>
 * Represents the exception when item is not found.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class ItemNotFoundException extends CustomException {

    /**
     * <p>
     * Constructs the ItemNotFoundException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public ItemNotFoundException(final String message) {
        super(message);
    }
}
