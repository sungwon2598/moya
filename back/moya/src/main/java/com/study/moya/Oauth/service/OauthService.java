package com.study.moya.Oauth.service;


import com.study.moya.Oauth.dto.*;
import com.study.moya.Oauth.exception.DuplicateNicknameException;
import com.study.moya.Oauth.exception.InvalidTokenException;
import com.study.moya.Oauth.mapper.MemberMapper;
import com.study.moya.Oauth.validator.SignupValidator;
import com.study.moya.auth.dto.LoginRequest;
import com.study.moya.auth.dto.UserInfoResponse;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class OauthService {
    private final JwtTokenProvider tokenProvider;
    private final RedisService redisService;
    private final MemberMapper memberMapper;
    private final SignupValidator signupValidator;
    private final MemberRepository memberRepository;

    @Transactional
    public OauthLoginResponse processOAuthLogin(OAuthUserInfo userInfo) {
        log.info("OAuth 로그인 처리 시작 - 이메일: {}", userInfo.getEmail());

        boolean isNewUser = !memberRepository.existsByEmail(userInfo.getEmail());

        if (isNewUser) {
            return handleNewUser(userInfo);
        } else {
            return handleExistingUser(userInfo);
        }
    }

    private OauthLoginResponse handleNewUser(OAuthUserInfo userInfo) {
        log.info("신규 회원 처리 - 이메일: {}", userInfo.getEmail());

        String temporaryToken = tokenProvider.createTokenForOAuth(userInfo.getEmail());
        redisService.saveTempMemberInfo(temporaryToken, OAuthTempMemberInfo.from(userInfo));

        return OauthLoginResponse.builder()
                .accessToken(temporaryToken)
                .isNewUser(true)
                .nextStep("SIGNUP")
                .build();
    }

    private OauthLoginResponse handleExistingUser(OAuthUserInfo userInfo) {
        log.info("기존 회원 처리 - 이메일: {}", userInfo.getEmail());

        Member member = memberRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(userInfo.getEmail()));

        member.updateTokenInfo(
                userInfo.getAccessToken(),
                userInfo.getRefreshToken(),
                userInfo.getTokenExpirationTime()
        );

        memberRepository.save(member);

        TokenInfo tokenInfo = tokenProvider.createToken(
                new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities())
        );

        return OauthLoginResponse.builder()
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .isNewUser(false)
                .nextStep("MAIN")
                .build();
    }

    @Transactional
    public OAuth2SignupCompleteResponse completeSignup(OAuth2SignupCompleteRequest request) {
        // 토큰 검증 및 임시 회원 정보 조회
        OAuthTempMemberInfo tempInfo = validateAndGetTempInfo(request.getToken());

        // 회원가입 요청 유효성 검증
        signupValidator.validate(request, tempInfo);

        try {
            // 회원 생성 및 저장
            Member newMember = memberMapper.toMember(tempInfo, request);
            Member savedMember = memberRepository.save(newMember);

            // JWT 토큰 생성
            TokenInfo tokenInfo = createAuthenticationToken(savedMember);

            // Redis 임시 데이터 삭제
            redisService.deleteTempMemberInfo(request.getToken());

            log.info("OAuth 회원가입 완료 - 이메일: {}", tempInfo.getEmail());

            return OAuth2SignupCompleteResponse.builder()
                    .status("SUCCESS")
                    .message("회원가입이 완료되었습니다.")
                    .email(savedMember.getEmail())
                    .nickname(savedMember.getNickname())
                    .roles(savedMember.getRoles())
                    .accessToken(tokenInfo.getAccessToken())
                    .build();

        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생 - 이메일: {}", tempInfo.getEmail(), e);
            throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다.", e);
        }
    }

    private OAuthTempMemberInfo validateAndGetTempInfo(String token) {
        if (!tokenProvider.validateOAuthToken(token)) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }

        String email = tokenProvider.getEmailFromOAuthToken(token);
        OAuthTempMemberInfo tempInfo = redisService.getTempMemberInfo(token);

        if (!email.equals(tempInfo.getEmail())) {
            throw new InvalidTokenException("토큰 정보가 일치하지 않습니다.");
        }

        return tempInfo;
    }

    private TokenInfo createAuthenticationToken(Member member) {
        List<GrantedAuthority> authorities = member.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.createToken(authentication);
    }

    @Transactional(readOnly = true)
    public void validateNickname(String nickname) {
        if(memberRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다 : " + nickname);
        }
    }
}