package com.study.moya.token.domain.ticket;

import com.study.moya.BaseEntity;
import com.study.moya.token.domain.Payment;
import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket_transactions")
public class TicketTransaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_account_id", nullable = false)
    private TicketAccount ticketAccount;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;  // 티켓 구매 시

    @ManyToOne
    @JoinColumn(name = "ticket_usage_id")
    private TicketUsage ticketUsage;  // 티켓 사용 시

    private Long amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;  // 기존 enum 재사용

    @Enumerated(EnumType.STRING)
    private TicketType ticketType;             // 새로 추가한 enum

    private Long balanceAfter;
    private String description;
}
