package com.study.moya.token.domain.enums;

public enum PaymentStatus {
    PENDING,    // 결제 대기 중
    COMPLETED,  // 결제 완료
    FAILED,     // 결제 실패
    REFUNDED    // 환불됨
}