package com.study.moya.coupon.dto;

import com.study.moya.coupon.domain.Coupon;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "쿠폰 발급 응답")
public class CouponResponse {
    @Schema(description = "쿠폰 ID", example = "1")
    private Long couponId;

    @Schema(description = "회원 ID", example = "100")
    private Long memberId;

    @Schema(description = "쿠폰 만료일시", example = "2024-12-31T23:59:59")
    private LocalDateTime expirationDate;

    @Schema(description = "쿠폰 사용 여부", example = "false")
    private boolean isUsed;

    public static CouponResponse from(Coupon coupon) {
        return CouponResponse.builder()
                .couponId(coupon.getId())
                .memberId(coupon.getMember().getId())
                .expirationDate(coupon.getExpirationDate())
                .isUsed(coupon.isUsed())
                .build();
    }
}
