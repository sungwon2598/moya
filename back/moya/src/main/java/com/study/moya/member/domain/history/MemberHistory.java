package com.study.moya.member.domain.history;

import com.study.moya.BaseEntity;
import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "member_histories",
        indexes = {
                @Index(name = "idx_member_histories_member_id", columnList = "memberId"),
                @Index(name = "idx_member_histories_created_at", columnList = "createdAt")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private HistoryType historyType;

    @Column(length = 100)
    private String email;

    @Column(length = 30)
    private String nickname;

    @Column(length = 200)
    private String profileImageUrl;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Column
    private Integer pointBalance;

    @Column(name = "terms_agreed")
    private Boolean termsAgreed;

    @Column(name = "privacy_policy_agreed")
    private Boolean privacyPolicyAgreed;

    @Column(name = "marketing_agreed")
    private Boolean marketingAgreed;

    @Getter
    @RequiredArgsConstructor
    public enum HistoryType {
        CREATED("생성"),
        UPDATED("일반 수정"),
        PROFILE_UPDATED("프로필 수정"),
        EMAIL_UPDATED("이메일 수정"),
        PRIVACY_UPDATED("개인정보 동의 변경"),
        POINT_UPDATED("포인트 변경"),
        ACTIVATED("계정 활성화"),
        DORMANT("휴면 전환"),
        BLOCKED("계정 차단"),
        WITHDRAWN("회원 탈퇴");

        private final String description;
    }

    @Builder
    public MemberHistory(Long memberId, HistoryType historyType, String email,
                         String nickname, String profileImageUrl, MemberStatus status,
                         Integer pointBalance, Boolean termsAgreed,
                         Boolean privacyPolicyAgreed, Boolean marketingAgreed) {
        this.memberId = memberId;
        this.historyType = historyType;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.pointBalance = pointBalance;
        this.termsAgreed = termsAgreed;
        this.privacyPolicyAgreed = privacyPolicyAgreed;
        this.marketingAgreed = marketingAgreed;
    }

    public static MemberHistory from(Member member, HistoryType historyType) {
        return MemberHistory.builder()
                .memberId(member.getId())
                .historyType(historyType)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .status(member.getStatus())
                //.pointBalance(member.getPointAccount() != null ? member.getPointAccount().getBalance() : 0)
                .termsAgreed(member.getPrivacyConsent() != null ? member.getPrivacyConsent().getTermsAgreed() : null)
                .privacyPolicyAgreed(member.getPrivacyConsent() != null ? member.getPrivacyConsent().getPrivacyPolicyAgreed() : null)
                .marketingAgreed(member.getPrivacyConsent() != null ? member.getPrivacyConsent().getMarketingAgreed() : null)
                .build();
    }
}