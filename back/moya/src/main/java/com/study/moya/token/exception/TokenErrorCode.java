package com.study.moya.token.exception;

import com.study.moya.error.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {
    INSUFFICIENT_TOKEN(HttpStatus.BAD_REQUEST, "001", "토큰 잔액이 부족합니다"),
    TOKEN_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "002", "토큰 계정이 존재하지 않습니다"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "003", "회원이 존재하지 않습니다"),
    TOKEN_PACKAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "004", "토큰 패키지가 존재하지 않습니다"),
    TOKEN_PACKAGE_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "005", "비활성화된 토큰 패키지입니다"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "006", "결제 정보가 존재하지 않습니다"),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "007", "이미 처리된 결제입니다"),
    AI_SERVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "008", "AI 서비스가 존재하지 않습니다"),
    AI_SERVICE_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "009", "비활성화된 AI 서비스입니다"),
    AI_USAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "010", "AI 사용 내역이 존재하지 않습니다"),
    TRANSACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "011", "토큰 거래 처리 중 오류가 발생했습니다"),
    PAYMENT_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "012", "결제 처리 중 오류가 발생했습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
    private static final String PREFIX = "TOKEN";

    @Override
    public String getFullCode() {
        return PREFIX + "_" + code;
    }
}