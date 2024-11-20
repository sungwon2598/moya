package com.study.moya.Oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.Oauth.dto.OauthLoginResponse;
import com.study.moya.Oauth.service.CustomOAuth2UserService;
import com.study.moya.Oauth.service.CustomOAuth2UserService.OAuth2UserImpl;
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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient("google",
                authentication.getName());
        if (authorizedClient != null) {
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

            logger.info("Access Token: " + accessToken.getTokenValue());
            if (refreshToken != null) {
                logger.info("Refresh Token: " + refreshToken.getTokenValue());
            } else {
                logger.warn("Refresh Token is null");
            }

            TokenInfo jwt = jwtTokenProvider.createToken(authentication);
            logger.info("Created AccessJWT: " + jwt.getAccessToken());
            logger.info("Created RefreshJWT: " + jwt.getRefreshToken());
            logger.info("User: " + authentication.getName());

            //사용자 정보 추출
            OAuth2UserImpl oAuth2User = (OAuth2UserImpl) authentication.getPrincipal();
            String email = oAuth2User.getEmail();
            String providerId = oAuth2User.getProviderId();

            //DB에 회원 정보 저장/없데이트
            boolean isNewUser = memberService.createOrUpdateOAuthMember(
                    email,
                    providerId,
                    oAuth2User.getAccessToken(),
                    refreshToken != null ? refreshToken.getTokenValue() : null,
                    accessToken.getExpiresAt(),
                    oAuth2User.getProfileImageUrl()
            );

            // 프론트엔드로 전달할 응답 데이터 생성
            OauthLoginResponse loginResponse = OauthLoginResponse.builder()
                    .accessToken(jwt.getAccessToken())
                    .refreshToken(jwt.getRefreshToken())
                    .isNewUser(isNewUser)
                    .nextStep(isNewUser ? "TERMS_NICKNAME" : "MAIN")
                    .build();

            // JWT를 쿠키에 저장
            Cookie cookie = new Cookie("jwt", jwt.getAccessToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            // 프론트엔드로 보내는 리다이렉트
            String redirectUri = UriComponentsBuilder
                    .fromUriString("https://moyastudy.com")
                            .queryParam("data", URLEncoder.encode(
                                    objectMapper.writeValueAsString(loginResponse),
                                    StandardCharsets.UTF_8
                                    )
                            )
                    .build()
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, redirectUri);
        } else {
            logger.warn("Authorized client not found for user: " + authentication.getName());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

        }
    }
}
