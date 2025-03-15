package com.study.moya.token.dto.charge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPackageResponse {
    private List<TokenPackageDto> availablePackages;
}