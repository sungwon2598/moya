package com.study.moya.token.dto.charge;

import com.study.moya.token.domain.TokenPackage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPackageDto {
    private Long id;
    private String packageName;
    private Long tokenAmount;
    private Long price;
    private String currency;
    private Boolean isActive;

    public static TokenPackageDto from(TokenPackage tokenPackage) {
        return TokenPackageDto.builder()
                .id(tokenPackage.getId())
                .packageName(tokenPackage.getPackageName())
                .tokenAmount(tokenPackage.getTokenAmount())
                .price(tokenPackage.getPrice())
                .currency(tokenPackage.getCurrency())
                .isActive(tokenPackage.getIsActive())
                .build();
    }
}