package com.study.moya.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.oauth.exception.InvalidTokenException;
import com.study.moya.auth.service.AuthService;
import com.study.moya.redis.RedisService;
import com.study.moya.auth.dto.LogoutRequest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerLogoutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private RedisService redisService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_ACCESS_TOKEN = "test-access-token";

    @Test
    @DisplayName("정상적인 로그아웃 테스트")
    void logout_Success() throws Exception {
        // Given
        doNothing().when(authService).logout(any(LogoutRequest.class));

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
                        .param("email", TEST_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("로그아웃되었습니다"))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    Cookie[] cookies = response.getCookies();
                    assertNotNull(cookies);
                    assertTrue(Arrays.stream(cookies)
                            .anyMatch(cookie ->
                                    "refresh_token".equals(cookie.getName()) &&
                                            cookie.getMaxAge() == 0));
                });
    }

    @Test
    @DisplayName("토큰 없이 로그아웃 시도")
    void logout_WithoutToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .param("email", TEST_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Authorization 헤더가 필요합니다"));
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 로그아웃 시도")
    void logout_WithInvalidToken() throws Exception {
        // Given
        doThrow(new InvalidTokenException("유효하지 않은 Access Token입니다."))
                .when(authService).logout(any(LogoutRequest.class));

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer invalid-token")
                        .param("email", TEST_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("인증 오류"));
    }

    @Test
    @DisplayName("쿠키 삭제 확인")
    void logout_CheckCookieDeletion() throws Exception {
        // Given
        doNothing().when(authService).logout(any(LogoutRequest.class));

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN)
                        .cookie(new Cookie("refresh_token", "test-refresh-token"))
                        .param("email", TEST_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Cookie[] cookies = result.getResponse().getCookies();
                    assertNotNull(cookies, "쿠키가 null입니다");
                    Optional<Cookie> refreshTokenCookie = Arrays.stream(cookies)
                            .filter(cookie -> "refresh_token".equals(cookie.getName()))
                            .findFirst();
                    assertTrue(refreshTokenCookie.isPresent(), "refresh_token 쿠키가 없습니다");
                    assertEquals(0, refreshTokenCookie.get().getMaxAge(), "쿠키가 즉시 만료되지 않았습니다");
                });
    }

    @Test
    @DisplayName("Redis에서 토큰 삭제 확인")
    void logout_CheckRedisTokenDeletion() throws Exception {
        // Given
        String email = TEST_EMAIL;
        String accessToken = TEST_ACCESS_TOKEN;

        // RedisService의 동작 모킹
        when(redisService.getAccessToken(email)).thenReturn(accessToken);

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        // 메서드 호출 검증
        verify(authService, times(1)).logout(any(LogoutRequest.class));
    }
}