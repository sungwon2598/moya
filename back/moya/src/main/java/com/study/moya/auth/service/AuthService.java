package com.study.moya.auth.service;

import com.study.moya.Oauth.exception.InvalidTokenException;
import com.study.moya.auth.dto.LoginRequest;
import com.study.moya.auth.dto.LogoutRequest;
import com.study.moya.auth.dto.UserInfoResponse;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import com.study.moya.auth.repository.RefreshTokenRepository;
import com.study.moya.member.constants.MemberErrorCode;
import com.study.moya.member.domain.Member;
import com.study.moya.member.exception.MemberException;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;


    @Transactional
    public TokenInfo authenticateUser(LoginRequest loginRequest) {
        log.info("인증 프로세스 시작 - 이메일: {}", loginRequest.email());

        // 1. 먼저 회원 존재 여부와 로그인 가능 상태 확인
        Member member = memberRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!member.isLoginable()) {
            log.warn("로그인 불가능한 상태의 계정 접근 시도 - 이메일: {}, 상태: {}",
                    loginRequest.email(), member.getStatus());
            throw new MemberException(MemberErrorCode.MEMBER_NOT_LOGINABLE);
        }

        // 2. Spring Security 인증 처리
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );
        } catch (BadCredentialsException e) {
            log.warn("잘못된 인증 정보 - 이메일: {}", loginRequest.email());
            throw e;
        }
        log.info("인증 매니저 인증 완료");

        // 3. 로그인 성공 시 회원 정보 업데이트
        member.updateLoginInfo();
        log.info("회원 로그인 정보 업데이트 완료");

        // 4. Security Context에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("SecurityContext에 인증 정보 저장 완료");

        // 5. JWT 토큰 생성
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

    @Transactional
    public void logout(LogoutRequest request) {
        log.info("모든 토큰 무효화 프로세스 시작 - email: {}", request.getEmail());
        try {
            String savedAccessToken = redisService.getAccessToken(request.getEmail());

            if (!isValidTokenPair(request.getAccessToken(), savedAccessToken)) {
                log.warn("Access Token 불일치 - email: {}", request.getEmail());
                throw new InvalidTokenException("유효하지 않은 Access Token 입니다");
            }
            redisService.deleteAllTokens(request.getAccessToken());
            log.info("모든 토큰이 삭제되었습니다 - email: {}", request.getAccessToken());
        } catch (Exception e) {
            log.error("로그아웃 처리 중 요류 발생 - email: {}", request.getAccessToken(), e);
            throw new RuntimeException("로그아웃 처리 중 오류가 발생했습니다.", e);
        }
    }
    /**
     * 토큰 유효성 검증 메서드
     */
    private boolean isValidTokenPair (String providedToken, String savedToken){
        if (providedToken == null || savedToken == null) {
            return false;
        }
        return providedToken.equals(savedToken);
    }
}
