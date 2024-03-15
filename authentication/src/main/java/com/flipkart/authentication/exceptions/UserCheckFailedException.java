package com.flipkart.authentication.exceptions;

import com.flipkart.exception.CustomException;

/**
 * <p>
 * Represents the exception when user check action is failed.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class UserCheckFailedException extends  CustomException {

    /**
     * <p>
     * Constructs the UpdateActionFailedException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public UserCheckFailedException(final String message) {
        super(message);
    }
}
