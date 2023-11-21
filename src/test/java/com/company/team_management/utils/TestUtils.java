package com.company.team_management.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {
    private static ObjectMapper mapper;
    private TestUtils() {}

    public static int generateId() {
        return ThreadLocalRandom.current().nextInt(0, 10);
    }

    public static String objectToJsonString(Object object) {
        if (mapper == null)
            mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException();
        }
    }
}
