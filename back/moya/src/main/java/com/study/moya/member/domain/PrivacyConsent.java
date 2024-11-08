package com.study.moya.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivacyConsent {

    @Column(name = "terms_agreed")
    private Boolean termsAgreed;

    @Column(name = "terms_agreed_at")
    private LocalDateTime termsAgreedAt;

    @Column(name = "privacy_policy_agreed")
    private Boolean privacyPolicyAgreed;

    @Column(name = "privacy_policy_agreed_at")
    private LocalDateTime privacyPolicyAgreedAt;

    @Column(name = "marketing_agreed")
    private Boolean marketingAgreed;

    @Column(name = "marketing_agreed_at")
    private LocalDateTime marketingAgreedAt;

    @Builder
    public PrivacyConsent(Boolean termsAgreed, Boolean privacyPolicyAgreed, Boolean marketingAgreed) {
        LocalDateTime now = LocalDateTime.now();
        this.termsAgreed = termsAgreed;
        this.termsAgreedAt = termsAgreed ? now : null;
        this.privacyPolicyAgreed = privacyPolicyAgreed;
        this.privacyPolicyAgreedAt = privacyPolicyAgreed ? now : null;
        this.marketingAgreed = marketingAgreed;
        this.marketingAgreedAt = marketingAgreed ? now : null;
    }

    public void updateMarketingConsent(Boolean agreed) {
        this.marketingAgreed = agreed;
        this.marketingAgreedAt = agreed ? LocalDateTime.now() : null;
    }
}