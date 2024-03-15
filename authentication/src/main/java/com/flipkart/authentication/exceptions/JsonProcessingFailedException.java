package com.flipkart.authentication.exceptions;

import com.flipkart.exception.CustomException;

/**
 * <p>
 * Represents the exception when json processing failed.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class JsonProcessingFailedException extends CustomException {

    /**
     * <p>
     * Constructs the JsonProcessingFailedException object.
     * </p>
     *
     * @param message Refers the message to be displayed.
     */
    public JsonProcessingFailedException(final String message) {
        super(message);
    }
}
