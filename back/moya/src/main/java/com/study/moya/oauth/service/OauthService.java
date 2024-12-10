package com.study.moya.oauth.service;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.study.moya.oauth.dto.*;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.domain.Role;
import com.study.moya.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;


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


    /**
     * OAuth credential 토큰 검증
     */
    @Transactional
    public String loginOAuthGoogle(IdTokenRequestDto requestBody) {
        Member verifiedMember = verifyIDToken(requestBody.getCredential());
        if (verifiedMember == null) {
            throw new IllegalArgumentException("Google ID 토큰 검증 실패");
        }
        Member savedMember = createOrUpdateMember(verifiedMember);

        return jwtTokenProvider.createTokenForOAuth(savedMember.getEmail());
    }

    /**
     * 신규 회원 여부 검사 메서드
     * nickname -> email에 '@gmail.com' 제외한 부분
     */
    @Transactional
    public Member createOrUpdateMember(Member member) {
        log.debug("Attempting to create or update member with email: {}", member.getEmail());

        Member existingMember = memberRepository.findByEmail(member.getEmail()).orElse(null);

        if (existingMember == null) {
            log.info("Creating new member with email: {}", member.getEmail());

            try {
                String defaultNickname = member.getEmail().split("@")[0];

                Member newMember = Member.createBuilder()
                        .email(member.getEmail())
                        .profileImageUrl(member.getProfileImageUrl())
                        .nickname(defaultNickname)
                        .roles(Set.of(Role.USER))
                        .status(MemberStatus.ACTIVE)
                        .providerId("NONE")  // 필수값이므로 기본값 설정
                        .accessToken(member.getAccessToken())
                        .refreshToken(member.getRefreshToken())
                        .termsAgreed(true)
                        .privacyPolicyAgreed(true)
                        .marketingAgreed(true)
                        .build();
                log.info("accessToken : {}", member.getAccessToken());
                return memberRepository.save(newMember);
            } catch (Exception e) {
                log.error("Failed to create new member", e);
                throw e;
            }
        }

        log.debug("Updating existing member with email: {}", member.getEmail());

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

    private Member verifyIDToken(String idToken) {
        try {
            log.debug("Verifying ID token: {}", idToken);

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(clientId))
                    .setIssuer("https://accounts.google.com")
                    .build();

            if (idToken == null || idToken.trim().isEmpty()) {
                log.error("ID token is null or empty");
                throw new IllegalArgumentException("ID token cannot be null or empty");
            }

            GoogleIdToken idTokenObj = verifier.verify(idToken);
            if (idTokenObj == null) {
                log.error("Failed to verify Google ID token");
                throw new IllegalArgumentException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idTokenObj.getPayload();
            log.info("Successfully verified token for email: {}", payload.getEmail());

// 개발중............
//            OAuthAccessToken accessToken = getPeopleApiAccessToken(idToken);
//            log.info("accessToken in verify : {}", accessToken);

            String email = payload.getEmail();
            String picture = (String) payload.get("picture");
            String name = (String) payload.get("name");
            String sub = payload.getSubject();

            return Member.createBuilder()
                    .email(email)
                    .profileImageUrl(picture)
                    .nickname("hello")
                    .providerId(sub)
                    .status(MemberStatus.ACTIVE)
                    .roles(Set.of(Role.USER))
//                    .accessToken(accessToken.getOAuthAccessToken())
                    .termsAgreed(true)
                    .privacyPolicyAgreed(true)
                    .marketingAgreed(true)
                    .build();

        } catch (GeneralSecurityException e) {
            log.error("Security error during token verification", e);
            throw new IllegalArgumentException("Security error during token verification", e);
        } catch (IOException e) {
            log.error("IO error during token verification", e);
            throw new IllegalArgumentException("IO error during token verification", e);
        } catch (Exception e) {
            log.error("Unexpected error during token verification: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Token verification failed", e);
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

    /**
     * credential 토큰 검증 메서드
     */

//    private OAuthAccessToken getPeopleApiAccessToken(String idToken) {
//
//        String decode = URLDecoder.decode(idToken, StandardCharsets.UTF_8);
//        String tokenUrl = "https://oauth2.googleapis.com/token";
//        MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
//        tokenRequest.add("grant_type", "authorization_code");
//        tokenRequest.add("code", decode);
//        tokenRequest.add("client_id", clientId);
//        tokenRequest.add("client_secret", clientSecret);
//        tokenRequest.add("redirect_uri", redirectUri);
//
//        try {
//            GoogleAccessToken googleAccessToken = webClient.post()
//                    .uri(tokenUrl)
//                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                    .accept(MediaType.APPLICATION_JSON)
//                    .bodyValue(tokenRequest)
//                    .retrieve()
//                    .bodyToMono(GoogleAccessToken.class)
//                    .blockOptional()
//                    .orElseThrow(() -> new RuntimeException("Access token retrieval failed"));
//
//
//        log.info("Access token retrieved successfully: {}", googleAccessToken.getAccessToken());
//        return googleAccessToken.toEntity();
//
//        } catch (WebClientResponseException ex) {
//            log.error("Error response from token endpoint: {}", ex.getResponseBodyAsString());
//            throw new RuntimeException("Access token retrieval failed", ex);
//        } catch (Exception e) {
//            log.error("Unexpected error during access token retrieval", e);
//            throw new RuntimeException("Failed to get access token", e);
//        }
//    }
}
