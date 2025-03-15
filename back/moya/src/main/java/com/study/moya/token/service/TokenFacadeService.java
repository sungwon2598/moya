package com.study.moya.token.service;

import com.study.moya.token.dto.account.TokenAccountDto;
import com.study.moya.token.dto.account.TokenBalanceResponse;
import com.study.moya.token.dto.charge.ChargeTokenRequest;
import com.study.moya.token.dto.charge.PaymentConfirmRequest;
import com.study.moya.token.dto.charge.PaymentResponse;
import com.study.moya.token.dto.charge.TokenPackageResponse;
import com.study.moya.token.dto.transaction.TokenTransactionDto;
import com.study.moya.token.dto.usage.AiServiceResponse;
import com.study.moya.token.dto.usage.AiUsageDto;
import com.study.moya.token.dto.usage.AiUsageResponse;
import com.study.moya.token.dto.usage.UseTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 토큰 서비스 퍼사드 - 여러 서비스를 통합하여 컨트롤러에 단일 인터페이스 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenFacadeService {

    private final TokenAccountService tokenAccountService;
    private final TokenTransactionService tokenTransactionService;
    private final TokenChargeService tokenChargeService;
    private final TokenUsageService tokenUsageService;

    /**
     * 회원의 토큰 계정을 조회하거나 없으면 생성합니다.
     */
    public TokenAccountDto getOrCreateTokenAccount(Long memberId) {
        return tokenAccountService.getOrCreateTokenAccount(memberId);
    }

    /**
     * 회원의 토큰 잔액 및 최근 거래 내역을 조회합니다.
     */
    public TokenBalanceResponse getTokenBalance(Long memberId) {

        getOrCreateTokenAccount(memberId);
        return tokenAccountService.getTokenBalance(memberId);
    }

    /**
     * 사용 가능한 토큰 패키지 목록 조회
     */
    public TokenPackageResponse getAvailableTokenPackages() {
        return tokenChargeService.getAvailableTokenPackages();
    }

    /**
     * 토큰 충전 요청
     */
    public PaymentResponse requestTokenCharge(Long memberId, ChargeTokenRequest request) {
        return tokenChargeService.requestTokenCharge(memberId, request);
    }

    /**
     * 결제 확인 및 토큰 충전 처리
     */
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        return tokenChargeService.confirmPayment(request);
    }

    /**
     * 사용 가능한 AI 서비스 목록 조회
     */
    public AiServiceResponse getAvailableAiServices() {
        return tokenUsageService.getAvailableAiServices();
    }

    /**
     * 특정 유형의 사용 가능한 AI 서비스 목록 조회
     */
    public AiServiceResponse getAvailableAiServicesByType(String serviceType) {
        return tokenUsageService.getAvailableAiServicesByType(serviceType);
    }

    /**
     * AI 서비스 사용 및 토큰 차감
     */
    public AiUsageResponse useTokenForAiService(Long memberId, UseTokenRequest request) {
        return tokenUsageService.useTokenForAiService(memberId, request);
    }

    /**
     * 회원의 트랜잭션 내역 조회 (페이징)
     */
    public Page<TokenTransactionDto> getMemberTransactions(Long memberId, Pageable pageable) {
        return tokenTransactionService.getMemberTransactions(memberId, pageable);
    }

    /**
     * 특정 기간 동안의 회원 트랜잭션 내역 조회
     */
    public List<TokenTransactionDto> getMemberTransactionsBetween(Long memberId, LocalDateTime start, LocalDateTime end) {
        return tokenTransactionService.getMemberTransactionsBetween(memberId, start, end);
    }

    /**
     * 회원의 AI 사용 내역 조회 (페이징)
     */
    public Page<AiUsageDto> getMemberAiUsages(Long memberId, Pageable pageable) {
        return tokenUsageService.getMemberAiUsages(memberId, pageable);
    }

    /**
     * 특정 기간 동안의 회원 AI 사용 내역 조회
     */
    public List<AiUsageDto> getMemberAiUsagesBetween(Long memberId, LocalDateTime start, LocalDateTime end) {
        return tokenUsageService.getMemberAiUsagesBetween(memberId, start, end);
    }

    /**
     * 대기 중인 AI 사용 내역 처리 (배치 작업)
     */
    public void processAiUsageResults() {
        tokenUsageService.processAiUsageResults();
    }

    /**
     * AI 사용 내역을 완료 상태로 변경
     */
    public void markAiUsageAsCompleted(Long usageId) {
        tokenUsageService.markAiUsageAsCompleted(usageId);
    }

    /**
     * AI 사용 내역을 실패 상태로 변경하고 토큰 환불 처리
     */
    public void markAiUsageAsFailed(Long usageId) {
        tokenUsageService.markAiUsageAsFailed(usageId);
    }

    /**
     * 실제 토큰 사용량 업데이트
     */
    public void updateActualTokenUsage(Long usageId, Long actualTokenUsage) {
        tokenUsageService.updateActualTokenUsage(usageId, actualTokenUsage);
    }
}