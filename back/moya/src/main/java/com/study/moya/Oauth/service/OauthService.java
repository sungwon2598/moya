package com.study.moya.Oauth.service;


import com.study.moya.auth.dto.LoginRequest;
import com.study.moya.auth.dto.UserInfoResponse;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import com.study.moya.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public TokenInfo authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.createToken(authentication);
    }

    public UserInfoResponse getUserInfo(Authentication authentication){
        Member member = (Member) authentication.getPrincipal();

        return new UserInfoResponse(
                member.getNickname(),
                member.getEmail(),
                member.getRoles()
        );
    }


}
