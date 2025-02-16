package com.study.moya.coupon.repository;

import com.study.moya.coupon.domain.Coupon;
import com.study.moya.coupon.domain.CouponType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    boolean existsByMemberIdAndCouponType(Long memberId, CouponType couponType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    Optional<Coupon> findByMemberIdAndCouponType(Long memberId, CouponType couponType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    Optional<Coupon> findFirstByMemberIsNullAndCouponTypeAndExpirationDateGreaterThanOrderByIdAsc(CouponType couponType, LocalDateTime currentTime);

    long countByMemberIsNotNull();
    int countByMemberId(Long id);
}
