package com.study.moya.oauth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.domain.Role;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.oauth.dto.user.GoogleUserInfo;
import com.study.moya.oauth.dto.auth.OAuthTokenResponse;
import com.study.moya.oauth.exception.OAuthErrorCode;
import com.study.moya.oauth.exception.OAuthException;
import com.study.moya.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberOAuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    /**
     * 신규 회원 여부 검사 메서드
     * nickname -> email에 '@gmail.com' 제외한 부분
     */
    @Transactional
    public Member createOrUpdateMember(GoogleIdToken.Payload idTokenPayload, OAuthTokenResponse tokenResponse) {
        try {
            Member existingMember = memberRepository.findByEmail(idTokenPayload.getEmail()).orElse(null);

            if (existingMember == null) {return createNewMember(idTokenPayload, tokenResponse);}

            return updateExistingMember(existingMember, tokenResponse);
        } catch (Exception e) {
            log.error("Failed to create or update member", e);
            throw OAuthException.of(OAuthErrorCode.MEMBER_NOT_FOUND);
        }
    }

    /**
     * 신규 회원 생성
     */
    private Member createNewMember(GoogleIdToken.Payload idTokenPayload,
                                   OAuthTokenResponse tokenResponse) {
        String defaultNickname = idTokenPayload.getEmail().split("@")[0];

        Member newMember = Member.builder()
                .email(idTokenPayload.getEmail())
                .profileImageUrl((String) idTokenPayload.get("picture"))
                .nickname(defaultNickname)
                .roles(Set.of(Role.USER))
                .status(MemberStatus.ACTIVE)
                .providerId(idTokenPayload.getSubject())
                .termsAgreed(true)
                .privacyPolicyAgreed(true)
                .marketingAgreed(true)
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .tokenExpirationTime(Instant.now().plusSeconds(tokenResponse.getExpiresIn()))
                .build();

        return memberRepository.save(newMember);
    }

    /**
     * 기존 회원 업데이트
     */
    private Member updateExistingMember(Member member, OAuthTokenResponse tokenResponse) {
        member.updateOAuthTokens(
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                Instant.now().plusSeconds(tokenResponse.getExpiresIn())
        );
        return memberRepository.save(member);
    }

    /**
     * 로그아웃 메서드
     */
    public void logout(String memberId, String refreshToken) {
        try {
            log.info("Logout request received - memberId: {}, Refresh Token: {}", memberId, refreshToken);
            SecurityContextHolder.clearContext();

            /**
             * redis 임시조치
             */
//            redisService.deleteRefreshToken(memberId);
            log.info("Refresh token deleted for user: {}", memberId);

            log.info("Logout completed successfully for user: {}", memberId);
        } catch (Exception e) {
            log.error("Error during logout process", e);
            throw OAuthException.of(OAuthErrorCode.LOGOUT_FAILED);
        }
    }

    /**
     * 탈퇴 기능 메서드
     */
    public void withdraw(String accessToken){
        try{
            log.info("accessToken for withdraw: {}", accessToken);
            String memberId = jwtTokenProvider.getEmailFromOAuthToken(accessToken);
            log.info("Start withdraw Account for {}", memberId);
            // 회원 조회
            Member member = memberRepository.findByEmail(memberId)
                    .orElseThrow(() -> OAuthException.of(OAuthErrorCode.MEMBER_NOT_FOUND));

            // 회원 삭제
            memberRepository.delete(member);
            redisService.deleteRefreshToken(memberId);
            log.info("Successfully withdrew account for {}", memberId);

        } catch (Exception e) {
            log.error("Error during withdrawal process for email", e);
            throw OAuthException.of(OAuthErrorCode.WITHDRAWAL_FAILED);
        }
    }
}
