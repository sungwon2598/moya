package com.study.moya.oauth.service;

import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.domain.Role;
import com.study.moya.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 사용자 정보 추출
        String email = (String) attributes.get("email");
        String picture = (String) attributes.get("picture");
        String providerId = (String) attributes.get("sub"); // Google에서의 고유 ID
        String defaultNickname = email.split("@")[0]; // 닉네임은 이메일에서 '@' 앞 부분으로 설정
        String accessToken = userRequest.getAccessToken().getTokenValue(); // OAuth 토큰 정보 추출

        // 회원 정보 조회 또는 생성
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    // 신규 회원 생성

                    Instant tokenExpirationTime = null;
                    if (userRequest.getAccessToken().getExpiresAt() != null) {
                        tokenExpirationTime = userRequest.getAccessToken().getExpiresAt();
                    } else if (userRequest.getAdditionalParameters().containsKey("expires_in")) {
                        Integer expiresIn = (Integer) userRequest.getAdditionalParameters().get("expires_in");
                        tokenExpirationTime = Instant.now().plusSeconds(expiresIn);
                    }

                    Member newMember = Member.builder()
                            .email(email)
                            .profileImageUrl(picture)
                            .nickname(defaultNickname)
                            .roles(Set.of(Role.USER))
                            .status(MemberStatus.ACTIVE)
                            .providerId(providerId)
                            .termsAgreed(true)
                            .privacyPolicyAgreed(true)
                            .marketingAgreed(true)
                            .accessToken(accessToken)
                            .refreshToken(null)
                            .tokenExpirationTime(tokenExpirationTime)
                            .build();

                    Member savedMember = memberRepository.save(newMember);
                    return savedMember;
                });

        return new DefaultOAuth2User(
                member.getAuthorities(),
                attributes,
                "sub" // 구글의 사용자 식별자 필드명
        );
    }
}