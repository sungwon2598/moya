package com.study.moya.token.dto.ticket;

import com.study.moya.token.domain.ticket.TicketUsage;
import com.study.moya.token.domain.enums.AiServiceType;
import com.study.moya.token.domain.enums.AiUsageStatus;
import com.study.moya.token.domain.enums.TicketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketUsageDto {
    private Long id;
    private Long memberId;
    private AiServiceType serviceType;
    private TicketType ticketType;
    private AiUsageStatus status;
    private LocalDateTime createdAt;

    public static TicketUsageDto from(TicketUsage ticketUsage) {
        return TicketUsageDto.builder()
                .id(ticketUsage.getId())
                .memberId(ticketUsage.getMember().getId())
                .serviceType(ticketUsage.getServiceType())
                .ticketType(ticketUsage.getTicketType())
                .status(ticketUsage.getStatus())
                .createdAt(ticketUsage.getCreatedAt())
                .build();
    }
}