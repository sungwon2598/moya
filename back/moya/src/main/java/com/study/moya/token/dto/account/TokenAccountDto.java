package com.study.moya.token.dto.account;

import com.study.moya.token.domain.TokenAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenAccountDto {
    private Long id;
    private Long memberId;
    private Long balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TokenAccountDto from(TokenAccount tokenAccount) {
        return TokenAccountDto.builder()
                .id(tokenAccount.getId())
                .memberId(tokenAccount.getMember().getId())
                .balance(tokenAccount.getBalance())
                .createdAt(tokenAccount.getCreatedAt())
                .updatedAt(tokenAccount.getModifiedAt())
                .build();
    }
}