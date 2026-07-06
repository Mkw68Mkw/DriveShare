package ch.zli.m223.util;

import java.util.Map;

public final class ApiError {

    private ApiError() {
    }

    public static Map<String, String> of(String message) {
        return Map.of("error", message);
    }
}
