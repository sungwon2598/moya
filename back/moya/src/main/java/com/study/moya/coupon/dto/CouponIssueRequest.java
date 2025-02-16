package com.study.moya.coupon.dto;

import com.study.moya.coupon.domain.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "쿠폰 발급 요청")
public class CouponIssueRequest {
    @Schema(description = "쿠폰 유형", example = "WELCOME", required = true)
    private CouponType couponType;
}