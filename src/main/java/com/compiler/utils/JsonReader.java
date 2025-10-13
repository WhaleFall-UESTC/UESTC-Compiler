package com.compiler.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonReader {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Read a Map from json, which value is Object
     * @param jsonPath the path of json
     * @param node the name of the top-level JSON object field to extract
     * @return a {@code Map<String, Object>} representing the nested JSON object
     *         corresponding to the given {@code node}, or {@code null} if the file
     *         is not found, the node does not exist, or an I/O error occurs.
     */
    public static Map<String, Object> readMapObject(String jsonPath, String node) {
        try (InputStream inputStream = JsonReader.class.getClassLoader().getResourceAsStream(jsonPath)) {
            if (inputStream == null) {
                MiniLogger.error("File not found: " + jsonPath);
                return null;
            }
            Map<String, Object> root = mapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});
            Object mapObj = root.get(node);
            if (mapObj == null) {
                MiniLogger.error("Map not found: " + node);
                return null;
            }
            @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) mapObj;
            return map;
        } catch (IOException e) {
            MiniLogger.error("Error while trying to read json file: " + jsonPath, e);
            return null;
        }
    }

    public static Map<String, Integer> readMapInteger(String jsonPath, String node) {
        return mapper.convertValue(readMapObject(jsonPath, node), new TypeReference<Map<String, Integer>>() {});
    }

    /**
     * Reads a list of strings from a JSON file in the classpath.
     * @param jsonPath the path to the JSON file in the classpath (e.g., "config/tokens.json")
     * @param node     the name of the top-level JSON field that contains a string array
     *                 (e.g., "mathOp" in {"mathOp": ["+", "-", ...]})
     * @return a {@code List<String>} containing the string elements from the specified JSON array,
     *         or {@code null} if the file is not found, the node does not exist, the value is not
     *         a list, or an I/O or parsing error occurs
     */
    public static List<String> readStringList(String jsonPath, String node) {
        try (InputStream inputStream = JsonReader.class.getClassLoader().getResourceAsStream(jsonPath)) {
            if (inputStream == null) {
                MiniLogger.error("File not found: " + jsonPath);
                return null;
            }
            Map<String, Object> root = mapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});
            Object listObj = root.get(node);
            if (listObj == null) {
                MiniLogger.error("List not found: " + node);
                return null;
            }
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) listObj;
            return list;
        } catch (IOException e) {
            MiniLogger.error("Error while trying to read json file: " + jsonPath, e);
            return null;
        }
    }

    public static List<Character> readCharList(String jsonPath, String node) {
        List<String> stringList = readStringList(jsonPath, node);
        if (stringList == null) {
            return null;
        }

        try {
            return stringList.stream()
                    .map(s -> {
                        if (s == null || s.length() != 1) {
                            throw new IllegalArgumentException("Expected single-character string, but got: '" + s + "'");
                        }
                        return s.charAt(0);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            MiniLogger.error("Error converting string list to char list for node: " + node, e);
            return null;
        }
    }
}
