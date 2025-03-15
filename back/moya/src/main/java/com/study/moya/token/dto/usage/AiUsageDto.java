package com.study.moya.token.dto.usage;

import com.study.moya.token.domain.AiUsage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiUsageDto {
    private Long id;
    private Long memberId;
    private Long serviceId;
    private String serviceName;
    private String requestData;
    private Long tokenCost;
    private String status;
    private LocalDateTime createdAt;

    public static AiUsageDto from(AiUsage aiUsage) {
        return AiUsageDto.builder()
                .id(aiUsage.getId())
                .memberId(aiUsage.getMember().getId())
                .serviceId(aiUsage.getAiService().getId())
                .serviceName(aiUsage.getAiService().getServiceName())
                .requestData(aiUsage.getRequestData())
                .tokenCost(aiUsage.getTokenCost())
                .status(aiUsage.getStatus().name())
                .createdAt(aiUsage.getCreatedAt())
                .build();
    }
}