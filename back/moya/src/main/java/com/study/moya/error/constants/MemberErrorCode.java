package com.study.moya.error.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "001", "회원을 찾을 수 없습니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "002", "이미 사용 중인 이메일입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "003", "잘못된 비밀번호입니다"),
    MEMBER_WITHDRAWN(HttpStatus.FORBIDDEN, "004", "탈퇴한 회원입니다"),
    MEMBER_SUSPENDED(HttpStatus.FORBIDDEN, "005", "정지된 회원입니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
    private static final String PREFIX = "MEMBER";

    @Override
    public String getFullCode() {
        return PREFIX + "_" + code;
    }
}