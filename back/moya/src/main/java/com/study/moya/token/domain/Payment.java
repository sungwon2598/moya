package com.study.moya.token.domain;

import com.study.moya.BaseEntity;
import com.study.moya.member.domain.Member;
import com.study.moya.token.domain.enums.PaymentMethod;
import com.study.moya.token.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(unique = true)
    private String orderId;

    private Long amount;

    @Column(length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String paymentGateway;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Long tokenAmount;

    private LocalDateTime paymentApprovedAt;

    // Builder 패턴이 사용되면 setter 대신 이 메서드 사용
    public void completePayment() {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.paymentApprovedAt = LocalDateTime.now();
    }

    public void updatePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }
}