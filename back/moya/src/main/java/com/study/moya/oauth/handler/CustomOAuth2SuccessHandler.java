package com.study.moya.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.auth.repository.RefreshTokenRepository;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.oauth.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final CookieUtils cookieUtils;

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

            // JWT 토큰 생성 (이미 DB에 refresh token 저장됨)
            JwtTokenProvider.TokenInfo tokenInfo = jwtTokenProvider.createToken(memberAuth);
            log.info("JWT 토큰 생성 및 DB 저장 완료 - 회원 ID: {}", member.getId());

            // 회원 정보 저장 (로그인 정보 업데이트만)
            memberRepository.save(member);
            log.info("회원 정보 업데이트 완료 - 회원 ID: {}", member.getId());

            // OAuth2는 운영환경에서만 사용 (로컬 테스트는 기본 로그인 사용)
            cookieUtils.setProductionCookies(response, tokenInfo);
            log.info("OAuth 운영용 쿠키 설정 완료");

            // 운영 사이트로 리다이렉트
            String redirectUrl = "https://moyastudy.com/";
            log.info("리다이렉트 URL: {}", redirectUrl);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            
            log.info("JWT 토큰을 쿠키에 저장 완료 - 회원 ID: {}, accessToken만료: {}초, refreshToken만료: {}초", 
                    member.getId(), accessTokenExpiration / 1000, refreshTokenExpiration / 1000);

            log.info("OAuth 로그인 성공 응답 전송 완료 - 회원: {}", email);

        } catch (Exception e) {
            log.error("OAuth 인증 성공 처리 중 오류 발생", e);

            // 에러 발생 시 운영 사이트 에러 페이지로 리다이렉트
            String errorUrl = "https://moyastudy.com/?auth=error";
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
}