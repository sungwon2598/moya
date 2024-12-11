//package com.study.moya.member.cu;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class CouponLoadTest {
//    @Autowired
//    private SimpleCouponService couponService;
//    @Autowired
//    private CouponRepository couponRepository;
//
//    @BeforeEach
//    void setUp() {
//        // 테스트용 쿠폰 생성 (100개)
//        Coupon coupon = Coupon.builder()
//                .name("테스트 쿠폰")
//                .quantity(100)
//                .build();
//        couponRepository.save(coupon);
//    }
//
//    @Test
//    void loadTest() throws InterruptedException {
//        int threadCount = 200; // 동시 요청 수
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//        AtomicInteger successCount = new AtomicInteger();
//
//        for (int i = 0; i < threadCount; i++) {
//            long userId = i;
//            executorService.submit(() -> {
//                try {
//                    boolean result = couponService.issueCoupon(1L, userId);
//                    if (result) {
//                        successCount.incrementAndGet();
//                    }
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//        System.out.println("성공 발급 건수: " + successCount.get());
//        System.out.println("실제 발급 건수: " + couponRepository.findById(1L).get().getIssuedQuantity());
//    }
//}