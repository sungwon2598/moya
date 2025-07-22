package com.study.moya.token.dto.ticket;

import com.study.moya.token.domain.ticket.TicketTransaction;
import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.domain.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "티켓 거래 내역")
public class TicketTransactionDto {

    @Schema(description = "거래 ID", example = "2")
    private Long id;

    @Schema(description = "거래 수량", example = "3")
    private Long amount;

    @Schema(description = "거래 유형", example = "ADMIN_CHARGE")
    private TransactionType transactionType;

    @Schema(description = "티켓 타입", example = "ROADMAP_TICKET")
    private TicketType ticketType;

    @Schema(description = "거래 후 잔액", example = "7")
    private Long balanceAfter;

    @Schema(description = "거래 설명", example = "관리자 지급: 관리자 테스트 지급")
    private String description;

    @Schema(description = "거래 일시", example = "2025-07-19T23:20:32.032983")
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