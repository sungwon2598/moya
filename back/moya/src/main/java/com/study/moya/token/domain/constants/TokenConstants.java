package com.study.moya.token.domain.constants;

public final class TokenConstants {
    private TokenConstants() {
        // 인스턴스화 방지
    }

    // 토큰 계정 관련 상수
    public static final Long INITIAL_TOKEN_BALANCE = 0L;

    // 거래 타입
    public static final String TRANSACTION_TYPE_CHARGE = "CHARGE";
    public static final String TRANSACTION_TYPE_USAGE = "USAGE";

    // 결제 상태
    public static final String PAYMENT_STATUS_PENDING = "PENDING";
    public static final String PAYMENT_STATUS_COMPLETED = "COMPLETED";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";
    public static final String PAYMENT_STATUS_REFUNDED = "REFUNDED";

    // 결제 방법
    public static final String PAYMENT_METHOD_CARD = "CREDIT_CARD";
    public static final String PAYMENT_METHOD_BANK = "BANK_TRANSFER";
    public static final String PAYMENT_METHOD_VIRTUAL = "VIRTUAL_ACCOUNT";

    // AI 서비스 상태
    public static final String AI_USAGE_STATUS_PENDING = "PENDING";
    public static final String AI_USAGE_STATUS_COMPLETED = "COMPLETED";
    public static final String AI_USAGE_STATUS_FAILED = "FAILED";
}