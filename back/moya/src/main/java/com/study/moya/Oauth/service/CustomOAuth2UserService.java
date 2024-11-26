package com.study.moya.Oauth.service;

import com.study.moya.Oauth.dto.OAuthUserInfo;
import com.study.moya.Oauth.dto.OauthLoginResponse;
import com.study.moya.Oauth.exception.OAuth2AuthenticationProcessingException;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OauthService oAuthService;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2 사용자 정보 로딩 시작");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        try {
            String email = oAuth2User.getAttribute("email");
            String providerId = oAuth2User.getAttribute("sub");
            String profileImageUrl = (String) attributes.get("picture");
            String accessToken = userRequest.getAccessToken().getTokenValue();
            Instant tokenExpirationTime = userRequest.getAccessToken().getExpiresAt();
            String refreshToken = extractRefreshToken(userRequest);

            log.info("OAuth2 사용자 정보 추출 - 이메일: {}, 제공자: {}", email, providerId);

            OAuthUserInfo userInfo = OAuthUserInfo.builder()
                    .email(email)
                    .providerId(providerId)
                    .profileImageUrl(profileImageUrl)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenExpirationTime(tokenExpirationTime)
                    .build();

            OauthLoginResponse loginResponse = oAuthService.processOAuthLogin(userInfo);

            // 원래 OAuth2User 그대로 반환
            return oAuth2User;


        } catch (Exception ex) {
            log.error("OAuth2 사용자 처리 중 오류 발생", ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private String extractRefreshToken(OAuth2UserRequest userRequest) {
        if (userRequest.getAdditionalParameters().containsKey("refresh_token")) {
            return userRequest.getAdditionalParameters().get("refresh_token").toString();
        }
        log.warn("No refresh token received");
        return null;
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
                    Optional.ofNullable(member.getPrivacyConsent()).map(PrivacyConsent::getMarketingAgreed).orElse(false),
                    member.getRoles() != null ? member.getRoles() : Set.of(Role.USER),  // Role이 null이면 기본값으로 USER 설정
                    member.getStatus() != null ? member.getStatus() : MemberStatus.ACTIVE  // Status가 null이면 기본값으로 ACTIVE 설정
            );
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

