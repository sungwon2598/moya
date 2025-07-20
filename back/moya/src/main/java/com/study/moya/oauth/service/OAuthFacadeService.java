//package com.study.moya.oauth.service;
//
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.study.moya.auth.jwt.JwtTokenProvider;
//import com.study.moya.member.domain.Member;
//import com.study.moya.member.repository.MemberRepository;
//import com.study.moya.oauth.dto.auth.OAuthTokenResponse;
//import com.study.moya.oauth.dto.user.GoogleUserInfo;
//import com.study.moya.oauth.dto.auth.OAuthLoginRequest;
//import com.study.moya.oauth.dto.member.MemberAuthResponse;
//import com.study.moya.oauth.exception.OAuthErrorCode;
//import com.study.moya.oauth.exception.OAuthException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class OAuthFacadeService {
//   private final MemberRepository memberRepository;
//   private final JwtTokenProvider jwtTokenProvider;
//   private final GoogleOAuthService googleOAuthService;
//   private final MemberOAuthService memberOAuthService;
//
//    /**
//     * OAuth credential 토큰 검증
//     */
//    @Transactional
//    public MemberAuthResponse loginOAuthGoogle(OAuthLoginRequest requestBody) {
//        log.info("Authorization Code: {}", requestBody.getAuthCode());
//        try {
//            OAuthTokenResponse tokenResponse = googleOAuthService.getGoogleTokens(requestBody.getAuthCode(), requestBody.getRedirectUri());
//            GoogleIdToken.Payload idTokenPayload = googleOAuthService.verifyAndGetIdToken(tokenResponse.getIdToken());
//
//            Member savedMember = memberOAuthService.createOrUpdateMember(idTokenPayload, tokenResponse);
//
//            List<GrantedAuthority> authorities = savedMember.getAuthorities().stream()
//                    .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
//                    .collect(Collectors.toList());
//
//            Authentication authentication = new UsernamePasswordAuthenticationToken(
//                    savedMember,
//                    null,
//                    authorities
//            );
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            JwtTokenProvider.TokenInfo tokenInfo = jwtTokenProvider.createToken(authentication);
//            String memberId = String.valueOf(savedMember.getId());
//
//            savedMember.updateJwtRefreshToken(tokenInfo.getRefreshToken());
//            memberRepository.save(savedMember);
//            //임시방책 redis
////            String existingIdentifier = redisService.findIdentifierByMemberId(memberId);
//
//
//            /**
//             * redis 오류로 인한 임시
//             */
////            if (existingIdentifier != null) {
////                redisService.deleteRefreshToken(memberId);
////            }
////            redisService.saveTokens(memberId, tokenInfo.getRefreshToken())
//
//            return new MemberAuthResponse(tokenInfo.getAccessToken(), tokenInfo.getRefreshToken(), savedMember);
//        } catch (Exception e){
//            log.error("Failed to process OAuth Google login", e);
//            throw OAuthException.of(OAuthErrorCode.GOOGLE_API_ERROR);
//        }
//    }
//}