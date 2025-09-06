package com.study.moya.admin.service;

import com.study.moya.coupon.domain.Coupon;
import com.study.moya.coupon.domain.CouponType;
import com.study.moya.coupon.dto.CouponResponse;
import com.study.moya.coupon.exception.CouponErrorCode;
import com.study.moya.coupon.exception.CouponException;
import com.study.moya.coupon.repository.CouponRepository;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCouponService {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    /**
     * 관리자가 특정 사용자에게 직접 쿠폰 발급
     */
    @Transactional
    public void issueCouponToMember(Long targetMemberId, CouponType couponType,
                                    LocalDateTime expirationDate, Long balance, String reason) {
        log.info("관리자 쿠폰 발급 시작 - 대상 멤버ID: {}, 쿠폰타입: {}, 사유: {}",
                targetMemberId, couponType, reason);

        // 대상 회원 존재 확인
        Member targetMember = getMember(targetMemberId);

        // 중복 발급 체크
        if (couponRepository.existsByMemberIdAndCouponType(targetMemberId, couponType)) {
            throw CouponException.of(CouponErrorCode.ALREADY_USED_COUPON);
        }

        // 만료일 검증
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw CouponException.of(CouponErrorCode.INVALID_EXPIRATION_DATE);
        }

        // 쿠폰 즉시 생성 및 할당
        Coupon coupon = Coupon.builder()
                .member(targetMember)
                .couponType(couponType)
                .expirationDate(expirationDate)
                .balance(balance)
                .build();

        couponRepository.save(coupon);
        log.info("관리자 쿠폰 발급 완료 - 멤버ID: {}, 쿠폰ID: {}, 사유: {}",
                targetMemberId, coupon.getId(), reason);
    }

    /**
     * 관리자용 쿠폰 일괄 생성 (기존 CouponService에서 이동)
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
     * 쿠폰 풀 현황 조회 (향후 구현 예정)
     */
    @Transactional(readOnly = true)
    public void getCouponPoolStatus() {
        // TODO: 쿠폰 풀 현황 조회 기능 구현 예정
        // 각 쿠폰 타입별 총 개수, 미할당, 할당됨, 사용됨, 만료됨 통계
        log.info("쿠폰 풀 현황 조회 기능 - 구현 예정");
    }

    /**
     * 만료된 쿠폰 정리 (향후 구현 예정)
     */
    @Transactional
    public void cleanupExpiredCoupons() {
        // TODO: 만료되고 미할당된 쿠폰들 삭제 기능 구현 예정
        log.info("만료된 쿠폰 정리 기능 - 구현 예정");
    }

    /**
     * 회원 조회
     */
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> CouponException.of(CouponErrorCode.MEMBER_NOT_FOUND));
    }
}
