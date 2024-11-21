package com.study.moya.error.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "001", "잘못된 입력값입니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "002", "잘못된 타입입니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "내부 서버 오류가 발생했습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
    private static final String PREFIX = "COMMON";

    @Override
    public String getFullCode() {
        return PREFIX + "_" + code;
    }
}