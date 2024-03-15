package com.flipkart.authentication.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.flipkart.authentication.exceptions.JsonProcessingFailedException;

/**
 * <p>
 * Represents a JSON array.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class JsonArray extends Json {

    private final ArrayNode arrayNode;

    /**
     * <p>
     * Constructs a new JsonArray instance with the specified JSON node.
     * </p>
     *
     *
     * @param jsonNode Refers the underlying JSON node.
     */
    public JsonArray(final JsonNode jsonNode) {
        this.arrayNode = (ArrayNode) jsonNode;
    }

    /**
     * <p>
     * Returns a string representation of the JSON array.
     * </p>
     *
     * @return The string representation of the JSON array.
     */
    @Override
    public String toString() {
        return arrayNode.toString();
    }

    /**
     * <p>
     * Retrieves the underlying JSON object.
     * </p>
     *
     * @return the underlying json object.
     */
    @Override
    public JsonNode get() {
        return arrayNode;
    }

    /**
     * <p>
     * Checks if the JSON object is empty.
     * </p>
     *
     * @return true if the JSON object is empty, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return arrayNode.isEmpty();
    }

    /**
     * <p>
     * Adds a JSON object to the JSON array.
     * </p>
     *
     * @param jsonObject Refers the JSON object to add.
     * @return The updated JsonArray instance.
     */
    public JsonArray add(final Json jsonObject) {
        arrayNode.add(jsonObject.get());

        return this;
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
            return new ObjectMapper().writeValueAsBytes(arrayNode);
        } catch (JsonProcessingException exception) {
            throw new JsonProcessingFailedException(exception.getMessage());
        }
    }
}
