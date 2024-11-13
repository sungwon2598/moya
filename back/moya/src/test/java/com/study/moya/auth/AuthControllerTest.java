package com.study.moya.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.auth.dto.LoginRequest;
import com.study.moya.auth.dto.SignupRequest;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.auth.jwt.JwtTokenProvider.TokenInfo;
import com.study.moya.auth.service.AuthService;
import com.study.moya.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthService authService;

    private SignupRequest validSignupRequest;
    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();

        validSignupRequest = new SignupRequest(
                "test@example.com",
                "Password123!",
                "testUser",
                null,
                "local",
                true,
                true,
                false
        );

        validLoginRequest = new LoginRequest(
                "test@example.com",
                "Password123!"
        );
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void registerUser_Success() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSignupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입이 성공적으로 이루어졌습니다"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 형식 불일치")
    void registerUser_InvalidEmail() throws Exception {
        SignupRequest invalidRequest = new SignupRequest(
                "invalid-email",
                "Password123!",
                "testUser",
                null,
                "local",
                true,
                true,
                false
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_Success() throws Exception {
        TokenInfo tokenInfo = new TokenInfo("access-token", "refresh-token");
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(tokenInfo);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    @DisplayName("토큰 갱신 성공 테스트")
    void refreshToken_Success() throws Exception {
        TokenInfo tokenInfo = new TokenInfo("new-access-token", "new-refresh-token");
        when(authService.refreshToken(anyString())).thenReturn(tokenInfo);

        Cookie refreshTokenCookie = new Cookie("refresh_token", "valid-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(refreshTokenCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_Success() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("refresh_token", 0));
    }
}