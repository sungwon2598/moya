package com.study.moya.token.dto.usage;

import com.study.moya.token.domain.AiService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiServiceDto {
    private Long id;
    private String serviceName;
    private String serviceType;
    private Long tokenCost;
    private Boolean isActive;

    public static AiServiceDto from(AiService aiService) {
        return AiServiceDto.builder()
                .id(aiService.getId())
                .serviceName(aiService.getServiceName())
                .serviceType(aiService.getServiceType().name())
                .tokenCost(aiService.getTokenCost())
                .isActive(aiService.getIsActive())
                .build();
    }
}