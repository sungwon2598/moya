package com.study.moya.token.dto.ticket;

import com.study.moya.token.domain.ticket.TicketTransaction;
import com.study.moya.token.domain.enums.TicketType;
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
public class TicketTransactionDto {
    private Long id;
    private Long amount;
    private TransactionType transactionType;
    private TicketType ticketType;
    private Long balanceAfter;
    private String description;
    private LocalDateTime createdAt;

    public static TicketTransactionDto from(TicketTransaction transaction) {
        return TicketTransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .ticketType(transaction.getTicketType())
                .balanceAfter(transaction.getBalanceAfter())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}