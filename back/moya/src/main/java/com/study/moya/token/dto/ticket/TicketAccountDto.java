package com.study.moya.token.dto.ticket;

import com.study.moya.token.domain.ticket.TicketAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAccountDto {
    private Long id;
    private Long memberId;
    private Long roadmapTicketBalance;
    private Long worksheetTicketBalance;

    public static TicketAccountDto from(TicketAccount ticketAccount) {
        return TicketAccountDto.builder()
                .id(ticketAccount.getId())
                .memberId(ticketAccount.getMember().getId())
                .roadmapTicketBalance(ticketAccount.getRoadmapTicketBalance())
                .worksheetTicketBalance(ticketAccount.getWorksheetTicketBalance())
                .build();
    }
}