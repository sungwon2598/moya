package com.study.moya.error.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "001", "유효하지 않은 토큰입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "002", "인증이 필요합니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "003", "접근 권한이 없습니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "004", "만료된 토큰입니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
    private static final String PREFIX = "AUTH";

    @Override
    public String getFullCode() {
        return PREFIX + "_" + code;
    }
}