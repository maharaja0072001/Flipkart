package com.flipkart.product.exceptions;

import com.flipkart.exception.CustomException;

/**
 * <p>
 * Represents the exception when rollback in transaction is failed.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class RollbackFailedException extends CustomException {

    /**
     * <p>
     * Constructs the RollbackFailedException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */

    public RollbackFailedException(final String message) {
        super(message);
    }
}
