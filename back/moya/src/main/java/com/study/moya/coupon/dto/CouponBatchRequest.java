package com.study.moya.coupon.dto;

import com.study.moya.coupon.domain.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Schema(description = "쿠폰 생성 요청")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponBatchRequest {
    @Schema(description = "쿠폰 유형", example = "WELCOME", required = true)
    private CouponType couponType;

    @Schema(description = "생성할 쿠폰 수량", example = "100", required = true, minimum = "1", maximum = "?")
    private int count;

    @NotNull(message = "유효기간은 필수입니다")
    @Schema(description = "쿠폰 만료일시", example = "2024-12-31T23:59:59", required = true)
    private LocalDateTime expirationDate;

    @NotNull(message = "충전될 토큰 잔액은 필수입니다")
    @Schema(description = "충전될 토큰 잔액", example = "1000", required = true, minimum = "1")
    private Long balance;
}
