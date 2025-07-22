package com.study.moya.admin.dto.ticket.request;

import com.study.moya.token.domain.enums.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "티켓 회수 요청")
public class DeductTicketRequest {

    @Schema(description = "회원 ID", example = "33")
    private Long memberId;

    @Schema(description = "티켓 타입", example = "WORKSHEET_TICKET")
    private TicketType ticketType;

    @Schema(description = "회수할 티켓 수량", example = "3")
    private Long amount;

    @Schema(description = "회수 사유", example = "부적절한 사용으로 인한 회수")
    private String reason;
}