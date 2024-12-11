//package com.study.moya.member.domain;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.assertj.core.api.BDDAssertions.within;
//
//import com.study.moya.member.constants.MemberConstants;
//import com.study.moya.member.constants.MemberErrorCode;
//import com.study.moya.member.exception.MemberException;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.OptimisticLockException;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.util.ReflectionTestUtils;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class MemberTest {
//
//    @Autowired
//    private EntityManager em;
//
//    private Member member;
//
//    @BeforeEach
//    void setUp() {
//        member = Member.builder()
//                .email("test@example.com")
//                .nickname("testUser")
//                .providerId("provider123")
//                .profileImageUrl("http://example.com/profile.jpg")
//                .termsAgreed(true)
//                .privacyPolicyAgreed(true)
//                .marketingAgreed(false)
//                .build();
//
//        em.persist(member);
//        em.flush();
//    }
//
//    @Test
//    @DisplayName("회원 생성 시 기본 상태는 ACTIVE이다")
//    void createMember() {
//        // when
//        Member foundMember = em.find(Member.class, member.getId());
//
//        // then
//        assertThat(foundMember.getStatus()).isEqualTo(MemberStatus.ACTIVE);
//        assertThat(foundMember.getRoles()).contains(Role.USER);
//        assertThat(foundMember.getLastLoginAt()).isNotNull();
//
//        // personalInfoExpiryDate 검증
//        LocalDateTime expectedExpiryDate = foundMember.getLastLoginAt()
//                .plusYears(MemberConstants.PERSONAL_INFO_RETENTION_YEARS);
//        LocalDateTime actualExpiryDate = foundMember.getPersonalInfoExpiryDate();
//
//        assertThat(actualExpiryDate)
//                .isCloseTo(expectedExpiryDate, within(1, ChronoUnit.SECONDS));
//    }
//
//    @Test
//    @DisplayName("회원 가입 시 동의 항목에 따라 동의 시간이 기록된다")
//    void createMemberWithPrivacyConsent() {
//        Member foundMember = em.find(Member.class, member.getId());
//        PrivacyConsent consent = foundMember.getPrivacyConsent();
//
//        assertThat(consent.getTermsAgreed()).isTrue();
//        assertThat(consent.getTermsAgreedAt()).isNotNull();
//        assertThat(consent.getPrivacyPolicyAgreed()).isTrue();
//        assertThat(consent.getPrivacyPolicyAgreedAt()).isNotNull();
//        assertThat(consent.getMarketingAgreed()).isFalse();
//        assertThat(consent.getMarketingAgreedAt()).isNull();
//    }
//
//    @Test
//    @DisplayName("닉네임 수정이 가능하다")
//    void updateNickname() {
//        Member foundMember = em.find(Member.class, member.getId());
//        String newNickname = "newNickname";
//
//        foundMember.updateNickname(newNickname);
//        em.flush();
//        em.clear();
//
//        Member updatedMember = em.find(Member.class, member.getId());
//        assertThat(updatedMember.getNickname()).isEqualTo(newNickname);
//    }
//
//    @Test
//    @DisplayName("ACTIVE 상태가 아닌 경우 닉네임 수정이 불가능하다")
//    void cannotUpdateNicknameWhenNotActive() {
//        Member foundMember = em.find(Member.class, member.getId());
//        foundMember.convertToDormant();
//        em.flush();
//        em.clear();
//
//        Member dormantMember = em.find(Member.class, member.getId());
//        assertThatThrownBy(() -> dormantMember.updateNickname("newNickname"))
//                .isInstanceOf(MemberException.class)
//                .hasMessageContaining(MemberStatus.DORMANT.getStateMessage())
//                .matches(e -> ((MemberException) e).getErrorCode() == MemberErrorCode.MEMBER_NOT_MODIFIABLE);
//    }
//
//    @Test
//    @DisplayName("프로필 이미지 수정이 가능하다")
//    void updateProfileImage() {
//        Member foundMember = em.find(Member.class, member.getId());
//        String newImageUrl = "http://example.com/new-profile.jpg";
//
//        foundMember.updateProfileImage(newImageUrl);
//        em.flush();
//        em.clear();
//
//        Member updatedMember = em.find(Member.class, member.getId());
//        assertThat(updatedMember.getProfileImageUrl()).isEqualTo(newImageUrl);
//    }
//
//    @Test
//    @DisplayName("이메일 수정 시 개인정보 보관기간이 연장된다")
//    void updateEmailExtendsRetentionPeriod() {
//        Member foundMember = em.find(Member.class, member.getId());
//        LocalDateTime originalExpiryDate = foundMember.getPersonalInfoExpiryDate();
//        String newEmail = "new@example.com";
//
//        foundMember.updateEmail(newEmail);
//        em.flush();
//        em.clear();
//
//        Member updatedMember = em.find(Member.class, member.getId());
//        assertThat(updatedMember.getEmail()).isEqualTo(newEmail);
//        assertThat(updatedMember.getPersonalInfoExpiryDate()).isAfter(originalExpiryDate);
//    }
//
//    @Test
//    @DisplayName("마케팅 동의 상태 변경 시 동의 시간이 함께 변경된다")
//    void updateMarketingConsentWithTimestampUsingReflection() throws Exception {
//        // given
//        Member foundMember = em.find(Member.class, member.getId());
//
//        // 초기 상태 설정 (마케팅 동의 true로 시작)
//        LocalDateTime initialTime = LocalDateTime.now().minusHours(1);
//        foundMember.updateMarketingConsent(true);
//
//        // 리플렉션으로 동의 시간을 과거로 설정
//        java.lang.reflect.Field marketingAgreedAtField =
//                PrivacyConsent.class.getDeclaredField("marketingAgreedAt");
//        marketingAgreedAtField.setAccessible(true);
//        marketingAgreedAtField.set(foundMember.getPrivacyConsent(), initialTime);
//
//        em.flush();
//        em.clear();
//
//        // when: 동의 상태 변경
//        Member member = em.find(Member.class, foundMember.getId());
//        member.updateMarketingConsent(true);
//        em.flush();
//        em.clear();
//
//        // then: 새로운 동의 시간 검증
//        Member updatedMember = em.find(Member.class, foundMember.getId());
//        PrivacyConsent updatedConsent = updatedMember.getPrivacyConsent();
//
//        assertThat(updatedConsent.getMarketingAgreed()).isTrue();
//        assertThat(updatedConsent.getMarketingAgreedAt())
//                .isNotNull()
//                .isAfter(initialTime)
//                .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
//
//        // when: 동의 철회
//        updatedMember.updateMarketingConsent(false);
//        em.flush();
//        em.clear();
//
//        // then: 동의 철회 상태 검증
//        Member withdrawnMember = em.find(Member.class, foundMember.getId());
//        PrivacyConsent withdrawnConsent = withdrawnMember.getPrivacyConsent();
//
//        assertThat(withdrawnConsent.getMarketingAgreed()).isFalse();
//        assertThat(withdrawnConsent.getMarketingAgreedAt()).isNull();
//    }
//
//    @Test
//    @DisplayName("회원 차단 시 상태가 BLOCKED로 변경되고 로그인이 불가능하다")
//    void blockMember() {
//        Member foundMember = em.find(Member.class, member.getId());
//
//        foundMember.block("violation of terms");
//        em.flush();
//        em.clear();
//
//        Member blockedMember = em.find(Member.class, member.getId());
//        assertThat(blockedMember.getStatus()).isEqualTo(MemberStatus.BLOCKED);
//
//        assertThatThrownBy(() -> blockedMember.updateLoginInfo())
//                .isInstanceOf(MemberException.class)
//                .hasMessageContaining(MemberErrorCode.MEMBER_BLOCKED.getMessage())
//                .matches(e -> ((MemberException) e).getErrorCode() == MemberErrorCode.MEMBER_BLOCKED);
//    }
//
//    @Test
//    @DisplayName("회원 탈퇴 시 개인정보가 삭제되고 로그인이 불가능하다")
//    void withdrawMember() {
//        Member foundMember = em.find(Member.class, member.getId());
//        String originalEmail = foundMember.getEmail();
//        String originalNickname = foundMember.getNickname();
//
//        foundMember.withdraw();
//        em.flush();
//        em.clear();
//
//        Member withdrawnMember = em.find(Member.class, member.getId());
//        assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
//        assertThat(withdrawnMember.getEmail()).isNotEqualTo(originalEmail)
//                .startsWith("DELETED_");
//        assertThat(withdrawnMember.getNickname()).isNotEqualTo(originalNickname)
//                .startsWith("DELETED_");
//        assertThat(withdrawnMember.getProfileImageUrl()).isNull();
//        assertThat(withdrawnMember.getPrivacyConsent()).isNull();
//
//        assertThatThrownBy(() -> withdrawnMember.updateLoginInfo())
//                .isInstanceOf(MemberException.class)
//                .hasMessageContaining(MemberErrorCode.MEMBER_WITHDRAWN.getMessage())
//                .matches(e -> ((MemberException) e).getErrorCode() == MemberErrorCode.MEMBER_WITHDRAWN);
//    }
//
//    @Test
//    @DisplayName("휴면 전환 조건을 확인할 수 있다")
//    void checkDormantCondition() {
//        // given
//        Member foundMember = em.find(Member.class, member.getId());
//        // 30개월보다 더 오래된 로그인 날짜로 설정
//        LocalDateTime oldLoginDate = LocalDateTime.now()
//                .minusMonths(31); // DORMANT_MONTHS(30) + 1
//
//        // when
//        ReflectionTestUtils.setField(foundMember, "lastLoginAt", oldLoginDate);
//
//        // then
//        assertThat(foundMember.shouldBeDormant()).isTrue();
//
//        // 추가 검증 (선택사항)
//        // 30개월 이내의 날짜로는 휴면 대상이 아님을 검증
//        ReflectionTestUtils.setField(foundMember, "lastLoginAt",
//                LocalDateTime.now().minusMonths(29));
//        assertThat(foundMember.shouldBeDormant()).isFalse();
//    }
//
//    @Test
//    @DisplayName("휴면 계정 전환이 가능하다")
//    void convertToDormant() {
//        Member foundMember = em.find(Member.class, member.getId());
//
//        foundMember.convertToDormant();
//        em.flush();
//        em.clear();
//
//        Member dormantMember = em.find(Member.class, member.getId());
//        assertThat(dormantMember.getStatus()).isEqualTo(MemberStatus.DORMANT);
//    }
//
//    @Test
//    @DisplayName("휴면 계정 로그인 시 ACTIVE 상태로 전환된다")
//    void activateDormantAccount() throws Exception {
//        // given
//        Member foundMember = em.find(Member.class, member.getId());
//
//        // 리플렉션으로 이전 로그인 시간을 1시간 전으로 설정
//        LocalDateTime oldLoginTime = LocalDateTime.now().minusHours(1);
//        java.lang.reflect.Field lastLoginField = Member.class.getDeclaredField("lastLoginAt");
//        lastLoginField.setAccessible(true);
//        lastLoginField.set(foundMember, oldLoginTime);
//
//        foundMember.convertToDormant();
//        em.flush();
//        em.clear();
//
//        // when
//        Member dormantMember = em.find(Member.class, member.getId());
//        dormantMember.updateLoginInfo();
//        em.flush();
//        em.clear();
//
//        // then
//        Member activatedMember = em.find(Member.class, member.getId());
//        assertThat(activatedMember.getStatus()).isEqualTo(MemberStatus.ACTIVE);
//        assertThat(activatedMember.getLastLoginAt())
//                .isAfter(oldLoginTime)
//                .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
//    }
//
//    @Test
//    @DisplayName("개인정보 보관 기간을 연장할 수 있다")
//    void extendPersonalInfoPeriod() {
//        Member foundMember = em.find(Member.class, member.getId());
//        LocalDateTime originalExpiryDate = foundMember.getPersonalInfoExpiryDate();
//
//        foundMember.extendPersonalInfoPeriod();
//        em.flush();
//        em.clear();
//
//        Member updatedMember = em.find(Member.class, member.getId());
//        assertThat(updatedMember.getPersonalInfoExpiryDate()).isAfter(originalExpiryDate);
//    }
//
//    @Test
//    @DisplayName("개인정보 파기 대상 여부를 확인할 수 있다")
//    void checkPersonalInfoDeletionCondition() {
//        Member foundMember = em.find(Member.class, member.getId());
//        LocalDateTime expiredDate = LocalDateTime.now().minusYears(2);
//
//        // 리플렉션을 사용하여 private 필드 수정
//        try {
//            java.lang.reflect.Field expiryField =
//                    Member.class.getDeclaredField("personalInfoExpiryDate");
//            expiryField.setAccessible(true);
//            expiryField.set(foundMember, expiredDate);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        assertThat(foundMember.shouldBeDeleted()).isTrue();
//    }
//
//}