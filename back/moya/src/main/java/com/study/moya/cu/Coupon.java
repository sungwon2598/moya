package com.study.moya.cu;

import com.study.moya.BaseEntity;
import com.study.moya.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime expirationDate;

    private boolean isUsed;

    private LocalDateTime usedAt;

    @Builder
    private Coupon(Member member, LocalDateTime expirationDate) {
        this.member = member;
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

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }
}