//package com.study.moya.member.domain;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.assertj.core.api.BDDAssertions.within;
//
//import com.study.moya.member.constants.MemberErrorCode;
//import com.study.moya.member.exception.MemberException;
//import jakarta.persistence.EntityManager;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@DisplayName("Member 도메인 테스트")
//class MemberTest2 {
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
//                .build();
//
//        em.persist(member);
//        em.flush();
//        em.clear();
//    }
//
//    @Nested
//    @DisplayName("개인정보 동의(PrivacyConsent) 관련 테스트")
//    class PrivacyConsentTest {
//
//        @Test
//        @DisplayName("마케팅 동의는 선택적이며 기본값은 false다")
//        void marketingConsentIsOptionalWithDefaultFalse() {
//            Member newMember = Member.builder()
//                    .email("test@example.com")
//                    .nickname("testUser")
//                    .providerId("provider123")
//                    .termsAgreed(true)
//                    .privacyPolicyAgreed(true)
//                    .build();
//
//            PrivacyConsent consent = newMember.getPrivacyConsent();
//            assertThat(consent.getMarketingAgreed()).isFalse();
//            assertThat(consent.getMarketingAgreedAt()).isNull();
//
//            // null 값으로 마케팅 동의 업데이트
//            consent.updateMarketingConsent(null);
//            assertThat(consent.getMarketingAgreed()).isFalse();
//            assertThat(consent.getMarketingAgreedAt()).isNull();
//        }
//
//        @Test
//        @DisplayName("동의 시점이 정확하게 기록된다")
//        void consentTimeIsRecordedCorrectly() {
//            Member newMember = Member.builder()
//                    .email("test@example.com")
//                    .nickname("testUser")
//                    .providerId("provider123")
//                    .termsAgreed(true)
//                    .privacyPolicyAgreed(true)
//                    .marketingAgreed(true)
//                    .build();
//
//            PrivacyConsent consent = newMember.getPrivacyConsent();
//            LocalDateTime now = LocalDateTime.now();
//
//            assertThat(consent.getTermsAgreedAt())
//                    .isCloseTo(now, within(1, ChronoUnit.SECONDS));
//            assertThat(consent.getPrivacyPolicyAgreedAt())
//                    .isCloseTo(now, within(1, ChronoUnit.SECONDS));
//            assertThat(consent.getMarketingAgreedAt())
//                    .isCloseTo(now, within(1, ChronoUnit.SECONDS));
//        }
//    }
//
//    @Nested
//    @DisplayName("회원 상태 관련 테스트")
//    class MemberStatusTest {
//
//        @Test
//        @DisplayName("차단된 회원은 로그인할 수 없다")
//        void blockedMemberCannotLogin() {
//            Member activeMember = em.find(Member.class, member.getId());
//            activeMember.block("violation");
//
//            assertThatThrownBy(() -> activeMember.updateLoginInfo())
//                    .isInstanceOf(MemberException.class)
//                    .hasMessageContaining(MemberErrorCode.MEMBER_BLOCKED.getMessage())
//                    .matches(e -> ((MemberException) e).getErrorCode() == MemberErrorCode.MEMBER_BLOCKED);
//        }
//
//        @Test
//        @DisplayName("탈퇴한 회원은 로그인할 수 없다")
//        void withdrawnMemberCannotLogin() {
//            Member activeMember = em.find(Member.class, member.getId());
//            activeMember.withdraw();
//
//            assertThatThrownBy(() -> activeMember.updateLoginInfo())
//                    .isInstanceOf(MemberException.class)
//                    .hasMessageContaining(MemberErrorCode.MEMBER_WITHDRAWN.getMessage())
//                    .matches(e -> ((MemberException) e).getErrorCode() == MemberErrorCode.MEMBER_WITHDRAWN);
//        }
//
//        @Test
//        @DisplayName("휴면 상태의 회원은 정보를 수정할 수 없다")
//        void dormantMemberCannotModifyInfo() {
//            Member activeMember = em.find(Member.class, member.getId());
//            activeMember.convertToDormant();
//
//            assertThatThrownBy(() -> activeMember.updateNickname("newNick"))
//                    .isInstanceOf(MemberException.class)
//                    .hasMessageContaining(MemberErrorCode.MEMBER_NOT_MODIFIABLE.getMessage(MemberStatus.DORMANT.getStateMessage()))
//                    .matches(e -> ((MemberException) e).getErrorCode() == MemberErrorCode.MEMBER_NOT_MODIFIABLE);
//        }
//    }
//
//    @Nested
//    @DisplayName("중복 검증 관련 테스트")
//    class DuplicationTest {
//
//        @Test
//        @DisplayName("동일한 닉네임으로 가입할 수 없다")
//        void cannotUseDuplicateNickname() {
//            // given
//            String duplicateNickname = "testUser";
//
//            // when & then
//            assertThatThrownBy(() -> {
//                Member newMember = Member.builder()
//                        .email("another@example.com")
//                        .nickname(duplicateNickname)
//                        .providerId("anotherId")
//                        .termsAgreed(true)
//                        .privacyPolicyAgreed(true)
//                        .build();
//                em.persist(newMember);
//                em.flush();
//            }).isInstanceOf(Exception.class);
//        }
//    }
//
//    @Nested
//    @DisplayName("삭제된 회원 관련 테스트")
//    class DeletedMemberTest {
//
//        @Test
//        @DisplayName("존재하지 않는 회원을 조회할 수 없다")
//        void cannotFindNonExistentMember() {
//            assertThatThrownBy(() -> {
//                Member foundMember = em.find(Member.class, 999L);
//                if (foundMember == null) {
//                    throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
//                }
//            })
//                    .isInstanceOf(MemberException.class)
//                    .hasMessageContaining(MemberErrorCode.MEMBER_NOT_FOUND.getMessage())
//                    .matches(e -> ((MemberException) e).getErrorCode() == MemberErrorCode.MEMBER_NOT_FOUND);
//        }
//    }
//}