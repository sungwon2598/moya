package com.study.moya.global.config.security;

import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestApiKeyAuthFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;
    
    @Value("${spring.profiles.active:}")
    private String activeProfile;
    
    private static final String API_KEY_HEADER = "X-Test-API-Key";
    
    private static final Map<String, String> API_KEY_TO_EMAIL = Map.of(
        "test-admin-key-moya", "test-admin@moyastudy.com",
        "test-user-key-moya", "test-user@moyastudy.com", 
        "frontend-dev-key", "frontend-dev@moyastudy.com"
    );
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
//        //TODO: 추후 서버가 나뉠때 고려하기
//        if (!isTestEnvironment()) {
//            filterChain.doFilter(request, response);
//            return;
//        }
        
        String apiKey = request.getHeader(API_KEY_HEADER);
        
        if (apiKey != null && API_KEY_TO_EMAIL.containsKey(apiKey)) {
            String email = API_KEY_TO_EMAIL.get(apiKey);
            
            Optional<Member> memberOpt = memberRepository.findByEmailWithRoles(email);
            
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                
                log.info("Test API 키 인증 성공: {}, 이메일: {}, 권한: {}", 
                    apiKey, email, member.getRoles());
                
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(
                        member, 
                        null, 
                        member.getAuthorities()
                    );
                
                auth.setDetails(Map.of(
                    "memberId", member.getId(),
                    "testUser", true,
                    "apiKey", apiKey,
                    "email", email
                ));
                
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.warn("Test API 키는 유효하지만 해당 사용자를 찾을 수 없음: {}", email);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isTestEnvironment() {
        return activeProfile.contains("local") || activeProfile.contains("dev") || activeProfile.isEmpty();
    }
}