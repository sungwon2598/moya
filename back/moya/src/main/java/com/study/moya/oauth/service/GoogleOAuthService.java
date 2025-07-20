//package com.study.moya.oauth.service;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.gson.GsonFactory;
//import com.study.moya.oauth.dto.user.GoogleUserInfo;
//import com.study.moya.oauth.dto.auth.OAuthTokenResponse;
//import com.study.moya.oauth.exception.OAuthErrorCode;
//import com.study.moya.oauth.exception.OAuthException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.util.Collections;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class GoogleOAuthService {
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String clientId;
//
//    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
//    private String clientSecret;
//
//    private final WebClient webClient;
//
//    public OAuthTokenResponse getGoogleTokens(String authorizationCode, String redirectUri) {
//        try {
//            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//            formData.add("code", authorizationCode);
//            formData.add("client_id", clientId);
//            formData.add("client_secret", clientSecret);
//            formData.add("redirect_uri", redirectUri);
//            formData.add("grant_type", "authorization_code");
//
//            return webClient.post()
//                    .uri("https://oauth2.googleapis.com/token")
//                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                    .body(BodyInserters.fromFormData(formData))
//                    .retrieve()
//                    .bodyToMono(OAuthTokenResponse.class)
//                    .block();
//        } catch (Exception e){
//            log.error("Failed to get Google tokens", e);
//            throw OAuthException.of(OAuthErrorCode.INVALID_AUTH_CODE);
//        }
//    }
//
//    /**
//     * Access Token으로 Google 사용자 정보 가져오기
//     */
//    public GoogleUserInfo getGoogleUserInfo(String accessToken) {
//        try {
//            return webClient.get()
//                    .uri("https://www.googleapis.com/oauth2/v2/userinfo")
//                    .header("Authorization", "Bearer " + accessToken)
//                    .retrieve()
//                    .bodyToMono(GoogleUserInfo.class)
//                    .block();
//        } catch (Exception e) {
//            log.error("Failed to get Google user info", e);
//            throw OAuthException.of(OAuthErrorCode.INVALID_ACCESS_TOKEN);
//        }
//    }
//
//    /**
//     * ID Token 검증 및 Payload 추출
//     */
//    public GoogleIdToken.Payload verifyAndGetIdToken(String idToken){
//        try {
//            log.debug("Verifying ID token: {}", idToken);
//            if (idToken == null || idToken.trim().isEmpty()) {
//                log.error("ID token is null or empty");
//                throw OAuthException.of(OAuthErrorCode.INVALID_ID_TOKEN);
//            }
//
//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
//                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
//                    .setAudience(Collections.singletonList(clientId))
//                    .setIssuer("https://accounts.google.com")
//                    .build();
//
//            GoogleIdToken googleIdToken = verifier.verify(idToken);
//            return googleIdToken.getPayload();
//        } catch (Exception e) {
//            log.error("Failed to verify ID token: {}", e.getMessage(), e);
//            throw OAuthException.of(OAuthErrorCode.INVALID_ID_TOKEN);
//        }
//    }
//
//}
