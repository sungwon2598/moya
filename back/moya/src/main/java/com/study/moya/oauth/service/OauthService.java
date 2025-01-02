package com.study.moya.oauth.service;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.domain.Role;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.oauth.dto.OAuthLogin.GoogleIdTokenResponse;
import com.study.moya.oauth.dto.GoogleUserInfo;
import com.study.moya.oauth.dto.OAuthLogin.IdTokenRequestDto;
import com.study.moya.oauth.dto.OAuthLogin.MemberAuthResult;
import com.study.moya.oauth.dto.token.TokenRefreshResult;
import com.study.moya.oauth.exception.InvalidTokenException;
import com.study.moya.redis.RedisService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class OauthService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private GoogleIdTokenVerifier verifier; // final 제거

    @PostConstruct
    public void init() {
        log.info("Initializing OAuthService with client ID: {}", clientId);
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .setIssuer("https://accounts.google.com") // Google의 issuer 추가
                .build();
    }

    /**
     * 요청 출처에 따라 적절한 리다이렉트 URI 설정
     */

   private String determineRedirectUri(String origin) {
       log.info("Determining redirect URI for origin: {}", origin);

       if (origin == null) {
           return "https://moyastudy.com";
       }
       return origin;
   }

   private final MemberRepository memberRepository;
   private final JwtTokenProvider jwtTokenProvider;
   private final WebClient webClient;
   private final RedisService redisService;


    /**
     * OAuth credential 토큰 검증
     */
    @Transactional
    public MemberAuthResult loginOAuthGoogle(IdTokenRequestDto requestBody) {
        log.info("Authorization Code: {}", requestBody.getAuthCode());

        GoogleIdTokenResponse tokenResponse = getGoogleTokens(requestBody.getAuthCode(),requestBody.getRedirectUri());
        GoogleIdToken.Payload idTokenPayload = verifyAndGetIdToken(tokenResponse.getIdToken());
        GoogleUserInfo userInfo = getGoogleUserInfo(tokenResponse.getAccessToken());

        Member savedMember = createOrUpdateMember(idTokenPayload, userInfo, tokenResponse);

        List<GrantedAuthority> authorities = savedMember.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedMember,
                null,
                authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        JwtTokenProvider.TokenInfo tokenInfo = jwtTokenProvider.createToken(authentication);
        String memberId = String.valueOf(savedMember.getId());

        String existingIdentifier = redisService.findIdentifierByMemberId(memberId);

        if (existingIdentifier != null) {
            redisService.deleteRefreshToken(memberId);
        }
//        String uniqueIdentifier = generateUniqueIdentifier();
//        String encryptedEmail = aesConverter.convertToDatabaseColumn(userInfo.getEmail());
        redisService.saveTokens(memberId, tokenInfo.getRefreshToken());

        return new MemberAuthResult(tokenInfo.getAccessToken(), tokenInfo.getRefreshToken(), savedMember);
    }

    private GoogleIdTokenResponse getGoogleTokens(String authorizationCode, String redirectUri) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", authorizationCode);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);
        formData.add("grant_type", "authorization_code");

        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(GoogleIdTokenResponse.class)
                .block();
    }


    /**
     * Access Token으로 Google 사용자 정보 가져오기
     */
    private GoogleUserInfo getGoogleUserInfo(String accessToken) {
        return webClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .block();
    }


    /**
     * 신규 회원 여부 검사 메서드
     * nickname -> email에 '@gmail.com' 제외한 부분
     */

    @Transactional
    public Member createOrUpdateMember(GoogleIdToken.Payload idTokenPayload,
                                       GoogleUserInfo userInfo,
                                       GoogleIdTokenResponse tokenResponse) {
        log.debug("Attempting to create or update member with email: {}", idTokenPayload.getEmail());

        Member existingMember = memberRepository.findByEmail(idTokenPayload.getEmail()).orElse(null);

        if (existingMember == null) {
            log.info("Creating new member with email: {}", idTokenPayload.getEmail());

            try {
                String defaultNickname = idTokenPayload.getEmail().split("@")[0];

                Member newMember = Member.builder()
                        .email(idTokenPayload.getEmail())
                        .profileImageUrl(userInfo.getPicture())
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
            } catch (Exception e) {
                log.error("Failed to create new member", e);
                throw e;
            }
        }

        log.debug("Updating existing member with email: {}", idTokenPayload.getEmail());

        try {
            existingMember.updateOAuthTokens(
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    Instant.now().plusSeconds(tokenResponse.getExpiresIn())
            );
            return memberRepository.save(existingMember);
        } catch (Exception e) {
            log.error("Failed to update member", e);
            throw e;
        }
    }

    /**
     * ID Token 검증 및 Payload 추출
     */
    private GoogleIdToken.Payload verifyAndGetIdToken(String idToken){
        try {
            log.debug("Verifying ID token: {}", idToken);

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(clientId))
                    .setIssuer("https://accounts.google.com")
                    .build();

            if (idToken == null || idToken.trim().isEmpty()) {
                log.error("ID token is null or empty");
                throw new IllegalArgumentException("ID token cannot be null or empty");
            }

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                log.error("Failed to verify Google ID token");
                throw new IllegalArgumentException("Invalid Google ID token");
            }

            return googleIdToken.getPayload();

        } catch (Exception e) {
            log.error("ID Token verification failed", e);
            throw new IllegalArgumentException("ID Token verification failed", e);
        }
    }

    /**
     * RefreshToken을 이용한 JwtToken 발급 메서드
     */
    @Transactional
    public TokenRefreshResult refreshToken(String refreshToken) {
        log.info(refreshToken);
        String currentIdentifier = jwtTokenProvider.getEmailFromOAuthToken(refreshToken);
        log.info(currentIdentifier);
        String storedRefreshToken = redisService.getRefreshToken(currentIdentifier);
        log.info(storedRefreshToken);
//        String storedEmail = redisService.getEmailByIdentifier(currentIdentifier);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("Expired or invalid refresh token");
        }

        Member member = memberRepository.findByEmail(currentIdentifier)
                .orElseThrow(() -> new InvalidTokenException("User not found"));

        List<GrantedAuthority> authorities = member.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

//        String newIdentifier = generateUniqueIdentifier();
        String newAccessToken = jwtTokenProvider.createTokenForOAuth(currentIdentifier);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(currentIdentifier);

        redisService.saveTokens(currentIdentifier, newRefreshToken);

        return new TokenRefreshResult(newAccessToken, newRefreshToken);
    }


    /**                          
     * 로그아웃 메서드
     */
    public void logout(String accessToken, String refreshToken) {
        try {
            // 리프레시 토큰에서 이메일 추출
            log.info("ac, rf : {}, {}", accessToken, refreshToken);
            String userEmail = jwtTokenProvider.getEmailFromOAuthToken(refreshToken);
            log.info("userEmail : {}", userEmail);

            SecurityContextHolder.clearContext();

            // 액세스 토큰 블랙리스트에 추가
            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                long expirationTime = jwtTokenProvider.getExpirationTime(accessToken);
                redisService.addToBlacklist(accessToken, expirationTime);
            }

            // Redis에서 리프레시 토큰 삭제
            redisService.deleteRefreshToken(userEmail);

            log.info("User logged out successfully: {}", userEmail);
        } catch (Exception e) {
            log.error("Error during logout process", e);
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
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

            // 회원 삭제
            memberRepository.delete(member);
            redisService.deleteRefreshToken(memberId);
            log.info("Successfully withdrew account for {}", memberId);

        } catch (Exception e) {
            log.error("Error during withdrawal process for email", e);
            throw new RuntimeException("회원 탈퇴 처리 중 오류가 발생했습니다.", e);
        }
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