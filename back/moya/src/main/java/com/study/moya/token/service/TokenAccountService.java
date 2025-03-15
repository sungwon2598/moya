package com.study.moya.token.service;

import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.token.domain.TokenAccount;
import com.study.moya.token.dto.account.TokenAccountDto;
import com.study.moya.token.dto.account.TokenBalanceResponse;
import com.study.moya.token.dto.transaction.TokenTransactionDto;
import com.study.moya.token.exception.TokenErrorCode;
import com.study.moya.token.exception.TokenException;
import com.study.moya.token.repository.TokenAccountRepository;
import com.study.moya.token.repository.TokenTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 토큰 계정 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenAccountService {

    private final MemberRepository memberRepository;
    private final TokenAccountRepository tokenAccountRepository;
    private final TokenTransactionRepository tokenTransactionRepository;

    /**
     * 회원의 토큰 계정을 조회하거나 없으면 생성합니다.
     */
    @Transactional
    public TokenAccountDto getOrCreateTokenAccount(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.MEMBER_NOT_FOUND));

        TokenAccount tokenAccount = tokenAccountRepository.findByMember(member)
                .orElseGet(() -> {
                    // 빌더 패턴을 사용하여 TokenAccount 객체 생성
                    TokenAccount newAccount = TokenAccount.builder()
                            .member(member)
                            .balance(0L)  // 초기 잔액 0으로 설정
                            .build();

                    return tokenAccountRepository.save(newAccount);
                });

        return TokenAccountDto.from(tokenAccount);
    }

    /**
     * 회원의 토큰 잔액 및 최근 거래 내역을 조회합니다.
     */
    public TokenBalanceResponse getTokenBalance(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.MEMBER_NOT_FOUND));

        TokenAccount tokenAccount = tokenAccountRepository.findByMember(member)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_ACCOUNT_NOT_FOUND));

        List<TokenTransactionDto> recentTransactions = tokenTransactionRepository
                .findByTokenAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                        tokenAccount.getId(),
                        LocalDateTime.now().minusDays(30),
                        LocalDateTime.now()
                )
                .stream()
                .map(TokenTransactionDto::from)
                .collect(Collectors.toList());

        return TokenBalanceResponse.builder()
                .balance(tokenAccount.getBalance())
                .recentTransactions(recentTransactions)
                .build();
    }

    /**
     * 회원의 토큰 잔액 업데이트 (충전)
     */
    @Transactional
    public Long addTokens(Long accountId, Long amount) {
        TokenAccount tokenAccount = tokenAccountRepository.findById(accountId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_ACCOUNT_NOT_FOUND));

        tokenAccount.addTokens(amount);
        TokenAccount savedAccount = tokenAccountRepository.save(tokenAccount);

        return savedAccount.getBalance();
    }

    /**
     * 회원의 토큰 잔액 업데이트 (사용)
     */
    @Transactional
    public Long useTokens(Long accountId, Long amount) {
        TokenAccount tokenAccount = tokenAccountRepository.findById(accountId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_ACCOUNT_NOT_FOUND));

        // 서비스 계층에서 잔액 검증 수행
        if (!tokenAccount.hasEnoughTokens(amount)) {
            throw TokenException.of(TokenErrorCode.INSUFFICIENT_TOKEN);
        }

        // 검증 후 토큰 사용 처리
        tokenAccount.useTokens(amount);
        TokenAccount savedAccount = tokenAccountRepository.save(tokenAccount);

        return savedAccount.getBalance();
    }
}