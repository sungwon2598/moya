package com.study.moya.oauth.service;


import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.domain.Role;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.member.util.AESConverter;
import com.study.moya.oauth.dto.GoogleIdTokenResponse;
import com.study.moya.oauth.dto.GoogleUserInfo;
import com.study.moya.oauth.dto.OAuthLogin.IdTokenRequestDto;
import com.study.moya.oauth.dto.OAuthLogin.MemberAuthResult;
import com.study.moya.oauth.dto.token.TokenRefreshResult;
import com.study.moya.oauth.exception.InvalidTokenException;
import com.study.moya.redis.RedisService;
import com.study.moya.redis.RedisWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.support.collections.RedisMap;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
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

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;


    private GoogleIdTokenVerifier verifier; // final 제거

    @PostConstruct
    public void init() {
        log.info("Initializing OAuthService with client ID: {}", clientId);
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .setIssuer("https://accounts.google.com") // Google의 issuer 추가
                .build();
    }

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient webClient;
    private final RedisService redisService;
    private final AESConverter aesConverter;
    private final RedisWrapper redisWrapper;


    /**
     * OAuth credential 토큰 검증
     */
    @Transactional
    public MemberAuthResult loginOAuthGoogle(IdTokenRequestDto requestBody) {
        log.info("Authorization Code : {}", requestBody.getAuthCode());
        GoogleIdTokenResponse tokenResponse = getGoogleTokens(requestBody.getAuthCode());

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

        String existingIdentifier = redisService.findIdentifierByEmail(savedMember.getEmail());

        if (existingIdentifier != null) {
            redisWrapper.deleteAllTokens(existingIdentifier);
        }

//        String uniqueIdentifier = generateUniqueIdentifier();
        String encryptedEmail = aesConverter.convertToDatabaseColumn(userInfo.getEmail());

        String jwtAccessToken = jwtTokenProvider.createTokenForOAuth(encryptedEmail);
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(encryptedEmail);

        redisWrapper.saveTokens(savedMember.getEmail(), tokenResponse.getAccessToken());

        return new MemberAuthResult(jwtAccessToken, jwtRefreshToken, savedMember);
    }


    private GoogleIdTokenResponse getGoogleTokens(String authorizationCode) {
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

                Member newMember = Member.createBuilder()
                        .email(idTokenPayload.getEmail())
                        .profileImageUrl(userInfo.getPicture())
                        .nickname(defaultNickname)
                        .roles(Set.of(Role.USER))
                        .status(MemberStatus.ACTIVE)
                        .providerId(idTokenPayload.getSubject())  // 필수값이므로 기본값 설정
                        .accessToken(tokenResponse.getAccessToken())
                        .refreshToken(tokenResponse.getRefreshToken())
                        .termsAgreed(true)
                        .privacyPolicyAgreed(true)
                        .marketingAgreed(true)
                        .build();
                return memberRepository.save(newMember);
            } catch (Exception e) {
                log.error("Failed to create new member", e);
                throw e;
            }
        }

        log.debug("Updating existing member with email: {}", idTokenPayload.getEmail());

        try {
            Member updatedMember = Member.updateBuilder(existingMember)
                    .accessToken(existingMember.getAccessToken())
                    .refreshToken(existingMember.getRefreshToken())
                    .tokenExpirationTime(existingMember.getTokenExpirationTime())
                    .build();
            return memberRepository.save(updatedMember);

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
        String currentIdentifier = jwtTokenProvider.getEmailFromOAuthToken(refreshToken);
        String storedRefreshToken = redisService.getRefreshToken(currentIdentifier);
        String storedEmail = redisService.getEmailByIdentifier(currentIdentifier);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("Expired or invalid refresh token");
        }

        Member member = memberRepository.findByEmail(storedEmail)
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
            String userEmail = jwtTokenProvider.getEmailFromOAuthToken(refreshToken);

            SecurityContextHolder.clearContext();

            // 액세스 토큰 블랙리스트에 추가
            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                long expirationTime = jwtTokenProvider.getExpirationTime(accessToken);
                redisService.addToBlacklist(accessToken, expirationTime);
            }

            // Redis에서 리프레시 토큰 삭제
            redisService.deleteAllTokens(userEmail);

            log.info("User logged out successfully: {}", userEmail);
        } catch (Exception e) {
            log.error("Error during logout process", e);
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


// 개발 임시 중단
//    @Transactional
//    public OAuth2SignupCompleteResponse completeSignup(OAuth2SignupCompleteRequest request) {
//        // 토큰 검증 및 임시 회원 정보 조회
//        OAuthTempMemberInfo tempInfo = validateAndGetTempInfo(request.getToken());
//
//        // 회원가입 요청 유효성 검증
//        signupValidator.validate(request, tempInfo);
//
//        try {
//            // 회원 생성 및 저장
//            Member newMember = memberMapper.toMember(tempInfo, request);
//            Member savedMember = memberRepository.save(newMember);
//
//            // JWT 토큰 생성
//            TokenInfo tokenInfo = createAuthenticationToken(savedMember);
//
//            // Redis 임시 데이터 삭제
//            redisService.deleteTempMemberInfo(request.getToken());
//
//            log.info("OAuth 회원가입 완료 - 이메일: {}", tempInfo.getEmail());
//
//            return OAuth2SignupCompleteResponse.builder()
//                    .status("SUCCESS")
//                    .message("회원가입이 완료되었습니다.")
//                    .email(savedMember.getEmail())
//                    .nickname(savedMember.getNickname())
//                    .roles(savedMember.getRoles())
//                    .accessToken(tokenInfo.getAccessToken())
//                    .build();
//
//        } catch (Exception e) {
//            log.error("회원가입 처리 중 오류 발생 - 이메일: {}", tempInfo.getEmail(), e);
//            throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다.", e);
//        }
//    }
//
//    private OAuthTempMemberInfo validateAndGetTempInfo(String token) {
//        if (!tokenProvider.validateOAuthToken(token)) {
//            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
//        }
//
//        String email = tokenProvider.getEmailFromOAuthToken(token);
//        OAuthTempMemberInfo tempInfo = redisService.getTempMemberInfo(token);
//
//        if (!email.equals(tempInfo.getEmail())) {
//            throw new InvalidTokenException("토큰 정보가 일치하지 않습니다.");
//        }
//        return tempInfo;
//    }

//    private TokenInfo createAuthenticationToken(Member member) {
//        List<GrantedAuthority> authorities = member.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
//                .collect(Collectors.toList());
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                member, null, authorities);
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        return tokenProvider.createToken(authentication);
//    }
//
//    @Transactional(readOnly = true)
//    public void validateNickname(String nickname) {
//        if(memberRepository.existsByNickname(nickname)) {
//            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다 : " + nickname);
//        }
//    }
