package com.study.moya.auth.jwt;


import com.study.moya.auth.domain.RefreshToken;
import com.study.moya.auth.exception.InvalidRefreshTokenException;
import com.study.moya.auth.exception.TokenProcessingException;
import com.study.moya.auth.repository.RefreshTokenRepository;
import com.study.moya.member.domain.Member;
import com.study.moya.member.service.MemberService;
import com.study.moya.oauth.exception.OAuthErrorCode;
import com.study.moya.oauth.exception.OAuthException;
import com.study.moya.redis.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectProvider<MemberService> memberServiceProvider;
    private final RedisService redisService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInMilliseconds;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.info("JwtTokenProvider가 비밀 키로 초기화되었습니다");
    }

    @Transactional
    public TokenInfo createToken(Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        Long userId = member.getId();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Access Token 생성
        String accessToken = createAccessToken(userId, authorities, member);
        // Refresh Token 생성
        String refreshToken = createRefreshToken(userId);

        log.info("사용자를 위한 토큰 생성 완료: {}", userId);
        return new TokenInfo(accessToken, refreshToken);
    }

    private String createAccessToken(Long id, String authorities, Member member) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim("auth", authorities)
//                .claim("id", member.getId())
//                .claim("name", member.getNickname())
                .issuedAt(now)
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    @Transactional
    public String createRefreshToken(Long id) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        String refreshToken = Jwts.builder()
                .subject(String.valueOf(id))
                .issuedAt(now)
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        try {
            // 기존 리프레시 토큰이 있다면 삭제
            refreshTokenRepository.deleteByMemberId(id);
            // 새로운 리프레시 토큰 저장
            refreshTokenRepository.save(new RefreshToken(
                    refreshToken,
                    id,
                    LocalDateTime.now().plusSeconds(refreshTokenValidityInMilliseconds / 1000)
            ));

            log.debug("리프레시 토큰 생성 및 저장 완료 - 사용자: {}", id);
        } catch (Exception e) {
            log.error("리프레시 토큰 처리 중 오류 발생 - 사용자: {}, 오류: {}", id, e.getMessage());
            throw new TokenProcessingException("리프레시 토큰 처리 중 오류가 발생했습니다.", e);
        }
        return refreshToken;
    }

    @Transactional
    public TokenInfo refreshAccessToken(String refreshToken) {
        // 리프레시 토큰 유효성 검증
        if (!validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException("유효하지 않은 리프레시 토큰입니다.");
        }

        // DB에서 리프레시 토큰 조회
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("존재하지 않는 리프레시 토큰입니다."));

        if (refreshTokenEntity.isExpired()) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new InvalidRefreshTokenException("만료된 리프레시 토큰입니다.");
        }

        // 사용자 정보 조회
        MemberService memberService = memberServiceProvider.getObject();
//        Member member = memberService.findByEmail(refreshTokenEntity.getMemberEmail());
        Member member = memberService.findById(refreshTokenEntity.getMemberId());

        // 새로운 액세스 토큰 생성
        String authorities = member.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String newAccessToken = createAccessToken(member.getId(), authorities, member);

        // 새로운 리프레시 토큰 생성
        String newRefreshToken = createRefreshToken(member.getId());

        log.info("토큰 갱신 완료 - 사용자 ID: {}, Email : {}", member.getId(), member.getEmail());
        return new TokenInfo(newAccessToken, newRefreshToken);
    }

    public Authentication getAuthentication(String token) {
        log.debug("토큰으로부터 인증 정보 추출 중");
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

//      String username = claims.getSubject();
        Long userId = Long.parseLong(claims.getSubject());
        log.info("사용자에 대한 인증 정보가 추출되었습니다: {}", userId);

        return new UsernamePasswordAuthenticationToken(userId, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            // 토큰이 블랙리스트에 있는지 확인
            if (redisService.isBlacklisted(token)) {
                log.warn("Token is blacklisted");
                return false;
            }
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * OAuth 임시 토큰에서 이메일 추출
     */
    public String getEmailFromOAuthToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw OAuthException.of(OAuthErrorCode.INVALID_ACCESS_TOKEN);
            }

            if (!validateToken(token)) {
                throw OAuthException.of(OAuthErrorCode.INVALID_ACCESS_TOKEN);
            }

            Authentication authentication = getAuthentication(token);
            if (authentication == null || authentication.getName() == null) {
                throw OAuthException.of(OAuthErrorCode.INVALID_ACCESS_TOKEN);
            }

            return authentication.getName();
        } catch (JwtException e) {
            log.error("JWT 토큰 처리 중 오류 발생: {}", e.getMessage());
            throw OAuthException.of(OAuthErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    /**
     * 토큰의 만료 시간을 반환
     */
    public long getExpirationTime(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration().getTime();
    }

    public Long extractMemberId(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw OAuthException.of(OAuthErrorCode.INVALID_ACCESS_TOKEN);
            }

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();
            if (subject == null || subject.trim().isEmpty()) {
                throw OAuthException.of(OAuthErrorCode.INVALID_ACCESS_TOKEN);
            }

            return Long.parseLong(subject);
        } catch (JwtException e) {
            log.error("JWT 토큰 처리 중 오류 발생: {}", e.getMessage());
            throw OAuthException.of(OAuthErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    // TokenInfo DTO
    @Getter
    @AllArgsConstructor
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
    }
}
