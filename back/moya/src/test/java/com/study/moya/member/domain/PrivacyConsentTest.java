//package com.study.moya.member.domain;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.time.LocalDateTime;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//class PrivacyConsentTest {
//
//    @Test
//    @DisplayName("마케팅 동의 상태 변경 시 시간이 함께 변경된다")
//    void updateMarketingConsent() {
//        // Given
//        PrivacyConsent consent = PrivacyConsent.builder()
//                .termsAgreed(true)
//                .privacyPolicyAgreed(true)
//                .marketingAgreed(false)
//                .build();
//
//        LocalDateTime beforeUpdate = LocalDateTime.now();
//
//        // When - 동의로 변경
//        consent.updateMarketingConsent(true);
//
//        // Then
//        assertThat(consent.getMarketingAgreed()).isTrue();
//        assertThat(consent.getMarketingAgreedAt())
//                .isNotNull()
//                .isAfterOrEqualTo(beforeUpdate)
//                .isBeforeOrEqualTo(LocalDateTime.now());
//
//        // When - 미동의로 변경
//        consent.updateMarketingConsent(false);
//
//        // Then
//        assertThat(consent.getMarketingAgreed()).isFalse();
//        assertThat(consent.getMarketingAgreedAt()).isNull();
//    }
//
//    @Test
//    @DisplayName("마케팅 동의 상태가 변경될 때마다 시간이 갱신된다")
//    void updateMarketingConsentMultipleTimes() {
//        // Given
//        PrivacyConsent consent = PrivacyConsent.builder()
//                .termsAgreed(true)
//                .privacyPolicyAgreed(true)
//                .marketingAgreed(true)  // 처음부터 동의 상태
//                .build();
//
//        LocalDateTime firstAgreedAt = consent.getMarketingAgreedAt();
//
//        // 잠시 대기
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            // 무시
//        }
//
//        // When
//        consent.updateMarketingConsent(true);  // 동의 상태에서 다시 동의
//
//        // Then
//        assertThat(consent.getMarketingAgreed()).isTrue();
//        assertThat(consent.getMarketingAgreedAt())
//                .isNotNull()
//                .isAfter(firstAgreedAt);  // 시간이 갱신되어야 함
//    }
//
//    @Test
//    @DisplayName("동의 시 동의 시간이 기록된다")
//    void createPrivacyConsentWithAgreement() {
//        // When
//        PrivacyConsent consent = PrivacyConsent.builder()
//                .termsAgreed(true)
//                .privacyPolicyAgreed(true)
//                .marketingAgreed(true)
//                .build();
//
//        // Then
//        LocalDateTime now = LocalDateTime.now();
//
//        assertThat(consent.getTermsAgreed()).isTrue();
//        assertThat(consent.getTermsAgreedAt())
//                .isNotNull()
//                .isBeforeOrEqualTo(now);
//
//        assertThat(consent.getPrivacyPolicyAgreed()).isTrue();
//        assertThat(consent.getPrivacyPolicyAgreedAt())
//                .isNotNull()
//                .isBeforeOrEqualTo(now);
//
//        assertThat(consent.getMarketingAgreed()).isTrue();
//        assertThat(consent.getMarketingAgreedAt())
//                .isNotNull()
//                .isBeforeOrEqualTo(now);
//    }
//
//    @Test
//    @DisplayName("미동의 시 동의 시간이 null로 저장된다")
//    void createPrivacyConsentWithoutAgreement() {
//        // When
//        PrivacyConsent consent = PrivacyConsent.builder()
//                .termsAgreed(false)
//                .privacyPolicyAgreed(false)
//                .marketingAgreed(false)
//                .build();
//
//        // Then
//        assertThat(consent.getTermsAgreed()).isFalse();
//        assertThat(consent.getTermsAgreedAt()).isNull();
//
//        assertThat(consent.getPrivacyPolicyAgreed()).isFalse();
//        assertThat(consent.getPrivacyPolicyAgreedAt()).isNull();
//
//        assertThat(consent.getMarketingAgreed()).isFalse();
//        assertThat(consent.getMarketingAgreedAt()).isNull();
//    }
//}