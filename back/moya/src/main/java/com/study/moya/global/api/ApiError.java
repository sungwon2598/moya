package com.study.moya.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API 에러 정보")
class ApiError {
    @Schema(description = "에러 코드", example = "COMMON_001")
    private final String code;

    @Schema(description = "에러 메시지", example = "잘못된 입력값입니다")
    private final String message;

    @Schema(description = "추가 에러 정보")
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