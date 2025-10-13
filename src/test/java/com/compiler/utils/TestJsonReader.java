package com.compiler.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class TestJsonReader {
    @Test
    public void TestreadMap() {
        Map<String, Integer> map = JsonReader.readMapInteger(PathConfig.TOKEN_MAP_JSON, "tokenMap");
        Assertions.assertNotNull(map);
        Assertions.assertEquals(1, map.get("begin"));
        Assertions.assertEquals(2, map.get("end"));
        Assertions.assertEquals(3, map.get("integer"));
        Assertions.assertEquals(12, map.get("="));
        Assertions.assertEquals(23, map.get(";"));
    }
}
