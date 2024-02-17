package org.abc.authentication.exceptions;

import org.abc.exception.CustomException;

/**
 * <p>
 * Represents the exception when algorithm is not found.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class AlgorithmNotFoundException extends CustomException {

    /**
     * <p>
     * Constructs the AlgorithmNotFoundException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public AlgorithmNotFoundException(final String message) {
        super(message);
    }
}
