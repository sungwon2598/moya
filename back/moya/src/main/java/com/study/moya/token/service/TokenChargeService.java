package com.study.moya.token.service;

import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.token.domain.enums.PaymentMethod;
import com.study.moya.token.domain.enums.PaymentStatus;
import com.study.moya.token.domain.Payment;
import com.study.moya.token.domain.TokenAccount;
import com.study.moya.token.domain.TokenPackage;
import com.study.moya.token.dto.charge.ChargeTokenRequest;
import com.study.moya.token.dto.charge.PaymentConfirmRequest;
import com.study.moya.token.dto.charge.PaymentResponse;
import com.study.moya.token.dto.charge.TokenPackageDto;
import com.study.moya.token.dto.charge.TokenPackageResponse;
import com.study.moya.token.exception.TokenErrorCode;
import com.study.moya.token.exception.TokenException;
import com.study.moya.token.repository.PaymentRepository;
import com.study.moya.token.repository.TokenAccountRepository;
import com.study.moya.token.repository.TokenPackageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 토큰 충전 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenChargeService {

    private final MemberRepository memberRepository;
    private final TokenAccountRepository tokenAccountRepository;
    private final TokenPackageRepository tokenPackageRepository;
    private final PaymentRepository paymentRepository;
    private final TokenAccountService tokenAccountService;
    private final TokenTransactionService tokenTransactionService;

    /**
     * 사용 가능한 토큰 패키지 목록 조회
     */
    public TokenPackageResponse getAvailableTokenPackages() {
        List<TokenPackage> packages = tokenPackageRepository.findByIsActiveTrueOrderByPriceAsc();

        return TokenPackageResponse.builder()
                .availablePackages(packages.stream()
                        .map(TokenPackageDto::from)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 토큰 충전 요청
     */
    @Transactional
    public PaymentResponse requestTokenCharge(Long memberId, ChargeTokenRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> TokenException.of(TokenErrorCode.MEMBER_NOT_FOUND));

        TokenPackage tokenPackage = tokenPackageRepository.findById(request.getTokenPackageId())
                .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_PACKAGE_NOT_FOUND));

        if (!tokenPackage.getIsActive()) {
            throw TokenException.of(TokenErrorCode.TOKEN_PACKAGE_NOT_ACTIVE);
        }

        // 주문 ID 생성
        String orderId = UUID.randomUUID().toString();

        // 결제 정보 생성 (Builder 패턴 사용)
        Payment payment = Payment.builder()
                .member(member)
                .orderId(orderId)
                .amount(tokenPackage.getPrice())
                .currency(tokenPackage.getCurrency())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .paymentStatus(PaymentStatus.PENDING)
                .tokenAmount(tokenPackage.getTokenAmount())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .orderId(savedPayment.getOrderId())
                .paymentStatus(savedPayment.getPaymentStatus().name())
                .tokenAmount(savedPayment.getTokenAmount())
                .build();
    }

    /**
     * 결제 확인 및 토큰 충전 처리
     */
    @Transactional
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> TokenException.of(TokenErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw TokenException.of(TokenErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        PaymentStatus newStatus = PaymentStatus.valueOf(request.getPaymentStatus());
        payment.updatePaymentStatus(newStatus);

        if (newStatus == PaymentStatus.COMPLETED) {
            payment.completePayment();

            // 토큰 계정에 토큰 추가
            TokenAccount tokenAccount = tokenAccountRepository.findByMember(payment.getMember())
                    .orElseThrow(() -> TokenException.of(TokenErrorCode.TOKEN_ACCOUNT_NOT_FOUND));

            // 토큰 계정 업데이트
            Long newBalance = tokenAccountService.addTokens(tokenAccount.getId(), payment.getTokenAmount());

            // 트랜잭션 기록
            tokenTransactionService.createChargeTransaction(tokenAccount.getId(), payment, payment.getTokenAmount());

            log.info("토큰 충전 완료: 회원 ID {}, 주문 ID {}, 충전 토큰 {}, 새 잔액 {}",
                    payment.getMember().getId(), payment.getOrderId(), payment.getTokenAmount(), newBalance);
        }

        Payment updatedPayment = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .orderId(updatedPayment.getOrderId())
                .paymentStatus(updatedPayment.getPaymentStatus().name())
                .tokenAmount(updatedPayment.getTokenAmount())
                .build();
    }
}