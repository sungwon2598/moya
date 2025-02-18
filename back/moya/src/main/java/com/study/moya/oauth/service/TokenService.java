package com.study.moya.oauth.service;

import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.oauth.dto.token.TokenRefreshResponse;
import com.study.moya.oauth.exception.OAuthErrorCode;
import com.study.moya.oauth.exception.OAuthException;
import com.study.moya.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    /**
     * RefreshToken을 이용한 JwtToken 발급 메서드
     */
    @Transactional
    public TokenRefreshResponse refreshToken(String refreshToken) {
        log.info("Refresh token received: {}", refreshToken);

        // 토큰에서 이메일 추출
        Long currentId =jwtTokenProvider.extractMemberId(refreshToken);
        log.info("UserId extracted from token: {}", currentId);
        Member member = memberRepository.findById(currentId)
                .orElseThrow(() -> OAuthException.of(OAuthErrorCode.MEMBER_NOT_FOUND));

        String memberId = String.valueOf(currentId);

        String storedRefreshToken = redisService.getRefreshToken(memberId);
        log.info("Stored refresh token found: {}", storedRefreshToken);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw OAuthException.of(OAuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw OAuthException.of(OAuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        List<GrantedAuthority> authorities = member.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        JwtTokenProvider.TokenInfo tokenInfo = jwtTokenProvider.createToken(authentication);

        redisService.saveTokens(memberId, tokenInfo.getRefreshToken());

        return new TokenRefreshResponse(tokenInfo.getAccessToken(), tokenInfo.getRefreshToken());
    }


    /**
     * UUID + 시간 기반 해시 등을 조합하여 unique한 식별자 생성
     */
    private String generateUniqueIdentifier() {
        int maxAttempts = 5;

        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            String newIdentifier = UUID.randomUUID().toString();

            if (!redisService.hasIdentifier(newIdentifier)) {
                return newIdentifier;
            }
        }

        throw new RuntimeException("Failed to generate unique identifier");
    }
}
