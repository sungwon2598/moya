package com.study.moya.token.dto.ticket.reponse;

import com.study.moya.token.domain.ticket.TicketAccount;
import com.study.moya.token.dto.ticket.TicketTransactionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketBalanceResponse {
    private Long roadmapTicketBalance;
    private Long worksheetTicketBalance;
    private Long totalTicketBalance;
    private List<TicketTransactionDto> recentTransactions;

    public static TicketBalanceResponse from(TicketAccount ticketAccount, List<TicketTransactionDto> recentTransactions) {
        return TicketBalanceResponse.builder()
                .roadmapTicketBalance(ticketAccount.getRoadmapTicketBalance())
                .worksheetTicketBalance(ticketAccount.getWorksheetTicketBalance())
                .totalTicketBalance(ticketAccount.getRoadmapTicketBalance() + ticketAccount.getWorksheetTicketBalance())
                .recentTransactions(recentTransactions)
                .build();
    }
}