package com.study.moya.coupon.service;

import com.study.moya.coupon.domain.Coupon;
import com.study.moya.coupon.domain.CouponType;
import com.study.moya.coupon.dto.CouponResponse;
import com.study.moya.coupon.exception.CouponErrorCode;
import com.study.moya.coupon.exception.CouponException;
import com.study.moya.coupon.repository.CouponRepository;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.token.domain.enums.PaymentMethod;
import com.study.moya.token.domain.enums.TransactionType;
import com.study.moya.token.dto.account.TokenAccountDto;
import com.study.moya.token.service.TokenAccountService;
import com.study.moya.token.service.TokenTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private final TokenAccountService tokenAccountService;
    private final TokenTransactionService tokenTransactionService;

    /**
     * 이미 발급된 쿠폰을 사용하는 기능
     */
    @Transactional
    public void useCoupon(Long memberId,  CouponType couponType) {
        log.info("인증된 사용자의 쿠폰 사용 시도 - 멤버ID: {}, 쿠폰타입: {}", memberId, couponType);

        getMember(memberId);

        Coupon coupon = couponRepository.findByMemberIdAndCouponType(memberId, couponType)
                .orElseThrow(() -> CouponException.of(CouponErrorCode.COUPON_NOT_FOUND));
        try {
            coupon.use();

            TokenAccountDto tokenAccountDto = tokenAccountService.getOrCreateTokenAccount(memberId);

            Long newBalance = tokenAccountService.addTokens(tokenAccountDto.getId(), coupon.getBalance());

            tokenTransactionService.createCouponTransaction(
                    tokenAccountDto.getId(),
                    coupon.getBalance(),
                    "쿠폰(" + couponType + ") 사용으로 토큰 충전"
            );

            log.info("쿠폰 사용 및 토큰 충전 완료 - 멤버ID: {}, 쿠폰ID: {}, 충전된 토큰: {}, 새 잔액: {}",
                    memberId, coupon.getId(), coupon.getBalance(), newBalance);
        } catch (IllegalStateException e) {
            handleCouponUsageException(e);
        }
    }

    /**
     * 새로운 쿠폰을 사용자에게 발급
     */
    @Transactional
    public void issueCoupon(Long memberId, CouponType couponType) {
        log.info("쿠폰 발급 시도 - 멤버ID: {}, 쿠폰타입: {}", memberId, couponType);

        if (couponRepository.existsByMemberIdAndCouponType(memberId, couponType)) {
            throw CouponException.of(CouponErrorCode.ALREADY_USED_COUPON);
        }

        Member member = getMember(memberId);

        Coupon coupon = couponRepository
                .findFirstByMemberIsNullAndCouponTypeAndExpirationDateGreaterThanOrderByIdAsc(
                        couponType, LocalDateTime.now())
                .orElseThrow(() -> CouponException.of(CouponErrorCode.NO_COUPON_AVAILABLE));

        coupon.assignMember(member);
        log.info("쿠폰 발급 성공 - 멤버ID: {}, 쿠폰ID: {}", memberId, coupon.getId());
    }

    /**
     * 관리자용 쿠폰 생성 메소드
     */
    @Transactional
    public CouponResponse createCoupons(int count, CouponType couponType, LocalDateTime expirationDate, Long balance) {
        log.info("새로운 {} 쿠폰 {} 개 생성 시도, 토큰 충전액: {}", couponType, count, balance);

        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw CouponException.of(CouponErrorCode.INVALID_EXPIRATION_DATE);
        }
        List<Coupon> coupons = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            coupons.add(Coupon.builder()
                    .couponType(couponType)
                    .expirationDate(LocalDateTime.now().plusDays(30))
                    .balance(balance)
                    .build());
        }
        List<Coupon> savedCoupons = couponRepository.saveAll(coupons);
        log.info("새로운 {} 쿠폰 {} 개 생성 완료, 토큰 충전액: {}", couponType, count, balance);
        return CouponResponse.from(savedCoupons.get(0));
    }

    /**
     * 쿠폰 사용 중 발생하는 예외를 처리하는 내부 메서드
     */
    private void handleCouponUsageException(IllegalStateException e) {
        if (e.getMessage().contains("만료된")) {
            throw CouponException.of(CouponErrorCode.EXPIRED_COUPON);
        } else if (e.getMessage().contains("이미 사용된")) {
            throw CouponException.of(CouponErrorCode.ALREADY_USED_COUPON);
        }
        throw e;
    }

    /**
     * 회원 조회
     */
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> CouponException.of(CouponErrorCode.MEMBER_NOT_FOUND));
    }
}
