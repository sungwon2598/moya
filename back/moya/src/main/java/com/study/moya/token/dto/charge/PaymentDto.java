package com.study.moya.token.dto.charge;

import com.study.moya.token.domain.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private Long memberId;
    private String orderId;
    private Long amount;
    private String currency;
    private String paymentMethod;
    private String paymentGateway;
    private String paymentStatus;
    private Long tokenAmount;
    private LocalDateTime paymentApprovedAt;
    private LocalDateTime createdAt;

    public static PaymentDto from(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .memberId(payment.getMember().getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod().name())
                .paymentGateway(payment.getPaymentGateway())
                .paymentStatus(payment.getPaymentStatus().name())
                .tokenAmount(payment.getTokenAmount())
                .paymentApprovedAt(payment.getPaymentApprovedAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}