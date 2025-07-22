package com.study.moya.token.dto.ticket;

import com.study.moya.token.domain.ticket.TicketAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "티켓 계정 정보")
public class TicketAccountDto {

    @Schema(description = "티켓 계정 ID", example = "1")
    private Long id;

    @Schema(description = "회원 ID", example = "33")
    private Long memberId;

    @Schema(description = "로드맵 티켓 소지 개수", example = "5")
    private Long roadmapTicketBalance;

    @Schema(description = "워크시트 티켓 소지 개수", example = "0")
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