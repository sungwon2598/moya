package com.study.moya.coupon.exception;

import com.study.moya.error.constants.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponErrorCode implements ErrorCode {
    @Schema(description = "쿠폰이 존재하지 않음")
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "001", "쿠폰이 존재하지 않습니다."),

    @Schema(description = "만료된 쿠폰")
    EXPIRED_COUPON(HttpStatus.BAD_REQUEST, "002", "만료된 쿠폰입니다."),

    @Schema(description = "사용된 쿠폰")
    ALREADY_USED_COUPON(HttpStatus.BAD_REQUEST, "003", "이미 사용된 쿠폰입니다."),

    @Schema(description = "쿠폰의 사용자가 아님")
    INVALID_COUPON_OWNER(HttpStatus.FORBIDDEN, "004", "쿠폰의 소유자가 아닙니다."),

    @Schema(description = "쿠폰 사용 처리 중 오류 발생")
    COUPON_LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "005", "쿠폰 사용 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),

    @Schema(description = "발급 가능한 쿠폰 없음")
    NO_COUPON_AVAILABLE(HttpStatus.BAD_REQUEST, "006", "발급 가능한 쿠폰이 없습니다."),

    @Schema(description = "회원을 찾을 수 없음")
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "007", "회원을 찾을 수 없습니다."),

    @Schema(description = "쿠폰 발급 처리 중 오류 발생")
    COUPON_ISSUE_LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "008", "쿠폰 발급 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),

    @Schema(description = "쿠폰 발급 실패")
    ISSUE_FAILED(HttpStatus.CONFLICT, "009", "쿠폰 발급에 실패했습니다."),

    @Schema(description = "유효기간이 현재 시간보다 이전")
    INVALID_EXPIRATION_DATE(HttpStatus.BAD_REQUEST, "010", "유효기간이 현재 시간보다 이전입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
    private static final String PREFIX = "COUPON";

    @Override
    public String getFullCode() {
        return PREFIX + "_" + code;
    }
}