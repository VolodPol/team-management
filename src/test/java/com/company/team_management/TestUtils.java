package com.company.team_management;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {
    private TestUtils() {}

    public static int generateId() {
        return ThreadLocalRandom.current().nextInt(0, 10);
    }

    public static String objectToJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException();
        }
    }
}
