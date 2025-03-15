package com.study.moya.token.service;

import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.token.domain.AiService;
import com.study.moya.token.domain.AiUsage;
import com.study.moya.token.domain.TokenAccount;
import com.study.moya.token.domain.enums.AiServiceType;
import com.study.moya.token.domain.enums.AiUsageStatus;
import com.study.moya.token.dto.usage.AiServiceDto;
import com.study.moya.token.dto.usage.AiServiceResponse;
import com.study.moya.token.dto.usage.AiUsageDto;
import com.study.moya.token.dto.usage.AiUsageResponse;
import com.study.moya.token.dto.usage.UseTokenRequest;
import com.study.moya.token.exception.TokenErrorCode;
import com.study.moya.token.exception.TokenException;
import com.study.moya.token.repository.AiServiceRepository;
import com.study.moya.token.repository.AiUsageRepository;
import com.study.moya.token.repository.TokenAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 서비스 사용 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenUsageService {

    private final MemberRepository memberRepository;
    private final TokenAccountRepository tokenAccountRepository;
    private final AiServiceRepository aiServiceRepository;
    private final AiUsageRepository aiUsageRepository;
    private final TokenAccountService tokenAccountService;
    private final TokenTransactionService tokenTransactionService;

    /**
     * 사용 가능한 AI 서비스 목록 조회
     */
    public AiServiceResponse getAvailableAiServices() {
        List<AiService> services = aiServiceRepository.findByIsActiveTrueOrderByTokenCostAsc();

        return AiServiceResponse.builder()
                .availableServices(services.stream()
                        .map(AiServiceDto::from)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 특정 유형의 사용 가능한 AI 서비스 목록 조회
     */
    public AiServiceResponse getAvailableAiServicesByType(String serviceType) {
        AiServiceType type = AiServiceType.valueOf(serviceType);
        List<AiService> services = aiServiceRepository.findByServiceTypeAndIsActiveTrue(type);

        return AiServiceResponse.builder()
                .availableServices(services.stream()
                        .map(AiServiceDto::from)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * AI 서비스 사용 및 토큰 차감
     */
    @Transactional
    public AiUsageResponse useTokenForAiService(Long memberId, UseTokenRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.MEMBER_NOT_FOUND));

        AiService aiService = aiServiceRepository.findById(request.getAiServiceId())
                .orElseThrow(() -> TokenException.of(TokenErrorCode.AI_SERVICE_NOT_FOUND));

        // 고정 토큰 비용 사용
        Long tokenCost = aiService.getTokenCost();

        // 토큰 계정 조회
        TokenAccount tokenAccount = tokenAccountRepository.findByMember(member)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_ACCOUNT_NOT_FOUND));

        // 잔액 확인
        if (tokenAccount.getBalance() < tokenCost) {
            throw TokenException.of(TokenErrorCode.INSUFFICIENT_TOKEN);
        }

        // 토큰 차감 (낙관적 락 적용)
        Long newBalance = tokenAccountService.useTokens(tokenAccount.getId(), tokenCost);

        // AI 사용 내역 생성
        AiUsage aiUsage = AiUsage.builder()
                .member(member)
                .aiService(aiService)
                .requestData(request.getRequestData())
                .tokenCost(tokenCost)
                .status(AiUsageStatus.PENDING)
                .build();

        AiUsage savedUsage = aiUsageRepository.save(aiUsage);

        // 트랜잭션 기록
        tokenTransactionService.createUsageTransaction(
                tokenAccount.getId(), savedUsage, tokenCost);

        return AiUsageResponse.builder()
                .id(savedUsage.getId())
                .serviceName(aiService.getServiceName())
                .tokenCost(tokenCost)
                .status(savedUsage.getStatus().name())
                .build();
    }

    /**
     * 회원의 AI 사용 내역 조회 (페이징)
     */
    public Page<AiUsageDto> getMemberAiUsages(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.MEMBER_NOT_FOUND));

        return aiUsageRepository.findByMemberOrderByCreatedAtDesc(member, pageable)
                .map(AiUsageDto::from);
    }

    /**
     * 특정 기간 동안의 회원 AI 사용 내역 조회
     */
    public List<AiUsageDto> getMemberAiUsagesBetween(Long memberId, LocalDateTime start, LocalDateTime end) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.MEMBER_NOT_FOUND));

        return aiUsageRepository.findByMemberAndAiServiceAndCreatedAtBetween(member, null, start, end)
                .stream()
                .map(AiUsageDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 대기 중인 AI 사용 내역 처리 (배치 작업)
     */
    @Transactional
    public void processAiUsageResults() {
        List<AiUsage> pendingUsages = aiUsageRepository.findByStatusOrderByCreatedAtAsc(AiUsageStatus.PENDING);

        for (AiUsage usage : pendingUsages) {
            try {
                // 실제로는 여기서 AI 서비스 처리 결과를 확인하는 로직이 필요함
                // 예시로 모든 요청이 성공했다고 가정
                usage.completeUsage();
                aiUsageRepository.save(usage);

                log.info("AI 서비스 처리 완료: {}", usage.getId());
            } catch (Exception e) {
                log.error("AI 서비스 처리 실패: {}", usage.getId(), e);
                usage.failUsage();
                aiUsageRepository.save(usage);

                // 실패한 경우 토큰 환불 처리
                refundTokensForFailedUsage(usage);
            }
        }
    }

    /**
     * 실패한 AI 사용에 대한 토큰 환불 처리
     */
    @Transactional
    private void refundTokensForFailedUsage(AiUsage usage) {
        TokenAccount tokenAccount = tokenAccountRepository.findByMember(usage.getMember())
                .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_ACCOUNT_NOT_FOUND));

        // 토큰 환불
        Long newBalance = tokenAccountService.addTokens(tokenAccount.getId(), usage.getTokenCost());

        // 환불 트랜잭션 기록
        tokenTransactionService.createRefundTransaction(tokenAccount.getId(), usage, usage.getTokenCost());

        log.info("토큰 환불 처리 완료: AI 사용 ID {}, 환불 토큰 {}, 새 잔액 {}",
                usage.getId(), usage.getTokenCost(), newBalance);
    }

    /**
     * 실제 사용한 토큰량 확인
     */
    @Transactional
    public void updateActualTokenUsage(Long usageId, Long actualTokenUsage) {
        AiUsage usage = aiUsageRepository.findById(usageId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.AI_USAGE_NOT_FOUND));

        usage.recordActualTokenUsage(actualTokenUsage);
        aiUsageRepository.save(usage);

        log.info("실제 토큰 사용량 업데이트: 사용 ID {}, 고정 비용 {}, 실제 사용 토큰 {}",
                usageId, usage.getTokenCost(), actualTokenUsage);
    }

    /**
     * AI 사용 내역을 완료 상태로 변경
     */
    @Transactional
    public void markAiUsageAsCompleted(Long usageId) {
        AiUsage usage = aiUsageRepository.findById(usageId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.AI_USAGE_NOT_FOUND));

        usage.completeUsage();
        aiUsageRepository.save(usage);

        log.info("AI 서비스 처리 완료 상태로 변경: {}", usage.getId());
    }

    /**
     * AI 사용 내역을 실패 상태로 변경하고 토큰 환불 처리
     */
    @Transactional
    public void markAiUsageAsFailed(Long usageId) {
        AiUsage usage = aiUsageRepository.findById(usageId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.AI_USAGE_NOT_FOUND));

        // 실패 상태로 변경
        usage.failUsage();
        aiUsageRepository.save(usage);

        // 토큰 환불 처리
        refundTokensForFailedUsage(usage);

        log.info("AI 서비스 처리 실패 상태로 변경 및 토큰 환불 처리: {}", usage.getId());
    }
}