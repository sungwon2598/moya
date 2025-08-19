package com.study.moya.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    // 프론트엔드 URL 설정 (환경에 따라 변경)
    //private static final String FRONTEND_URL = "http://localhost:3000"; // 개발환경
    private static final String FRONTEND_URL = "https://moyastudy.com"; // 운영환경

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

            // 토큰 정보를 URL 파라미터로 전달하여 프론트엔드로 리다이렉트
            String redirectUrl = UriComponentsBuilder.fromUriString(FRONTEND_URL + "/oauth/callback")
                    .queryParam("accessToken", tokenInfo.getAccessToken())
                    .queryParam("refreshToken", tokenInfo.getRefreshToken())
                    .queryParam("email", URLEncoder.encode(member.getEmail(), StandardCharsets.UTF_8))
                    .queryParam("nickname", URLEncoder.encode(member.getNickname(), StandardCharsets.UTF_8))
                    .queryParam("profileImage", URLEncoder.encode(member.getProfileImageUrl() != null ? member.getProfileImageUrl() : "", StandardCharsets.UTF_8))
                    .build()
                    .toUriString();

            log.info("OAuth 로그인 성공 - 리다이렉트 URL: {}", redirectUrl);

            // 프론트엔드로 리다이렉트
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

            log.info("OAuth 로그인 성공 응답 전송 완료 - 회원: {}", email);

        } catch (Exception e) {
            log.error("OAuth 인증 성공 처리 중 오류 발생", e);

            // 에러 발생 시 에러 페이지로 리다이렉트
            String errorUrl = UriComponentsBuilder.fromUriString(FRONTEND_URL + "/oauth/error")
                    .queryParam("message", "로그인 처리 중 오류가 발생했습니다.")
                    .build()
                    .toUriString();
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
}