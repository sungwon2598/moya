package com.study.moya.auth.service;

import com.study.moya.auth.dto.UserInfoResponse;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.auth.dto.LoginRequest;
import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;

    public TokenInfo authenticateUser(LoginRequest loginRequest) {
        log.info("인증 프로세스 시작 - 이메일: {}", loginRequest.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );
        log.info("인증 매니저 인증 완료");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("SecurityContext에 인증 정보 저장 완료");

        TokenInfo tokenInfo = tokenProvider.createToken(authentication);
        log.info("JWT 토큰 생성 완료");

        return tokenInfo;
    }

    public TokenInfo refreshToken(String refreshToken) {
        log.info("토큰 갱신 프로세스 시작");
        return tokenProvider.refreshAccessToken(refreshToken);
    }

    public UserInfoResponse getUserInfo(Authentication authentication) {
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return new UserInfoResponse(
                member.getNickname(),
                member.getEmail(),
                member.getRoles()
        );
    }
}