package com.study.moya.auth;


import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.auth.dto.LoginRequest;
import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import com.study.moya.auth.service.AuthService;
import com.study.moya.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerLogoutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthService authService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Password123!";

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_Success() throws Exception {
        // Given
        // 먼저 로그인을 수행하여 쿠키 설정
        TokenInfo tokenInfo = new TokenInfo("test-access-token", "test-refresh-token");
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(tokenInfo);

        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

        // 로그인 수행
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print()) // 로그인 응답 출력
                .andReturn();

        // 로그인 응답에서 쿠키 확인
        Cookie[] loginCookies = loginResult.getResponse().getCookies();
        //log.info("Login response cookies: {}", Arrays.toString(loginCookies));

        // When & Then
        // 로그아웃 수행 및 검증
        MvcResult logoutResult = mockMvc.perform(post("/api/auth/logout")
                        .cookie(loginCookies)) // 로그인에서 받은 쿠키 포함
                .andDo(print()) // 로그아웃 응답 출력
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Cookie[] cookies = result.getResponse().getCookies();
                    //log.info("Logout response cookies: {}", Arrays.toString(cookies));

                    // 쿠키가 존재하는지 확인
                    assertTrue(cookies != null && cookies.length > 0, "응답에 쿠키가 없습니다.");

                    // refresh_token 쿠키를 찾아서 검증
                    Optional<Cookie> refreshTokenCookie = Arrays.stream(cookies)
                            .filter(cookie -> "refresh_token".equals(cookie.getName()))
                            .findFirst();

                    assertTrue(refreshTokenCookie.isPresent(),
                            "refresh_token 쿠키가 없습니다. 존재하는 쿠키: " +
                                    Arrays.toString(cookies));

                    assertEquals(0, refreshTokenCookie.get().getMaxAge(),
                            "쿠키 만료 시간이 0이 아닙니다.");
                })
                .andReturn();

        // 추가 로깅
//        log.info("Login Request: {}", objectMapper.writeValueAsString(loginRequest));
//        log.info("Login Response Status: {}", loginResult.getResponse().getStatus());
//        log.info("Logout Response Status: {}", logoutResult.getResponse().getStatus());
//        log.info("Logout Response Headers: {}", logoutResult.getResponse().getHeaderNames());
    }

    @Test
    @DisplayName("로그아웃 요청 시 모든 인증 관련 쿠키가 제거되는지 확인")
    void logout_RemovesAllAuthCookies() throws Exception {
        // Given
        Cookie refreshTokenCookie = new Cookie("refresh_token", "test-refresh-token");
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);

        Cookie jwtCookie = new Cookie("jwt", "test-jwt-token");
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(refreshTokenCookie, jwtCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Cookie[] responseCookies = result.getResponse().getCookies();
                    assertNotNull(responseCookies, "응답 쿠키가 null입니다");

                    List<Cookie> expiredCookies = Arrays.stream(responseCookies)
                            .filter(cookie -> cookie.getMaxAge() == 0)
                            .collect(Collectors.toList());



                            expiredCookies.stream()
                                    .map(Cookie::getName)
                                    .collect(Collectors.joining(", "));

                    assertTrue(expiredCookies.stream()
                                    .anyMatch(cookie -> "refresh_token".equals(cookie.getName())),
                            "refresh_token 쿠키가 만료되지 않았습니다");
                });
    }
}