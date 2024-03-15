package com.flipkart.authentication.exceptions;

import com.flipkart.exception.CustomException;

/**
 * <p>
 * Represents the exception when user creation action is failed.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class UserCreationFailedException extends CustomException {

    /**
     * <p>
     * Constructs the UserCreationFailedException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public UserCreationFailedException(final String message) {
        super(message);
    }
}
