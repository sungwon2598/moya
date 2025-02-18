package com.study.moya.oauth.exception;

import com.study.moya.error.constants.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OAuthErrorCode implements ErrorCode {
    @Schema(description = "ID 토큰이 유효하지 않음")
    INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED, "001", "유효하지 않은 ID 토큰입니다."),

    @Schema(description = "리프레시 토큰이 유효하지 않음")
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "002", "유효하지 않은 리프레시 토큰입니다."),

    @Schema(description = "액세스 토큰이 유효하지 않음")
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "003", "유효하지 않은 액세스 토큰입니다."),

    @Schema(description = "인증 코드가 유효하지 않음")
    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "004", "유효하지 않은 인증 코드입니다."),

    @Schema(description = "Google API 호출 실패")
    GOOGLE_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "005", "Google API 호출 중 오류가 발생했습니다."),

    @Schema(description = "회원 정보를 찾을 수 없음")
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "006", "회원 정보를 찾을 수 없습니다."),

    @Schema(description = "로그아웃 처리 실패")
    LOGOUT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "007", "로그아웃 처리에 실패했습니다."),

    @Schema(description = "회원 탈퇴 처리 실패")
    WITHDRAWAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "008", "회원 탈퇴 처리에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
    private static final String PREFIX = "OAUTH";

    @Override
    public String getFullCode() {
        return PREFIX + "_" + code;
    }
}
