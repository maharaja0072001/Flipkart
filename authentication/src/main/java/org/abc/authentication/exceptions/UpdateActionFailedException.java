package org.abc.authentication.exceptions;

import org.abc.exception.CustomException;

/**
 * <p>
 * Represents the exception when user detail update action is failed.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class UpdateActionFailedException extends CustomException {

    /**
     * <p>
     * Constructs the UpdateActionFailedException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public UpdateActionFailedException(final String message) {
        super(message);
    }
}
