package com.flipkart.authentication.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * <p>
 * Creates JSON objects and arrays, and converts Java objects to JSON.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class JsonFactory {

    private final ObjectMapper objectMapper;

    /**
     * <p>
     * Constructs a new JsonFactory.
     * </p>
     *
     */
    public JsonFactory() {
        this.objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * <p>
     * Creates a new JSON object.
     * </p>
     *
     * @return the JSON object.
     */
    public JsonObject createJsonObject() {
        return new JsonObject(objectMapper.createObjectNode());
    }

    /**
     * <p>
     * Creates a new JSON array object.
     * </p>
     *
     * @return the JSON array object.
     */
    public JsonArray createJsonArray() {
        return new JsonArray(objectMapper.createArrayNode());
    }

    /**
     * <p>
     * Converts a Java object to JSON.
     * </p>
     *
     * @param object Refers the Java object to convert.
     * @return the JSON representation of the object.
     */
    public Json toJson(final Object object) {
        final JsonNode jsonNode = objectMapper.valueToTree(object);

        if (jsonNode.isArray()) {
            return new JsonArray(jsonNode);
        }

        return new JsonObject(jsonNode);
    }

    /**
     * <p>
     * Returns the provider instance for Jackson.
     * </p>
     *
     * @return the provider.
     */
    public static JacksonJsonProvider getProvider(){
        return new JacksonJsonProvider();
    }
}
