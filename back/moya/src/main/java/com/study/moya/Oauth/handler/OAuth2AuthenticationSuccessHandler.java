package com.study.moya.Oauth.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.Oauth.dto.OAuthUserInfo;
import com.study.moya.Oauth.dto.OauthLoginResponse;
import com.study.moya.Oauth.service.CustomOAuth2UserService;
import com.study.moya.Oauth.service.CustomOAuth2UserService.OAuth2UserImpl;
import com.study.moya.Oauth.service.OauthService;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.member.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OauthService oauthService;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(
                "google", authentication.getName());

        if (authorizedClient == null) {
            log.warn("Authorized client not found for user: {}", authentication.getName());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

        log.info("OAuth 토큰 정보 - Access Token: {}, User: {}",
                accessToken.getTokenValue(), authentication.getName());

        // OAuth 정보로 OAuthUserInfo 생성
        OAuthUserInfo userInfo = OAuthUserInfo.builder()
                .email(oAuth2User.getAttribute("email"))
                .providerId(oAuth2User.getAttribute("sub"))
                .profileImageUrl(oAuth2User.getAttribute("picture"))
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken != null ? refreshToken.getTokenValue() : null)
                .tokenExpirationTime(accessToken.getExpiresAt())
                .build();

        OauthLoginResponse loginResponse = oauthService.processOAuthLogin(userInfo);

        // JWT 쿠키 설정
        addTokenCookie(response, loginResponse.getAccessToken());

        //API 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
    }

    private void addTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String buildRedirectUri(OauthLoginResponse loginResponse) throws JsonProcessingException {
        return UriComponentsBuilder
                .fromUriString("https://moyastudy.com")
                .queryParam("data", URLEncoder.encode(
                        objectMapper.writeValueAsString(loginResponse),
                        StandardCharsets.UTF_8
                ))
                .build()
                .toUriString();
    }
}
