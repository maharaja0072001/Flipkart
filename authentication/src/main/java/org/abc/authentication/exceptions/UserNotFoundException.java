package org.abc.authentication.exceptions;

import org.abc.exception.CustomException;

/**
 * <p>
 * Represents the exception when user is not found.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class UserNotFoundException extends CustomException {

    /**
     * <p>
     * Constructs the UserNotFoundException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public UserNotFoundException(final String message) {
        super(message);
    }
}
