package com.milistock.develop.response;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class ValidationResponse {
    private Map<String, List<String>> fieldErrors = new HashMap<>();

    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }

    public void addFieldError(String fieldName, String errorMessage) {
        fieldErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
    }
}