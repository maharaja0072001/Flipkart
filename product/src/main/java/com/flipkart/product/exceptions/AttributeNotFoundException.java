package com.flipkart.product.exceptions;

import com.flipkart.exception.CustomException;

/**
 * <p>
 * Represents the exception when attributes not found.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class AttributeNotFoundException extends CustomException {

    /**
     * <p>
     * Constructs the AttributeNotFoundException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public AttributeNotFoundException(final String message) {
        super(message);
    }
}
