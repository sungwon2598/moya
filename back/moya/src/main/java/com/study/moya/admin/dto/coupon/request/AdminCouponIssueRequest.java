package com.study.moya.admin.dto.coupon.request;

import com.study.moya.coupon.domain.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "관리자 쿠폰 발급 요청")
public class AdminCouponIssueRequest {

    @NotNull(message = "대상 사용자 ID는 필수입니다")
    @Positive(message = "사용자 ID는 양수여야 합니다")
    @Schema(description = "쿠폰을 받을 사용자 ID", example = "123", required = true)
    private Long targetMemberId;

    @NotNull(message = "쿠폰 타입은 필수입니다")
    @Schema(description = "쿠폰 유형", example = "WELCOME", required = true)
    private CouponType couponType;

    @NotNull(message = "만료일은 필수입니다")
    @Schema(description = "쿠폰 만료일시", example = "2024-12-31T23:59:59", required = true)
    private LocalDateTime expirationDate;

    @NotNull(message = "토큰 잔액은 필수입니다")
    @Positive(message = "토큰 잔액은 양수여야 합니다")
    @Schema(description = "충전될 토큰 잔액", example = "10000", required = true)
    private Long balance;

    @Schema(description = "발급 사유", example = "고객센터 보상")
    private String reason;
}
