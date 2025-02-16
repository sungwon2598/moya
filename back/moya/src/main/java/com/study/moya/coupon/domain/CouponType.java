package com.study.moya.coupon.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponType {
    WELCOME("신규 가입 환영 쿠폰", 10),
    BIRTHDAY("생일 축하 쿠폰", 15),
    HOLIDAY("공휴일 특별 쿠폰", 20),
    VIP("VIP 전용 쿠폰", 25);

    private final String description;
    private final int tokenAmount;
}
