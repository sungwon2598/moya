package com.study.moya.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiError {
    private final String code;
    private final String message;
    private final Map<String, Object> details;

    private ApiError(String code, String message, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public static ApiError of(String code, String message) {
        return new ApiError(code, message, null);
    }

    public static ApiError of(String code, String message, Map<String, Object> details) {
        return new ApiError(code, message, details);
    }
}