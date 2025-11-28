package com.aibidcomposer.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * JSON Utility Class
 *
 * <p>Provides utility methods for JSON serialization and deserialization using Jackson library.
 * Includes methods for converting objects to JSON strings, parsing JSON to objects/maps,
 * and handling JSON nodes.</p>
 *
 * <p>Features:
 * <ul>
 *   <li>Object to JSON string conversion</li>
 *   <li>JSON string to object parsing</li>
 *   <li>JSON to Map conversion</li>
 *   <li>JSON to List conversion</li>
 *   <li>Support for Java 8 Date/Time API</li>
 * </ul>
 * </p>
 *
 * <p>需求编号: REQ-JAVA-COMMON-002</p>
 *
 * @author AIBidComposer Team
 * @version 1.0
 * @since 2025-11-26
 */
@Slf4j
public class JsonUtil {

    /**
     * Private constructor to prevent instantiation
     */
    private JsonUtil() {}

    /**
     * Date format pattern for JSON serialization
     */
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * ObjectMapper instance for JSON operations - thread-safe for mapping only
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // Do not write null values
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Entity property mapping does not need to be one-to-one
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Enable accepting empty string as null object
        OBJECT_MAPPER.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        // Date format configuration
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(DATE_PATTERN));
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Register JavaTimeModule for Java 8 Date/Time API
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * Convert object to JSON string
     *
     * @param obj the object to convert
     * @return JSON string representation of the object, or null if conversion fails
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convert object to pretty JSON string with indentation
     *
     * @param obj the object to convert
     * @return pretty-printed JSON string, or null if conversion fails
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to pretty JSON: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convert JSON string to object
     *
     * @param jsonStr JSON string to parse
     * @param clazz target class type
     * @param <T> type parameter
     * @return parsed object of specified type, or null if parsing fails
     */
    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON to {}: {}", clazz.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convert JSON string to object with TypeReference
     *
     * <p>This method is useful for parsing generic types like List&lt;User&gt;, Map&lt;String, Object&gt;</p>
     *
     * @param jsonStr JSON string to parse
     * @param typeRef TypeReference for the target type
     * @param <T> type parameter
     * @return parsed object of specified type, or null if parsing fails
     */
    public static <T> T fromJson(String jsonStr, TypeReference<T> typeRef) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonStr, typeRef);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convert JSON string to Map
     *
     * @param jsonStr JSON string to parse
     * @return Map containing the parsed JSON data, or null if parsing fails
     */
    public static Map<String, Object> toMap(String jsonStr) {
        return fromJson(jsonStr, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Convert JSON string to List
     *
     * @param jsonStr JSON string to parse
     * @param elementClass class type for list elements
     * @param <T> type parameter
     * @return List of objects parsed from JSON, or null if parsing fails
     */
    public static <T> List<T> toList(String jsonStr, Class<T> elementClass) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, elementClass);
            return OBJECT_MAPPER.readValue(jsonStr, javaType);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON to List: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convert JSON string to JsonNode
     *
     * @param jsonStr JSON string to parse
     * @return JsonNode representing the parsed JSON structure, or null if parsing fails
     */
    public static JsonNode parseTree(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON to tree: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get string value from JsonNode
     *
     * @param node JsonNode to extract value from
     * @return String value of the node, or null if node is null
     */
    public static String getNodeValue(JsonNode node) {
        if (node == null) {
            return null;
        }
        return node.asText();
    }

    /**
     * Get ObjectMapper instance
     *
     * <p>Use this method if you need to customize the ObjectMapper configuration</p>
     *
     * @return the shared ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
