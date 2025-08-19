package com.study.moya.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;
    
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = (String) oAuth2User.getAttributes().get("email");

            log.info("OAuth 인증 성공 - 이메일: {}", email);

            // 이메일로 회원 조회
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("OAuth 인증 후 회원 조회 실패 - 이메일: {}", email);
                        return new RuntimeException("Member not found: " + email);
                    });

            log.info("회원 조회 완료 - ID: {}, 이메일: {}, 닉네임: {}",
                    member.getId(), member.getEmail(), member.getNickname());

            // OAuth 인증 성공 시 필요한 모든 정보 업데이트
            member.updateLoginInfo(); // 로그인 정보 업데이트 (마지막 로그인 시간, 상태 등)

            // Member로 새로운 Authentication 객체 생성
            UsernamePasswordAuthenticationToken memberAuth =
                    new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());

            // JWT 토큰 생성
            JwtTokenProvider.TokenInfo tokenInfo = jwtTokenProvider.createToken(memberAuth);
            log.info("JWT 토큰 생성 완료 - 회원 ID: {}", member.getId());

            // JWT refresh token 업데이트 및 모든 변경사항을 한 번에 저장
            member.updateJwtRefreshToken(tokenInfo.getRefreshToken());
            memberRepository.save(member);
            log.info("회원 정보 업데이트 완료 - 회원 ID: {}", member.getId());

            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", tokenInfo.getAccessToken())
                    .domain(".moyastudy.com")  // ResponseCookie는 점 도메인 허용
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(accessTokenExpiration / 1000)
                    .sameSite("Lax")
                    .build();

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenInfo.getRefreshToken())
                    .domain(".moyastudy.com")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(refreshTokenExpiration / 1000)
                    .sameSite("Lax")
                    .build();

            response.addHeader("Set-Cookie", accessTokenCookie.toString());
            response.addHeader("Set-Cookie", refreshTokenCookie.toString());
            
            log.info("JWT 토큰을 쿠키에 저장 완료 - 회원 ID: {}, accessToken만료: {}초, refreshToken만료: {}초", 
                    member.getId(), accessTokenExpiration / 1000, refreshTokenExpiration / 1000);

            // 프론트엔드로 리다이렉트 (토큰 정보 없이)
            String redirectUrl = "https://moyastudy.com/";
            log.info("OAuth 로그인 성공 - 리다이렉트 URL: {}", redirectUrl);

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

            log.info("OAuth 로그인 성공 응답 전송 완료 - 회원: {}", email);

        } catch (Exception e) {
            log.error("OAuth 인증 성공 처리 중 오류 발생", e);

            // 에러 발생 시 에러 페이지로 리다이렉트
            String errorUrl = "https://moyastudy.com/?auth=error";
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
}