package com.study.moya.token.domain;

import com.study.moya.BaseEntity;
import com.study.moya.token.domain.enums.PaymentMethod;
import com.study.moya.token.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "token_transactions")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenTransaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "token_account_id", nullable = false)
    private TokenAccount tokenAccount;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "ai_usage_id")
    private AiUsage aiUsage;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Long balanceAfter;

    private String description;

    private String createdBy;
}