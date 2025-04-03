package com.study.moya.token.service;

import com.study.moya.token.domain.AiUsage;
import com.study.moya.token.domain.Payment;
import com.study.moya.token.domain.TokenAccount;
import com.study.moya.token.domain.TokenTransaction;
import com.study.moya.token.domain.enums.PaymentMethod;
import com.study.moya.token.domain.enums.TransactionType;
import com.study.moya.token.dto.transaction.TokenTransactionDto;
import com.study.moya.token.exception.TokenErrorCode;
import com.study.moya.token.exception.TokenException;
import com.study.moya.token.repository.TokenAccountRepository;
import com.study.moya.token.repository.TokenTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenTransactionService {

    private final TokenTransactionRepository tokenTransactionRepository;
    private final TokenAccountRepository tokenAccountRepository;

    @Transactional
    public TokenTransaction createChargeTransaction(Long tokenAccountId, Payment payment, Long amount) {
        TokenAccount tokenAccount = tokenAccountRepository.findById(tokenAccountId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_ACCOUNT_NOT_FOUND));

        TokenTransaction transaction = TokenTransaction.builder()
                .tokenAccount(tokenAccount)
                .payment(payment)
                .amount(amount)
                .transactionType(TransactionType.CHARGE)
                .paymentMethod(payment.getPaymentMethod()) // 결제 방법 추가
                .balanceAfter(tokenAccount.getBalance())
                .description("토큰 충전: " + payment.getPaymentMethod().name() + " 결제")
                .createdBy("SYSTEM")
                .build();

        return tokenTransactionRepository.save(transaction);
    }

    @Transactional
    public TokenTransaction createUsageTransaction(Long tokenAccountId, AiUsage aiUsage, Long amount) {
        TokenAccount tokenAccount = tokenAccountRepository.findById(tokenAccountId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_ACCOUNT_NOT_FOUND));

        TokenTransaction transaction = TokenTransaction.builder()
                .tokenAccount(tokenAccount)
                .aiUsage(aiUsage)
                .amount(amount)
                .transactionType(TransactionType.USAGE)
                .paymentMethod(null) // AI 사용에는 결제 방법이 없음
                .balanceAfter(tokenAccount.getBalance())
                .description("AI 서비스 사용: " + aiUsage.getAiService().getServiceName())
                .createdBy("SYSTEM")
                .build();

        return tokenTransactionRepository.save(transaction);
    }

    @Transactional
    public TokenTransaction createRefundTransaction(Long tokenAccountId, AiUsage aiUsage, Long amount) {
        TokenAccount tokenAccount = tokenAccountRepository.findById(tokenAccountId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_ACCOUNT_NOT_FOUND));

        TokenTransaction transaction = TokenTransaction.builder()
                .tokenAccount(tokenAccount)
                .aiUsage(aiUsage)
                .amount(amount)
                .transactionType(TransactionType.REFUND)
                .paymentMethod(null) // 환불에는 결제 방법이 없음
                .balanceAfter(tokenAccount.getBalance())
                .description("AI 서비스 사용 취소 환불: " + aiUsage.getAiService().getServiceName())
                .createdBy("SYSTEM")
                .build();

        return tokenTransactionRepository.save(transaction);
    }

    @Transactional
    public TokenTransaction createAdminTransaction(Long tokenAccountId, Long amount, String description, PaymentMethod paymentMethod) {
        TokenAccount tokenAccount = tokenAccountRepository.findById(tokenAccountId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_ACCOUNT_NOT_FOUND));

        TransactionType type = amount >= 0 ? TransactionType.ADMIN_CHARGE : TransactionType.ADMIN_DEDUCT;

        TokenTransaction transaction = TokenTransaction.builder()
                .tokenAccount(tokenAccount)
                .amount(Math.abs(amount))
                .transactionType(type)
                .paymentMethod(paymentMethod) // 관리자 트랜잭션에도 결제 방법 설정 가능
                .balanceAfter(tokenAccount.getBalance())
                .description(description)
                .createdBy("ADMIN")
                .build();

        return tokenTransactionRepository.save(transaction);
    }

    @Transactional
    public TokenTransaction createCouponTransaction(Long tokenAccountId, Long amount,String description) {
        TokenAccount tokenAccount = tokenAccountRepository.findById(tokenAccountId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_ACCOUNT_NOT_FOUND));

        TokenTransaction transaction = TokenTransaction.builder()
                .tokenAccount(tokenAccount)
                .amount(amount)
                .transactionType(TransactionType.REFUND)
                .paymentMethod(PaymentMethod.COUPON) // 환불에는 결제 방법이 없음
                .balanceAfter(tokenAccount.getBalance())
                .description(description)
                .createdBy("SYSTEM")
                .build();

        return tokenTransactionRepository.save(transaction);
    }

    public Page<TokenTransactionDto> getMemberTransactions(Long tokenAccountId, Pageable pageable) {
        return tokenTransactionRepository.findByTokenAccountIdOrderByCreatedAtDesc(tokenAccountId, pageable)
                .map(TokenTransactionDto::from);
    }

    public List<TokenTransactionDto> getMemberTransactionsBetween(Long tokenAccountId, LocalDateTime start, LocalDateTime end) {
        return tokenTransactionRepository.findByTokenAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                        tokenAccountId, start, end)
                .stream()
                .map(TokenTransactionDto::from)
                .collect(Collectors.toList());
    }
}