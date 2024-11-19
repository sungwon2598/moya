package com.study.moya.Oauth.service;

import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.domain.PrivacyConsent;
import com.study.moya.member.domain.Role;
import com.study.moya.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("Loading OAuth2 user");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub");
        String profileImageUrl = (String) attributes.get("picture");

        logger.info("OAuth2 user info - Email: {}, Provider: {}", email, providerId);

        String accessToken = userRequest.getAccessToken().getTokenValue();
        Instant tokenExpirationTime = userRequest.getAccessToken().getExpiresAt();

        logger.info("Access token received. Expires at: {}", tokenExpirationTime);

        String refreshToken;
        if (userRequest.getAdditionalParameters().containsKey("refresh_token")) {
            refreshToken = userRequest.getAdditionalParameters().get("refresh_token").toString();
            logger.info("Refresh token received");
        } else {
            refreshToken = null;
            logger.warn("No refresh token received");
        }

        Member member = memberRepository.findByEmail(email)
                .map(existingMember -> {
                    logger.info("Updating existing member: {}", existingMember.getEmail());
                    return updateMember(existingMember, email, accessToken, refreshToken, tokenExpirationTime);
                })
                .orElseGet(() -> {
                    logger.info("Creating new member with email: {}", email);
                    return createMember(email, providerId, profileImageUrl, accessToken, refreshToken, tokenExpirationTime);
                });

        logger.info("OAuth2User created successfully");
        return new OAuth2UserImpl(member, oAuth2User.getAttributes());
    }
    private Member createMember(String email, String profileImageUrl , String providerId, String accessToken, String refreshToken, Instant tokenExpirationTime ){

        Member newMember = Member.builder()
                .email(email)
                .password(null) // OAuth 로그인이므로 password는 null
                .nickname(email.split("@")[0]) // 이메일의 @ 앞부분을 닉네임으로 설정 -> 이후 설정해야한다
                .providerId(providerId)
                .profileImageUrl(profileImageUrl)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenExpirationTime(tokenExpirationTime)
                .termsAgreed(false)
                .privacyPolicyAgreed(false)
                .marketingAgreed(false)
                //.status(MemberStatus.INACTIVE) -> 약관 동의 전이므로 INACTIVE 상태를 만드는것이 어떤지?
                .build();
        Member savedMember = memberRepository.save(newMember);
        logger.info("New member created and saved - ID: {}", savedMember.getId());
        return savedMember;
    }

    private Member updateMember(Member existingMember, String email, String accessToken, String refreshToken, Instant tokenExpirationTime
    ) {
        logger.info("Updating member - ID: {}, Email: {}", existingMember.getId(), existingMember.getEmail());
        Member updatedMember = Member.updateBuilder()
                .existingMember(existingMember)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenExpirationTime(tokenExpirationTime)
                .build();
        logger.info("Updated member info: {}", updatedMember.getEmail());
        return memberRepository.save(updatedMember);
    }

    public static class OAuth2UserImpl extends Member implements OAuth2User {
        private final Map<String, Object> attributes;

        public OAuth2UserImpl(Member member, Map<String, Object> attributes) {
            super(
                    member.getEmail(),
                    member.getPassword(),
                    member.getNickname(),
                    member.getProviderId(),
                    member.getProfileImageUrl(),
                    member.getAccessToken(),
                    member.getRefreshToken(),
                    member.getTokenExpirationTime(),
                    Optional.ofNullable(member.getPrivacyConsent()).map(PrivacyConsent::getTermsAgreed).orElse(false),
                    Optional.ofNullable(member.getPrivacyConsent()).map(PrivacyConsent::getPrivacyPolicyAgreed).orElse(false),
                    Optional.ofNullable(member.getPrivacyConsent()).map(PrivacyConsent::getMarketingAgreed).orElse(false)
            );

            // 부모 클래스의 필드들을 직접 복사하는 방식이 필요하다면
            // reflection이나 다른 방식으로 처리 필요

            this.attributes = attributes;
            log.info("OAuth2UserImpl created for user: {}", this.getEmail());
        }

        @Override
        public Map<String, Object> getAttributes() {
            return Collections.unmodifiableMap(this.attributes);
        }


        @Override
        public String getName() {
            return this.getEmail();
        }
    }


}
