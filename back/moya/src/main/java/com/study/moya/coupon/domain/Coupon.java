package com.study.moya.coupon.domain;

import com.study.moya.BaseEntity;
import com.study.moya.member.domain.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    private LocalDateTime expirationDate;

    private boolean isUsed;

    private LocalDateTime usedAt;

    @Builder
    private Coupon(Member member, CouponType couponType, LocalDateTime expirationDate) {
        this.member = member;
        this.couponType = couponType;
        this.expirationDate = expirationDate;
        this.isUsed = false;
    }

    public void use() {
        validateExpiration();
        validateAlreadyUsed();

        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }

    private void validateExpiration() {
        if (LocalDateTime.now().isAfter(expirationDate)) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }
    }

    private void validateAlreadyUsed() {
        if (isUsed) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
    }
    public void assignMember(Member member) {
        validateNotAssigned();
        this.member = member;
    }

    private void validateNotAssigned() {
        if (this.member != null) {
            throw new IllegalStateException("이미 할당된 쿠폰입니다.");
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }

    public int getDiscountPercent() {
        return this.couponType.getTokenAmount();
    }
}