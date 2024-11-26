package com.study.moya.Oauth.mapper;

import com.study.moya.Oauth.dto.OAuth2SignupCompleteRequest;
import com.study.moya.Oauth.dto.OAuth2SignupCompleteResponse;
import com.study.moya.Oauth.dto.OAuthTempMemberInfo;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.domain.PrivacyConsent;
import com.study.moya.member.domain.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MemberMapper {
    private static final Logger logger = LoggerFactory.getLogger(MemberMapper.class);

    public Member toMember(OAuthTempMemberInfo tempInfo, OAuth2SignupCompleteRequest request) {
        logger.info("Creating member with email: {}, nickname: {}", tempInfo.getEmail(), request.getNickname());
        Member member = Member.builder()
                .email(tempInfo.getEmail())
                .password(null)
                .nickname(request.getNickname())
                .providerId(tempInfo.getProviderId())
                .profileImageUrl(tempInfo.getProfileImageUrl())
                .accessToken(tempInfo.getAccessToken())
                .refreshToken(tempInfo.getRefreshToken())
                .tokenExpirationTime(tempInfo.getTokenExpirationTime())
                .termsAgreed(request.isTermsAgreed())
                .privacyPolicyAgreed(request.isPrivacyPolicyAgreed())
                .marketingAgreed(request.isMarketingAgreed())
                .status(MemberStatus.ACTIVE)
                .roles(Set.of(Role.USER))
                .build();

        logger.info("Created member status: {}", member.getStatus());
        logger.info("Created member: {}", member);
        return member;

    }


}
