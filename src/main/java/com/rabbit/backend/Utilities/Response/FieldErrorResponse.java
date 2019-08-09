package com.rabbit.backend.Utilities.Response;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldErrorResponse {
    @Deprecated
    public static Map<String, Object> oldGenerator(Errors errors) {
        Map<String, Object> errResponse = new HashMap<>();
        List<ObjectError> objectErrorList = errors.getAllErrors();
        for (ObjectError error : objectErrorList) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                errResponse.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                errResponse.put(error.getObjectName(), error.getDefaultMessage());
            }
        }
        return errResponse;
    }

    public static String generator(Errors errors) {
        StringBuilder errResponse = new StringBuilder();
        List<ObjectError> objectErrorList = errors.getAllErrors();
        for (ObjectError error : objectErrorList) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                errResponse.append(fieldError.getField()).append(":").append(fieldError.getDefaultMessage()).append("\n");
            } else {
                errResponse.append(error.getObjectName()).append(":").append(error.getDefaultMessage()).append("\n");
            }
        }
        return errResponse.toString();
    }
}
