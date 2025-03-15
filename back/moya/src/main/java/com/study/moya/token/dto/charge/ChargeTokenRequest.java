package com.study.moya.token.dto.charge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeTokenRequest {
    private Long tokenPackageId;
    private String paymentMethod;
}