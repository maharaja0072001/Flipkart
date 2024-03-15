package com.flipkart.authentication.json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>
 * Base class for JSON objects.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public abstract class Json {

    /**
     * <p>
     * Retrieves the underlying JSON object.
     * </p>
     *
     * @return The JSON node representing the JSON object.
     */
    public abstract JsonNode get();

    /**
     * <p>
     * Checks if the JSON object is empty.
     * </p>
     *
     * @return true if the JSON object is empty, false otherwise.
     */
    public abstract boolean isEmpty();

    /**
     * <p>
     * Converts the json to byte array and returns it.
     * </p>
     *
     * @return the byte array.
     */
    public abstract byte[] asBytes();
}
