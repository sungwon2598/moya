package com.study.moya.global.config.security;

import com.study.moya.Oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.study.moya.Oauth.service.CustomOAuth2UserService;
import com.study.moya.auth.jwt.JwtAuthorizationFilter;
import com.study.moya.auth.jwt.JwtTokenProvider;
import com.study.moya.auth.jwt.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationConfiguration authenticationConfiguration;

//    @Value("${cors.allowed-origins}")
//    private List<String> allowedOrigins;
//
//    @Value("${cors.allowed-methods}")
//    private List<String> allowedMethods;
//
//    @Value("${cors.allowed-headers}")
//    private List<String> allowedHeaders;
//
//    @Value("${cors.allow-credentials}")
//    private boolean allowCredentials;
//
//    @Value("${cors.max-age}")
//    private long maxAge;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomOAuth2UserService customOAuth2UserService,
                                                   OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/**","/api/auth/**", "/css/**", "/js/**", "/*.ico", "/dashboard",
                                "/webjars/**", "/swagger-ui.html", "/swagger-ui/**", "/actuator/**",
                                "/mermaid/**", "/api/mermaid/**",
                                "/api-docs/**", "/v3/api-docs/**", "/result", "/",
                                "/video", "/signal", "/vid", // 추가된 부분
                                "/error", "/roadmap/**", // 에러 페이지도 허용
                                "/whiteboard", " /api/auth/oauth/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/api/auth/login")
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler((request, response, authentication) -> {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"success\": true}");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"success\": false, \"message\": \"" + exception.getMessage() + "\"}");
                        }))

                .oauth2Login(oauth2 ->
                        oauth2.authorizationEndpoint(endpoint ->
                                        endpoint.baseUri("/oauth2/authorization")  // 기본 인증 엔드포인트 URI
                                )
                                .redirectionEndpoint(endpoint ->
                                        endpoint.baseUri("/login/oauth2/code/*")  // 리다이렉션 URI를 Google 콘솔에 등록된 것과 일치하게 수정
                                )
                                .userInfoEndpoint(userInfo ->
                                        userInfo.userService(customOAuth2UserService)
                                )
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                )

                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration),
                                jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of(
                        "https://moyastudy.com",
                        "http://localhost:3000",
                        "http://localhost:3090"
                )
        );
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}