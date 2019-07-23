package com.rabbit.backend.Utilities;

import java.util.HashMap;
import java.util.Map;

public class GeneralResponse {

    public static Map<String, Object> generator(Integer code, Object message) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("code", code);
        result.put("message", message);

        return result;
    }

    public static Map<String, Object> generator(Integer code) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("code", code);

        return result;
    }

}
