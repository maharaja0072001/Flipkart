package org.abc.authentication.exceptions;

import org.abc.exception.CustomException;

/**
 * <p>
 * Represents the exception when method is not found.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class MethodNotFoundException extends CustomException {

    /**
     * <p>
     * Constructs the MethodNotFoundException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public MethodNotFoundException(final String message) {
        super(message);
    }
}
