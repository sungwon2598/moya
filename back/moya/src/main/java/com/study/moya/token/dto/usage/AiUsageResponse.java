package com.study.moya.token.dto.usage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiUsageResponse {
    private Long id;
    private String serviceName;
    private Long tokenCost;
    private String status;
}