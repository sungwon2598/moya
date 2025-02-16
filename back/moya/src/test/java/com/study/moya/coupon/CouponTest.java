package com.study.moya.coupon;

import com.study.moya.coupon.domain.Coupon;
import com.study.moya.coupon.domain.CouponType;
import com.study.moya.coupon.repository.CouponRepository;
import com.study.moya.coupon.service.CouponService;
import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.domain.Role;
import com.study.moya.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class CouponTest {
    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;

    private List<Member> testMembers;
    private static final int TOTAL_TEST_MEMBERS = 1000;

    @BeforeEach
    @Transactional
    void setUp() {
        // 테스트 시작 전 기존 데이터 정리
        couponRepository.deleteAll();
        memberRepository.deleteAll();

        // 테스트용 멤버 200명 생성
        testMembers = new ArrayList<>();
        for (int i = 0; i < TOTAL_TEST_MEMBERS; i++) {
            testMembers.add(memberRepository.save(Member.builder()
                    .email("test" + i + "@example.com")
                    .password("password")
                    .nickname("testUser" + i)
                    .providerId("test_provider_" + i)
                    .roles(new HashSet<>(Arrays.asList(Role.USER)))
                    .status(MemberStatus.ACTIVE)
                    .termsAgreed(true)
                    .privacyPolicyAgreed(true)
                    .marketingAgreed(false)
                    .build()));
        }
    }

    @AfterEach
    @Transactional
    void cleanup() {
        couponRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("낙관적 락: 200명이 동시에 50개의 쿠폰을 선착순으로 발급받기 시도")
    void concurrentCouponIssueWithOptimisticLockTest() throws InterruptedException {
        // given
        int totalCoupons = 200;

        // 쿠폰 생성
        for (int i = 0; i < totalCoupons; i++) {
            couponRepository.save(Coupon.builder()
                    .expirationDate(LocalDateTime.now().plusDays(7))
                    .couponType(CouponType.WELCOME)
                    .build());
        }

        log.info("테스트 시작 - 생성된 멤버 수: {}, 생성된 쿠폰 수: {}",
                memberRepository.count(), couponRepository.count());

        int threadCount = TOTAL_TEST_MEMBERS;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    couponService.issueCoupon(testMembers.get(index).getId(), CouponType.WELCOME);
                    log.info("쿠폰 발급 성공 - 멤버 ID: {}", testMembers.get(index).getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.info("쿠폰 발급 실패 - 멤버 ID: {}, 에러: {}",
                            testMembers.get(index).getId(), e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // then
        latch.await();
        executorService.shutdown();

        Long issuedCouponsCount = couponRepository.countByMemberIsNotNull();
        log.info("테스트 결과 - 발급된 쿠폰 수: {}, 성공: {}, 실패: {}",
                issuedCouponsCount, successCount.get(), failCount.get());

        // 검증
        assertThat(successCount.get()).isEqualTo(totalCoupons);
        assertThat(issuedCouponsCount).isEqualTo(totalCoupons);

        // 중복 발급 검증
        long membersWithMultipleCoupons = testMembers.stream()
                .filter(member -> couponRepository.countByMemberId(member.getId()) > 1)
                .count();
        assertThat(membersWithMultipleCoupons).isZero();
    }

    @Test
    @DisplayName("낙관적 락: 200명의 사용자가 동시에 쿠폰 사용 시도시 단 한명만 성공해야 한다")
    void concurrentCouponUseWithOptimisticLockTest() throws InterruptedException {
        // given
        Member testMember = testMembers.get(0);
        Coupon coupon = couponRepository.save(Coupon.builder()
                .expirationDate(LocalDateTime.now().plusDays(7))
                .couponType(CouponType.WELCOME)
                .member(testMember)
                .build());

        log.info("테스트 시작 - 쿠폰 ID: {}, 할당된 멤버 ID: {}",
                coupon.getId(), testMember.getId());

        int threadCount = TOTAL_TEST_MEMBERS;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    couponService.useCoupon(testMember.getId(), CouponType.WELCOME);
                    log.info("쿠폰 사용 성공 - 멤버 ID: {}", testMember.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.info("쿠폰 사용 실패 - 멤버 ID: {}, 에러: {}",
                            testMember.getId(), e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // then
        latch.await();
        executorService.shutdown();

        log.info("테스트 완료 - 성공: {}, 실패: {}",
                successCount.get(), failCount.get());

        // 검증
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);

        // DB 상태 확인
        Coupon finalCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertThat(finalCoupon.isUsed()).isTrue();
    }
}