package com.study.moya.admin.dto.ticket.request;

import com.study.moya.token.domain.enums.TicketType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GiveTicketRequest {
    private Long memberId;
    private TicketType ticketType;
    private Long amount;
    private String reason;
}
