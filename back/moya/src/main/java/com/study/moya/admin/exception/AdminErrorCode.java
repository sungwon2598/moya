package com.study.moya.admin.exception;

import com.study.moya.error.constants.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements ErrorCode {
    // 공통 에러
    @Schema(description = "관리자 권한 없음")
    UNAUTHORIZED_ADMIN(HttpStatus.FORBIDDEN, "001", "관리자 권한이 없습니다."),

    // 회원 관리 관련 에러
    @Schema(description = "회원을 찾을 수 없음")
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "101", "해당 회원을 찾을 수 없습니다."),

    @Schema(description = "이미 차단된 회원")
    ALREADY_BLOCKED_MEMBER(HttpStatus.BAD_REQUEST, "102", "이미 차단된 회원입니다."),

    @Schema(description = "차단 사유 누락")
    BLOCK_REASON_REQUIRED(HttpStatus.BAD_REQUEST, "103", "차단 사유를 입력해주세요."),

    // 게시물 관리 관련 에러
    @Schema(description = "게시물을 찾을 수 없음")
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "201", "해당 게시물을 찾을 수 없습니다."),

    @Schema(description = "이미 삭제된 게시물")
    ALREADY_DELETED_POST(HttpStatus.BAD_REQUEST, "202", "이미 삭제된 게시물입니다."),

    // 쿠폰 관리 관련 에러
    @Schema(description = "통계 기간 오류")
    INVALID_STATISTICS_PERIOD(HttpStatus.BAD_REQUEST, "301", "유효하지 않은 통계 조회 기간입니다."),

    @Schema(description = "쿠폰 통계 조회 실패")
    STATISTICS_RETRIEVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "302", "쿠폰 통계 조회 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
    private static final String PREFIX = "ADMIN";

    @Override
    public String getFullCode() {
        return PREFIX + "_" + code;
    }
}