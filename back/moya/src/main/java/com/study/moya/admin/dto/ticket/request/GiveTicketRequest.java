
// 1. GiveTicketRequest.java
package com.study.moya.admin.dto.ticket.request;

import com.study.moya.token.domain.enums.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "티켓 지급 요청")
public class GiveTicketRequest {

    @Schema(description = "회원 ID", example = "33")
    private Long memberId;

    @Schema(description = "티켓 타입", example = "ROADMAP_TICKET")
    private TicketType ticketType;

    @Schema(description = "지급할 티켓 수량", example = "10")
    private Long amount;

    @Schema(description = "지급 사유", example = "이벤트 참여 보상")
    private String reason;
}