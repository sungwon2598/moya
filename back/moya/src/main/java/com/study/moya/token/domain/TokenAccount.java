package com.study.moya.token.domain;

import com.study.moya.BaseEntity;
import com.study.moya.member.domain.Member;
import com.study.moya.token.exception.TokenErrorCode;
import com.study.moya.token.exception.TokenException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "token_accounts")
public class TokenAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false)
    private Long balance;

    @Version
    private Long version;  // 낙관적 락을 통한 동시성 제어

    @Builder
    public TokenAccount(Member member, Long balance) {
        this.member = member;
        this.balance = balance;
    }

    // 생성자, 메서드 등
    public void addTokens(Long amount) {
        this.balance += amount;
    }

    // 예외 처리 없이 값만 변경
    public void useTokens(Long amount) {
        this.balance -= amount;
    }

    // 토큰 잔액 조회 메서드 추가
    public boolean hasEnoughTokens(Long amount) {
        return this.balance >= amount;
    }
}