package org.abc.exception;

/**
 * <p>
 * Provides custom exception which extends the RuntimeException.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class CustomException extends RuntimeException {

    /**
     * <p>
     * Constructs the Custom exception object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public CustomException(final String message) {
        super(message);
    }
}
