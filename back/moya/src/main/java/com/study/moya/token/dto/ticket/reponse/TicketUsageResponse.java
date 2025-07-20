package com.study.moya.token.dto.ticket.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketUsageResponse {
    private Long id;  // TicketUsage ID (사용 내역 추적용)
}