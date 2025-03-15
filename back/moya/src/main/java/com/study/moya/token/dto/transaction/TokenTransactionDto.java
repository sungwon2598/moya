package com.study.moya.token.dto.transaction;

import com.study.moya.token.domain.TokenTransaction;
import com.study.moya.token.domain.enums.PaymentMethod;
import com.study.moya.token.domain.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenTransactionDto {
    private Long id;
    private Long tokenAccountId;
    private Long memberId;
    private Long paymentId;
    private String orderId;
    private Long aiUsageId;
    private Long amount;
    private TransactionType transactionType;
    private PaymentMethod paymentMethod; // 결제 방법 추가
    private Long balanceAfter;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;

    public static TokenTransactionDto from(TokenTransaction transaction) {
        return TokenTransactionDto.builder()
                .id(transaction.getId())
                .tokenAccountId(transaction.getTokenAccount().getId())
                .memberId(transaction.getTokenAccount().getMember().getId())
                .paymentId(transaction.getPayment() != null ? transaction.getPayment().getId() : null)
                .orderId(transaction.getPayment() != null ? transaction.getPayment().getOrderId() : null)
                .aiUsageId(transaction.getAiUsage() != null ? transaction.getAiUsage().getId() : null)
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .paymentMethod(transaction.getPaymentMethod()) // 결제 방법 추가
                .balanceAfter(transaction.getBalanceAfter())
                .description(transaction.getDescription())
                .createdBy(transaction.getCreatedBy())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}