package com.study.moya.token.dto.account;

import com.study.moya.token.dto.transaction.TokenTransactionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenBalanceResponse {
    private Long balance;
    private List<TokenTransactionDto> recentTransactions;
}