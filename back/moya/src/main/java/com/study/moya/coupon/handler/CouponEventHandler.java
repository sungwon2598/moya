package com.study.moya.coupon.handler;

import com.study.moya.coupon.domain.CouponType;
import com.study.moya.coupon.service.CouponService;
import com.study.moya.member.event.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventHandler {

    private final CouponService couponService;

    @EventListener
    public void handleMemberRegistered(MemberRegisteredEvent event) {
        log.info("회원가입 이벤트 수신 - 웰컴 쿠폰 발급 시작, 회원ID: {}", event.memberId());

        try {
            couponService.createAndIssueCoupon(
                    event.memberId(),
                    CouponType.WELCOME,
                    LocalDateTime.now().plusDays(30),
                    10000L
            );
            log.info("웰컴 쿠폰 발급 완료 - 회원ID: {}", event.memberId());
        } catch (Exception e) {
            log.error("웰컴 쿠폰 발급 실패 - 회원ID: {}, 오류: {}", event.memberId(), e.getMessage());
            // 실패해도 회원가입에는 영향 없음
        }
    }
}