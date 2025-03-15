package com.study.moya.token.domain.enums;

public enum TransactionType {
    CHARGE,         // 충전
    USAGE,          // 사용
    REFUND,         // 환불
    ADMIN_CHARGE,   // 관리자 추가
    ADMIN_DEDUCT    // 관리자 차감
}