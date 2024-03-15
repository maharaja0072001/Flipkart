package com.flipkart.authentication.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.authentication.exceptions.JsonProcessingFailedException;

/**
 * <p>
 * Represents a JSON object.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class JsonObject extends Json {

    private final ObjectNode objectNode;

    /**
     * <p>
     * Constructs a new Json object with the specified JSON node.
     * </p>
     *
     *
     * @param jsonNode Refers the underlying JSON node.
     */
    public JsonObject(final JsonNode jsonNode) {
        this.objectNode = (ObjectNode) jsonNode;
    }

    /**
     * <p>
     * Returns a string representation of the JSON object.
     * </p>
     *
     * @return The string representation of the JSON object.
     */
    @Override
    public String toString() {
        return objectNode.toString();
    }

    /**
     * <p>
     * Adds or updates a field in this JSON object with the specified value.
     * </p>
     *
     * @param fieldName The name of the field.
     * @param value     The value to set for the field.
     * @return This JsonObject instance for method chaining.
     */
    public JsonObject put(final String fieldName, final String value) {
        objectNode.put(fieldName, value);

        return this;
    }

    /**
     * <p>
     * Sets the value of a field in this JSON object to the specified JSON value.
     * </p>
     *
     * @param fieldName The name of the field.
     * @param value     The JSON value to set for the field.
     * @return This JsonObject instance for method chaining.
     */
    public JsonObject set(final String fieldName, final Json value) {
        objectNode.set(fieldName, value.get());

        return this;
    }

    /**
     * <p>
     * Checks if the JSON object is empty.
     * </p>
     *
     * @return true if the JSON object is empty, false otherwise.
     */
    public boolean isEmpty() {
        return objectNode.isEmpty();
    }

    /**
     * <p>
     * Retrieves the underlying JSON object.
     * </p>
     *
     * @return the json object.
     */
    @Override
    public JsonNode get() {
        return objectNode;
    }

    /**
     * <p>
     * Converts the object node to byte array and returns it.
     * </p>
     *
     * @return the byte array.
     */
    @Override
    public byte[] asBytes() {
        try {
            return new ObjectMapper().writeValueAsBytes(objectNode);
        } catch (JsonProcessingException exception) {
            throw new JsonProcessingFailedException(exception.getMessage());
        }
    }
}
