package com.rabbit.backend.Utilities.Response;

import java.util.HashMap;
import java.util.Map;

public class GeneralResponse {

    public static Map<String, Object> generate(Integer code, Object message) {
        Map<String, Object> result = new HashMap<>();

        result.put("code", code);
        result.put("message", message);

        return result;
    }

    public static Map<String, Object> generate(Integer code) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);

        return result;
    }

}
